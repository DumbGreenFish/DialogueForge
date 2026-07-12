package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.DialogueRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.SettingsRepository
import io.github.dumbgreenfish.dialogueforge.data.service.LlmService
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.toCharacter
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.toMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

private const val DEFAULT_ERROR = "Unknown error"

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

    fun handle(intent: DialogueIntent) {
        when (intent) {
            is DialogueIntent.LoadCharacter -> loadCharacter(intent.id)
            is DialogueIntent.LoadConversation -> loadConversation()
            is DialogueIntent.Back -> {}
            is DialogueIntent.UpdateInput -> _state.update { it.copy(inputText = intent.value) }
            is DialogueIntent.Send -> onSend()
            is DialogueIntent.DismissError -> _state.update { it.copy(error = null) }
            is DialogueIntent.StopGeneration -> stopGeneration()
            is DialogueIntent.Regenerate -> regenerate()
            is DialogueIntent.DeleteMessage -> deleteMessage(intent.messageId)
            is DialogueIntent.ToggleMessageSelection -> toggleMessageSelection(intent.messageId)
            is DialogueIntent.ClearSelection -> clearSelection()
            is DialogueIntent.DeleteSelected -> deleteSelected()
            is DialogueIntent.ShowActionRow -> showActionRow(intent.messageId)
            is DialogueIntent.HideActionRow -> hideActionRow()
            is DialogueIntent.StartEditing -> startEditing(intent.messageId, intent.text)
            is DialogueIntent.UpdateEditText -> _state.update { it.copy(editText = intent.value) }
            is DialogueIntent.SaveEdit -> saveEdit()
            is DialogueIntent.CancelEdit -> cancelEdit()
            is DialogueIntent.ShowDeleteDialog -> showDeleteDialog(intent.messageId)
            is DialogueIntent.DismissDeleteDialog -> dismissDeleteDialog()
            is DialogueIntent.ConfirmDelete -> confirmDelete()
        }
    }

    private fun onSend() {
        val text = _state.value.inputText.text.trim()
        if (text.isEmpty()) return
        val conversationId = _state.value.conversationId ?: return
        val character = _state.value.character ?: return
        _state.update { it.copy(inputText = TextFieldValue(), isGenerating = true, error = null, lastSentText = text) }

        generationJob = viewModelScope.launch {
            val history = buildHistory(conversationId)
            dialogueRepository.addMessage(conversationId, MessageRole.User.wire, text)
            generateResponse(character, conversationId, history, text)
        }
    }

    private suspend fun generateResponse(character: Character, conversationId: String, history: List<Pair<String, String>>, userMessage: String) {
        val apiKey = settingsRepository.getApiKey()
        if (apiKey.isNullOrBlank()) {
            _state.update { it.copy(isGenerating = false, error = "API key not set. Configure in Presets.") }
            return
        }

        val systemPrompt = buildSystemPrompt(character)
        llmService.chat(
            systemPrompt = systemPrompt,
            history = history,
            userMessage = userMessage,
        ).fold(
            onSuccess = { response ->
                dialogueRepository.addMessage(conversationId, MessageRole.Assistant.wire, response)
                _state.update { it.copy(isGenerating = false) }
            },
            onFailure = { e ->
                _state.update { it.copy(isGenerating = false, error = e.message ?: DEFAULT_ERROR) }
            },
        )
    }

    private fun stopGeneration() {
        generationJob?.cancel()
        _state.update { it.copy(isGenerating = false) }
    }

    private fun regenerate() {
        val text = _state.value.lastSentText ?: return
        val conversationId = _state.value.conversationId ?: return
        val character = _state.value.character ?: return
        _state.update { it.copy(isGenerating = true, error = null) }

        generationJob = viewModelScope.launch {
            val fullHistory = buildHistory(conversationId)
            val historyBeforeRetry =
                if (fullHistory.lastOrNull() == (MessageRole.User.wire to text)) fullHistory.dropLast(1) else fullHistory
            generateResponse(character, conversationId, historyBeforeRetry, text)
        }
    }

    private fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            dialogueRepository.deleteMessage(messageId)
        }
    }

    private fun toggleMessageSelection(messageId: String) {
        if (_state.value.isGenerating) return
        _state.update { state ->
            val newSet = state.selectedMessageIds.toMutableSet()
            if (messageId in newSet) newSet.remove(messageId) else newSet.add(messageId)
            state.copy(
                selectedMessageIds = newSet,
                activeActionRowMessageId = if (newSet.isNotEmpty()) null else state.activeActionRowMessageId,
            )
        }
    }

    private fun clearSelection() {
        _state.update { it.copy(selectedMessageIds = emptySet(), activeActionRowMessageId = null) }
    }

    private fun deleteSelected() {
        viewModelScope.launch {
            val ids = _state.value.selectedMessageIds
            for (id in ids) {
                dialogueRepository.deleteMessage(id)
            }
            _state.update { it.copy(selectedMessageIds = emptySet()) }
        }
    }

    private fun showActionRow(messageId: String) {
        _state.update {
            if (it.activeActionRowMessageId == messageId) {
                it.copy(activeActionRowMessageId = null)
            } else {
                it.copy(activeActionRowMessageId = messageId)
            }
        }
    }

    private fun hideActionRow() {
        _state.update { it.copy(activeActionRowMessageId = null, editingMessageId = null, editText = TextFieldValue()) }
    }

    private fun startEditing(messageId: String, text: String) {
        _state.update { it.copy(editingMessageId = messageId, editText = TextFieldValue(text)) }
    }

    private fun saveEdit() {
        val messageId = _state.value.editingMessageId ?: return
        val newText = _state.value.editText.text
        viewModelScope.launch {
            dialogueRepository.updateMessage(messageId, newText)
            _state.update { it.copy(editingMessageId = null, editText = TextFieldValue()) }
        }
    }

    private fun cancelEdit() {
        _state.update { it.copy(editingMessageId = null, editText = TextFieldValue()) }
    }

    private fun showDeleteDialog(messageId: String?) {
        _state.update { it.copy(showDeleteDialog = true, deleteDialogMessageId = messageId) }
    }

    private fun dismissDeleteDialog() {
        _state.update { it.copy(showDeleteDialog = false, deleteDialogMessageId = null) }
    }

    private fun confirmDelete() {
        viewModelScope.launch {
            val messageId = _state.value.deleteDialogMessageId
            if (messageId != null) {
                dialogueRepository.deleteMessage(messageId)
            } else {
                val ids = _state.value.selectedMessageIds
                for (id in ids) {
                    dialogueRepository.deleteMessage(id)
                }
            }
            _state.update {
                it.copy(
                    showDeleteDialog = false,
                    deleteDialogMessageId = null,
                    selectedMessageIds = emptySet(),
                    activeActionRowMessageId = null,
                    editingMessageId = null,
                )
            }
        }
    }

    private suspend fun buildHistory(conversationId: String): List<Pair<String, String>> {
        return dialogueRepository.getMessages(conversationId).first()
            .filter { it.role == MessageRole.User.wire || it.role == MessageRole.Assistant.wire }
            .map { it.role to it.text }
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

    private fun loadCharacter(id: String) {
        if (_state.value.character?.id == id && _state.value.isLoading) return
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val entity = characterRepository.getById(id)
            val character = entity?.toCharacter()
            val modelName = settingsRepository.getModel()
            _state.update {
                it.copy(
                    character = character,
                    isLoading = false,
                    modelName = modelName,
                )
            }
            if (character != null) {
                handle(DialogueIntent.LoadConversation)
            }
        }
    }

    private fun loadConversation() {
        val character = _state.value.character ?: return
        viewModelScope.launch {
            val conversation = dialogueRepository.getOrCreateConversation(
                characterId = character.id,
                greeting = character.firstMessage,
            )
            dialogueRepository.getMessages(conversation.id).collect { entities ->
                _state.update {
                    it.copy(
                        conversationId = conversation.id,
                        messages = entities.map { e -> e.toMessage() },
                    )
                }
            }
        }
    }
}
