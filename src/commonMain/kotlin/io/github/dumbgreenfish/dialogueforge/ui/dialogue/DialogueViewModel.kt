package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.DialogueRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.SettingsRepository
import io.github.dumbgreenfish.dialogueforge.data.service.LlmService
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.toCharacter
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.toMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

private const val PAGE_SIZE = 50

@KoinViewModel
class DialogueViewModel(
    private val characterRepository: CharacterRepository,
    private val dialogueRepository: DialogueRepository,
    private val llmService: LlmService,
    private val settingsRepository: SettingsRepository,
    @InjectedParam private val clipboardManager: ClipboardManager,
) : ViewModel() {
    private val _state = MutableStateFlow(DialogueState())
    val state: StateFlow<DialogueState> = _state.asStateFlow()

    private var generationJob: Job? = null
    private var totalMessageCount: Int = 0

    fun handle(intent: DialogueIntent) {
        when (intent) {
            is DialogueIntent.LoadCharacter -> loadCharacter(intent.id)
            is DialogueIntent.Back -> {}
            is DialogueIntent.UpdateInput -> _state.update { it.copy(inputText = intent.value) }
            is DialogueIntent.Send -> onSend()
            is DialogueIntent.StopGeneration -> stopGeneration()
            is DialogueIntent.DeleteMessage -> deleteMessage(intent.messageId)
            is DialogueIntent.LoadOlderMessages -> loadOlderMessages()
            is DialogueIntent.ToggleActions -> toggleActions(intent.messageId)
            is DialogueIntent.StartEdit -> startEdit(intent.messageId)
            is DialogueIntent.UpdateEditText -> _state.update { it.copy(editingText = intent.value) }
            is DialogueIntent.SaveEdit -> saveEdit()
            is DialogueIntent.CancelEdit -> _state.update { it.copy(editingMessageId = null, editingText = TextFieldValue()) }
            is DialogueIntent.CopyMessage -> copyMessage(intent.messageId)
            is DialogueIntent.ToggleSelection -> toggleSelection(intent.messageId)
            is DialogueIntent.ClearSelection -> _state.update { it.copy(selectedMessageIds = emptySet()) }
            is DialogueIntent.DeleteSelected -> deleteSelected()
            is DialogueIntent.CopySelected -> copySelected()
        }
    }

    private fun loadCharacter(id: String) {
        if (_state.value.isLoading) return
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val entity = characterRepository.getById(id)
            val character = checkNotNull(entity?.toCharacter()) { "Character not found: $id" }
            val modelName = settingsRepository.getModel()
            val conversation = dialogueRepository.getOrCreateConversation(
                characterId = character.id,
                greeting = character.firstMessage,
            )
            totalMessageCount = dialogueRepository.getMessageCount(conversation.id)
            val page = dialogueRepository.getMessagesPage(conversation.id, PAGE_SIZE, 0)
            val messages = page.map { it.toMessage() }
            _state.update {
                it.copy(
                    character = character,
                    isLoading = false,
                    modelName = modelName,
                    conversationId = conversation.id,
                    messages = messages,
                    hasMoreOlderMessages = page.size < totalMessageCount,
                )
            }
        }
    }

    private fun loadOlderMessages() {
        val conversationId = _state.value.conversationId ?: return
        if (_state.value.isLoadingOlder || !_state.value.hasMoreOlderMessages) return
        _state.update { it.copy(isLoadingOlder = true) }
        viewModelScope.launch {
            val offset = _state.value.messages.size
            val page = dialogueRepository.getMessagesPage(conversationId, PAGE_SIZE, offset)
            val mapped = page.map { it.toMessage() }
            _state.update { current ->
                val merged = current.messages + mapped
                current.copy(
                    messages = merged,
                    isLoadingOlder = false,
                    hasMoreOlderMessages = merged.size < totalMessageCount,
                )
            }
        }
    }

    private fun onSend() {
        val text = _state.value.inputText.text.trim()
        if (text.isEmpty()) return
        val conversationId = _state.value.conversationId ?: return
        val character = _state.value.character ?: return
        _state.update {
            it.copy(
                inputText = TextFieldValue(),
                isGenerating = true,
            )
        }

        generationJob = viewModelScope.launch {
            val userMessage = dialogueRepository.addMessage(conversationId, MessageRole.User.wire, text).toMessage()
            _state.update { it.copy(messages = listOf(userMessage) + it.messages) }
            val history = buildHistory()
            generateResponse(character, conversationId, history, text)
        }
    }

    private suspend fun generateResponse(
        character: Character,
        conversationId: String,
        history: List<Pair<String, String>>,
        userMessage: String,
    ) {
        val apiKey = settingsRepository.getApiKey()
        if (apiKey.isNullOrBlank()) {
            _state.update { it.copy(isGenerating = false) }
            return
        }

        val systemPrompt = buildSystemPrompt(character)
        llmService.chat(
            systemPrompt = systemPrompt,
            history = history,
            userMessage = userMessage,
        ).fold(
            onSuccess = { response ->
                val assistantMessage = dialogueRepository.addMessage(
                    conversationId,
                    MessageRole.Assistant.wire,
                    response,
                ).toMessage()
                _state.update {
                    it.copy(
                        messages = listOf(assistantMessage) + it.messages,
                        isGenerating = false,
                    )
                }
                totalMessageCount += 1
            },
            onFailure = {
                _state.update { it.copy(isGenerating = false) }
            },
        )
    }

    private fun stopGeneration() {
        generationJob?.cancel()
        _state.update { it.copy(isGenerating = false) }
    }

    private fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            dialogueRepository.deleteMessage(messageId)
            _state.update { current ->
                current.copy(messages = current.messages.filter { it.id != messageId })
            }
            totalMessageCount -= 1
        }
    }

    private fun toggleActions(messageId: String) {
        _state.update { current ->
            val next = if (current.expandedActionsMessageId == messageId) null else messageId
            current.copy(expandedActionsMessageId = next)
        }
    }

    private fun toggleSelection(messageId: String) {
        if (_state.value.isGenerating && _state.value.messages.firstOrNull { it.id == messageId }?.role == MessageRole.Assistant) return
        _state.update { current ->
            val selected = current.selectedMessageIds.toMutableSet()
            if (messageId in selected) selected.remove(messageId) else selected.add(messageId)
            current.copy(selectedMessageIds = selected)
        }
    }

    private fun copyMessage(messageId: String) {
        val text = _state.value.messages.find { it.id == messageId }?.text ?: return
        clipboardManager.setText(AnnotatedString(text))
    }

    private fun copySelected() {
        val selected = _state.value.selectedMessageIds
        if (selected.isEmpty()) return
        val texts = _state.value.messages
            .asReversed()
            .filter { it.id in selected }
            .map { it.text }
            .joinToString("\n\n")
        clipboardManager.setText(AnnotatedString(texts))
        _state.update { it.copy(selectedMessageIds = emptySet()) }
    }

    private fun deleteSelected() {
        val selected = _state.value.selectedMessageIds.toList()
        if (selected.isEmpty()) return
        viewModelScope.launch {
            selected.forEach { dialogueRepository.deleteMessage(it) }
            _state.update { current ->
                current.copy(
                    messages = current.messages.filter { it.id !in selected },
                    selectedMessageIds = emptySet(),
                )
            }
            totalMessageCount -= selected.size
        }
    }

    private fun startEdit(messageId: String) {
        val message = _state.value.messages.find { it.id == messageId } ?: return
        _state.update {
            it.copy(
                editingMessageId = messageId,
                editingText = TextFieldValue(message.text),
                expandedActionsMessageId = null,
            )
        }
    }

    private fun saveEdit() {
        val messageId = _state.value.editingMessageId ?: return
        val text = _state.value.editingText.text.trim()
        if (text.isEmpty()) return
        viewModelScope.launch {
            dialogueRepository.updateMessage(messageId, text)
            _state.update { current ->
                current.copy(
                    messages = current.messages.map { msg ->
                        if (msg.id == messageId) msg.copy(text = text) else msg
                    },
                    editingMessageId = null,
                    editingText = TextFieldValue(),
                )
            }
        }
    }

    private fun buildHistory(): List<Pair<String, String>> {
        return _state.value.messages
            .asReversed()
            .filter { it.role == MessageRole.User || it.role == MessageRole.Assistant }
            .map { it.role.wire to it.text }
    }

    private fun buildSystemPrompt(character: Character): String {
        val sb = StringBuilder()
        sb.appendLine("You are ${character.name}.")

        val desc = character.description
        if (desc.isNotBlank()) {
            sb.appendLine()
            sb.appendLine(desc)
        }

        val personality = character.personality
        if (personality.isNotBlank()) {
            sb.appendLine()
            sb.appendLine("Personality: $personality")
        }

        val scenario = character.scenario
        if (scenario.isNotBlank()) {
            sb.appendLine()
            sb.appendLine("Scenario: $scenario")
        }

        sb.appendLine()
        sb.appendLine("Respond in character as ${character.name}. Stay consistent with the description and personality above.")

        return sb.toString()
    }
}
