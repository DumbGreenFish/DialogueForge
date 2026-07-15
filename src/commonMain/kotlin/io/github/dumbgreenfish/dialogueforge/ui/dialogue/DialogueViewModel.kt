package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.DialogueRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.MessageEntity
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.SettingsRepository
import io.github.dumbgreenfish.dialogueforge.data.service.LlmService
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_error_api_key_not_set
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import org.jetbrains.compose.resources.getString
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
import org.koin.core.annotation.KoinViewModel

private const val DEFAULT_ERROR = "Unknown error"
private const val PAGE_SIZE = 50

@KoinViewModel
class DialogueViewModel(
    private val characterRepository: CharacterRepository,
    private val dialogueRepository: DialogueRepository,
    private val llmService: LlmService,
    private val settingsRepository: SettingsRepository,
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
            is DialogueIntent.DismissSnackbar -> _state.update { it.copy(snackbarError = null) }
            is DialogueIntent.StopGeneration -> stopGeneration()
            is DialogueIntent.Regenerate -> regenerate()
            is DialogueIntent.DeleteMessage -> deleteMessage(intent.messageId)
            is DialogueIntent.LoadOlderMessages -> loadOlderMessages()
            is DialogueIntent.ToggleActions -> toggleActions(intent.messageId)
            is DialogueIntent.StartEdit -> startEdit(intent.messageId)
            is DialogueIntent.UpdateEditText -> _state.update { it.copy(editingText = intent.value) }
            is DialogueIntent.SaveEdit -> saveEdit()
            is DialogueIntent.CancelEdit -> _state.update { it.copy(editingMessageId = null, editingText = TextFieldValue()) }
            is DialogueIntent.RegenerateMessage -> regenerateMessage(intent.messageId)
            is DialogueIntent.NextVariant -> nextVariant(intent.messageId)
            is DialogueIntent.PrevVariant -> prevVariant(intent.messageId)
            is DialogueIntent.ToggleSelection -> toggleSelection(intent.messageId)
            is DialogueIntent.ClearSelection -> _state.update { it.copy(selectedMessageIds = emptySet()) }
            is DialogueIntent.DeleteSelected -> deleteSelected()
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
            val messages = page.withVariantCounts(dialogueRepository)
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
            val mapped = page.withVariantCounts(dialogueRepository)
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
                snackbarError = null,
                lastSentText = text,
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
            _state.update {
                it.copy(
                    isGenerating = false,
                    snackbarError = getString(Res.string.dialogue_error_api_key_not_set),
                )
            }
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
            onFailure = { e ->
                _state.update {
                    it.copy(
                        isGenerating = false,
                        snackbarError = e.message ?: DEFAULT_ERROR,
                    )
                }
            },
        )
    }

    private fun stopGeneration() {
        generationJob?.cancel()
        _state.update { it.copy(isGenerating = false) }
    }

    private fun regenerate() {
        val lastAssistant = _state.value.messages.firstOrNull { it.role == MessageRole.Assistant }
        if (lastAssistant != null) {
            regenerateMessage(lastAssistant.id)
        } else {
            val text = _state.value.lastSentText ?: return
            val conversationId = _state.value.conversationId ?: return
            val character = _state.value.character ?: return
            _state.update { it.copy(isGenerating = true, snackbarError = null) }

            generationJob = viewModelScope.launch {
                val fullHistory = buildHistory()
                val historyBeforeRetry =
                    if (fullHistory.lastOrNull() == (MessageRole.User.wire to text)) fullHistory.dropLast(1) else fullHistory
                generateResponse(character, conversationId, historyBeforeRetry, text)
            }
        }
    }

    private suspend fun List<MessageEntity>.withVariantCounts(repo: DialogueRepository): List<Message> {
        if (isEmpty()) return emptyList()
        val counts = repo.countVariantsByMessageIds(map { it.id }).associate { it.messageId to it.count }
        return map { entity ->
            val variantCount = counts[entity.id]?.let { it.coerceAtLeast(1) } ?: 1
            entity.toMessage(variantCount)
        }
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
        _state.update { current ->
            val selected = current.selectedMessageIds.toMutableSet()
            if (messageId in selected) selected.remove(messageId) else selected.add(messageId)
            current.copy(selectedMessageIds = selected)
        }
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

    private fun buildHistoryBefore(messageId: String): Pair<List<Pair<String, String>>, String?> {
        val chronological = _state.value.messages.asReversed()
        val index = chronological.indexOfFirst { it.id == messageId }
        if (index <= 0) return emptyList<Pair<String, String>>() to null
        val before = chronological.subList(0, index)
            .filter { it.role == MessageRole.User || it.role == MessageRole.Assistant }
        val userMessage = before.lastOrNull { it.role == MessageRole.User }?.text
        val history = if (userMessage != null) {
            before.dropLastWhile { it.role == MessageRole.User }
        } else {
            before
        }
        return history.map { it.role.wire to it.text } to userMessage
    }

    private fun regenerateMessage(messageId: String) {
        if (_state.value.isGenerating) return
        val character = _state.value.character ?: return
        val conversationId = _state.value.conversationId ?: return
        val message = _state.value.messages.find { it.id == messageId && it.role == MessageRole.Assistant } ?: return
        _state.update { it.copy(isGenerating = true, snackbarError = null) }

        generationJob = viewModelScope.launch {
            val (history, userMessage) = buildHistoryBefore(messageId)
            if (userMessage == null) {
                _state.update { it.copy(isGenerating = false) }
                return@launch
            }
            llmService.chat(
                systemPrompt = buildSystemPrompt(character),
                history = history,
                userMessage = userMessage,
            ).fold(
                onSuccess = { response ->
                    val variant = dialogueRepository.addVariant(messageId, response)
                    val updated = dialogueRepository.setActiveVariant(messageId, variant.ordinal)
                    updated?.let { entity ->
                        _state.update { current ->
                            current.copy(
                                messages = current.messages.map { msg ->
                                    if (msg.id == messageId) entity.toMessage(variant.ordinal + 1) else msg
                                },
                                isGenerating = false,
                            )
                        }
                    } ?: _state.update { it.copy(isGenerating = false) }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isGenerating = false,
                            snackbarError = e.message ?: DEFAULT_ERROR,
                        )
                    }
                },
            )
        }
    }

    private fun nextVariant(messageId: String) {
        val message = _state.value.messages.find { it.id == messageId } ?: return
        if (message.variantIndex >= message.variantCount - 1) {
            regenerateMessage(messageId)
        } else {
            viewModelScope.launch {
                val updated = dialogueRepository.setActiveVariant(messageId, message.variantIndex + 1)
                updated?.let { entity ->
                    _state.update { current ->
                        current.copy(
                            messages = current.messages.map { msg ->
                                if (msg.id == messageId) msg.copy(text = entity.text, variantIndex = entity.activeVariant) else msg
                            },
                        )
                    }
                }
            }
        }
    }

    private fun prevVariant(messageId: String) {
        val message = _state.value.messages.find { it.id == messageId } ?: return
        if (message.variantIndex <= 0) return
        viewModelScope.launch {
            val updated = dialogueRepository.setActiveVariant(messageId, message.variantIndex - 1)
            updated?.let { entity ->
                _state.update { current ->
                    current.copy(
                        messages = current.messages.map { msg ->
                            if (msg.id == messageId) msg.copy(text = entity.text, variantIndex = entity.activeVariant) else msg
                        },
                    )
                }
            }
        }
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
