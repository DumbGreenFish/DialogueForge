package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.dumbgreenfish.dialogueforge.data.generation.GenerationController
import io.github.dumbgreenfish.dialogueforge.data.generation.GenerationRequest
import io.github.dumbgreenfish.dialogueforge.data.generation.BackgroundGenerationSettings
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.dialogue.DialogueRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.SettingsRepository
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.toCharacter
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatError
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatErrorType
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.toMessage
import kotlinx.coroutines.Dispatchers
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
    private val generationController: GenerationController,
    private val settingsRepository: SettingsRepository,
    private val backgroundGenerationSettings: BackgroundGenerationSettings,
    @InjectedParam private val clipboardManager: ClipboardManager,
) : ViewModel() {
    private val _state = MutableStateFlow(DialogueState())
    val state: StateFlow<DialogueState> = _state.asStateFlow()

    private var messageObservationJob: Job? = null
    private var totalMessageCount: Int = 0

    init {
        viewModelScope.launch {
            generationController.activeConversationIds.collect { activeIds ->
                _state.update { current ->
                    current.copy(isGenerating = current.conversationId in activeIds)
                }
            }
        }
        viewModelScope.launch {
            generationController.changedConversationIds.collect { conversationId ->
                if (_state.value.conversationId == conversationId) refreshConversation(conversationId)
            }
        }
    }

    fun handle(intent: DialogueIntent) {
        when (intent) {
            is DialogueIntent.LoadCharacter -> loadCharacter(intent.id)
            is DialogueIntent.Back -> {}
            is DialogueIntent.UpdateInput -> _state.update { it.copy(inputText = intent.value) }
            is DialogueIntent.Send -> onSend()
            is DialogueIntent.AcceptBackgroundSetup -> acceptBackgroundSetup()
            is DialogueIntent.DeclineBackgroundSetup -> finishBackgroundSetup(openSettings = false)
            is DialogueIntent.OpenBackgroundSettings -> finishBackgroundSetup(openSettings = true)
            is DialogueIntent.ContinueWithoutBackgroundSettings -> finishBackgroundSetup(openSettings = false)
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
            is DialogueIntent.RetrySend -> retrySend()
            is DialogueIntent.DismissChatError -> {
                _state.update { it.copy(chatError = null) }
                viewModelScope.launch {
                    _state.value.conversationId?.let { dialogueRepository.clearConversationError(it) }
                }
            }
        }
    }

    private fun loadCharacter(id: String) {
        if (_state.value.isLoading) return
        _state.update { DialogueState(isLoading = true) }
        viewModelScope.launch(Dispatchers.Default) {
            val entity = characterRepository.getById(id)
            val character = checkNotNull(entity?.toCharacter()) { "Character not found: $id" }
            val modelName = settingsRepository.getModel()
            val conversationResult = dialogueRepository.getOrCreateConversation(
                characterId = character.id,
                greeting = character.firstMessage,
            )
            val conversationId = conversationResult.conversation.id
            val greetingMessageId = conversationResult.greetingMessageId
            val isGenerating = conversationId in generationController.activeConversationIds.value
            val chatError = conversationResult.conversation.let { conv ->
                if (conv.hasError && conv.errorType != null && !(isGenerating && conv.errorType == ChatErrorType.Interrupted)) {
                    ChatError(conv.errorType, conv.errorText)
                } else {
                    null
                }
            }
            totalMessageCount = dialogueRepository.getMessageCount(conversationId)
            val page = dialogueRepository.getMessagesPage(conversationId, PAGE_SIZE, 0)
            val messages = page.map { it.toMessage() }
            _state.update {
                it.copy(
                    character = character,
                    isLoading = false,
                    modelName = modelName,
                    conversationId = conversationId,
                    messages = messages,
                    hasMoreOlderMessages = page.size < totalMessageCount,
                    greetingMessageId = greetingMessageId,
                    chatError = chatError,
                    isGenerating = isGenerating,
                )
            }
            observeMessages(conversationId)
        }
    }

    private fun observeMessages(conversationId: String) {
        messageObservationJob?.cancel()
        messageObservationJob = viewModelScope.launch {
            dialogueRepository.getMessages(conversationId).collect {
                refreshMessages(conversationId)
            }
        }
    }

    private suspend fun refreshConversation(conversationId: String) {
        refreshMessages(conversationId)
        val conversation = dialogueRepository.getConversation(conversationId)
        val isGenerating = conversationId in generationController.activeConversationIds.value
        val chatError = conversation?.let {
            if (it.hasError && it.errorType != null && !(isGenerating && it.errorType == ChatErrorType.Interrupted)) {
                ChatError(it.errorType, it.errorText)
            } else {
                null
            }
        }
        _state.update { current ->
            if (current.conversationId == conversationId) current.copy(chatError = chatError) else current
        }
    }

    private suspend fun refreshMessages(conversationId: String) {
        if (_state.value.conversationId != conversationId) return
        totalMessageCount = dialogueRepository.getMessageCount(conversationId)
        val loadedMessageCount = _state.value.messages.size.coerceAtLeast(PAGE_SIZE)
        val page = dialogueRepository.getMessagesPage(conversationId, loadedMessageCount, 0)
        _state.update { current ->
            if (current.conversationId != conversationId) current else current.copy(
                messages = page.map { it.toMessage() },
                hasMoreOlderMessages = page.size < totalMessageCount,
            )
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
        if (generationRequestOrNull() == null) return
        if (backgroundGenerationSettings.shouldShowOnboarding()) {
            _state.update { it.copy(backgroundSetupStep = BackgroundSetupStep.Introduction) }
            return
        }
        sendCurrentInput()
    }

    private fun acceptBackgroundSetup() {
        if (_state.value.backgroundSetupStep != BackgroundSetupStep.Introduction) return
        backgroundGenerationSettings.requestNotificationPermission()
        _state.update { it.copy(backgroundSetupStep = BackgroundSetupStep.BackgroundWork) }
    }

    private fun finishBackgroundSetup(openSettings: Boolean) {
        if (_state.value.backgroundSetupStep == null) return
        backgroundGenerationSettings.completeOnboarding()
        _state.update { it.copy(backgroundSetupStep = null) }
        sendCurrentInput()
        if (openSettings) backgroundGenerationSettings.openBackgroundSettings()
    }

    private fun generationRequestOrNull(): GenerationRequest? {
        val current = _state.value
        val conversationId = current.conversationId ?: return null
        val character = current.character ?: return null
        val text = current.inputText.text.trim()
        if (text.isEmpty() && current.messages.firstOrNull()?.role != MessageRole.User) return null
        return GenerationRequest(
            conversationId = conversationId,
            characterId = character.id,
            userText = text.ifEmpty { null },
            characterName = character.name,
        )
    }

    private fun sendCurrentInput() {
        val request = generationRequestOrNull() ?: return

        if (request.userText == null) {
            if (generationController.start(request)) {
                _state.update { it.copy(isGenerating = true, chatError = null) }
            }
            return
        }

        if (generationController.start(request)) {
            _state.update {
                it.copy(
                    inputText = TextFieldValue(),
                    isGenerating = true,
                    chatError = null,
                )
            }
        }
    }

    private fun stopGeneration() {
        _state.value.conversationId?.let(generationController::cancel)
    }

    private fun retrySend() {
        val conversationId = _state.value.conversationId ?: return
        val character = _state.value.character ?: return
        if (generationController.start(GenerationRequest(conversationId, character.id, null, character.name))) {
            _state.update { it.copy(isGenerating = true, chatError = null) }
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

}
