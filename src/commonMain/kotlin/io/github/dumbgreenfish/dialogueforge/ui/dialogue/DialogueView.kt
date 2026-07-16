package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import io.github.dumbgreenfish.dialogueforge.data.cache.ImageCache
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.ForgeSettings
import io.github.dumbgreenfish.dialogueforge.util.image.toImageBitmapOrNull
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.background.ChatBackground
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.composer.Composer
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.header.ChatHeader
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.header.SelectionHeader
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages.ActionRowEvent
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages.EditFieldEvent
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages.MessageItemContext
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages.MessageItemEvent
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages.MessagesList
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages.MessagesListData
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.scaffold.DialogueScaffold
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.MessageWidth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@Composable
@OptIn(KoinExperimentalAPI::class)
fun DialogueView(characterId: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<DialogueViewModel>()
    val state by viewModel.state.collectAsState()
    val forgeSettings = koinInject<ForgeSettings>()
    val messageWidth by forgeSettings.messageWidth.collectAsState()
    val bgBytes by forgeSettings.chatBackgroundBytes.collectAsState()
    val bgOpacity by forgeSettings.chatBackgroundOpacity.collectAsState()
    val panelOpacity by forgeSettings.chatPanelOpacity.collectAsState()
    val bgDim by forgeSettings.chatBackgroundDim.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    val imageCache = koinInject<ImageCache>()

    LaunchedEffect(characterId) {
        viewModel.handle(DialogueIntent.LoadCharacter(characterId))
    }

    val bgFlow = imageCache.observeBackground("chatBg")
    val bgBitmap by bgFlow.collectAsState()
    LaunchedEffect(bgBytes) {
        bgFlow.value = bgBytes?.let { withContext(Dispatchers.Default) { it.toImageBitmapOrNull() } }
    }

    val character = state.character
    if (character == null || state.conversationId == null) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding(),
    ) {
        ChatBackground(
            bitmap = bgBitmap,
            opacity = bgOpacity,
            dim = bgDim,
        )

        val bg = MaterialTheme.colorScheme.background
        DialogueScaffold(
            headerBackground = bg.copy(alpha = panelOpacity),
            composerBackground = bg.copy(alpha = panelOpacity),
            header = {
                AnimatedContent(
                    targetState = state.selectedMessageIds.isNotEmpty(),
                    label = "dialogue_header",
                ) { inSelectionMode ->
                    if (inSelectionMode) {
                        SelectionHeader(
                            selectedCount = state.selectedMessageIds.size,
                            onClearSelection = { viewModel.handle(DialogueIntent.ClearSelection) },
                            onCopySelected = {
                                val texts = state.messages
                                    .asReversed()
                                    .filter { it.id in state.selectedMessageIds }
                                    .map { it.text }
                                    .joinToString("\n\n")
                                clipboardManager.setText(AnnotatedString(texts))
                                viewModel.handle(DialogueIntent.ClearSelection)
                            },
                            onDeleteSelected = { viewModel.handle(DialogueIntent.DeleteSelected) },
                        )
                    } else {
                        ChatHeader(
                            character = character,
                            modelName = state.modelName,
                            onBack = onBack,
                            onHistory = { /* TODO: open conversation history */ },
                            onAdd = { /* TODO: create new conversation */ },
                        )
                    }
                }
            },
            composer = {
                Composer(
                    textFieldValue = state.inputText,
                    onInputChange = { viewModel.handle(DialogueIntent.UpdateInput(it)) },
                    onSend = { viewModel.handle(DialogueIntent.Send) },
                    isGenerating = state.isGenerating,
                    onStop = { viewModel.handle(DialogueIntent.StopGeneration) },
                    onAttach = { /* TODO: open file picker for attachment */ },
                )
            },
            messages = {
                MessagesList(
                    data = MessagesListData(
                        messages = state.messages,
                        isLoadingOlder = state.isLoadingOlder,
                        hasMoreOlderMessages = state.hasMoreOlderMessages,
                        onLoadOlder = { viewModel.handle(DialogueIntent.LoadOlderMessages) },
                    ),
                    itemContext = MessageItemContext(
                        character = character,
                        isGenerating = state.isGenerating,
                        messageWidth = messageWidth,
                        expandedActionsMessageId = state.expandedActionsMessageId,
                        editingMessageId = state.editingMessageId,
                        editingText = state.editingText,
                        selectedMessageIds = state.selectedMessageIds,
                        onActionRowEvent = { messageId, event ->
                            onActionRowEvent(messageId, event, state.messages, clipboardManager, viewModel)
                        },
                        onEditFieldEvent = { messageId, event ->
                            onEditFieldEvent(messageId, event, viewModel)
                        },
                        onMessageItemEvent = { messageId, event ->
                            onMessageItemEvent(messageId, event, viewModel)
                        },
                    ),
                )
            },
        )
    }
}

private fun onActionRowEvent(
    messageId: String,
    event: ActionRowEvent,
    messages: List<Message>,
    clipboardManager: ClipboardManager,
    viewModel: DialogueViewModel,
) {
    when (event) {
        ActionRowEvent.Copy -> messages.find { it.id == messageId }?.text
            ?.let { clipboardManager.setText(AnnotatedString(it)) }
        ActionRowEvent.Edit -> viewModel.handle(DialogueIntent.StartEdit(messageId))
        ActionRowEvent.Delete -> viewModel.handle(DialogueIntent.DeleteMessage(messageId))
        ActionRowEvent.Select -> viewModel.handle(DialogueIntent.ToggleSelection(messageId))
    }
}

private fun onEditFieldEvent(
    messageId: String,
    event: EditFieldEvent,
    viewModel: DialogueViewModel,
) {
    when (event) {
        is EditFieldEvent.TextChanged -> viewModel.handle(DialogueIntent.UpdateEditText(event.value))
        EditFieldEvent.Save -> viewModel.handle(DialogueIntent.SaveEdit)
        EditFieldEvent.Cancel -> viewModel.handle(DialogueIntent.CancelEdit)
    }
}

private fun onMessageItemEvent(
    messageId: String,
    event: MessageItemEvent,
    viewModel: DialogueViewModel,
) {
    when (event) {
        MessageItemEvent.ToggleActions -> viewModel.handle(DialogueIntent.ToggleActions(messageId))
        MessageItemEvent.ToggleSelection -> viewModel.handle(DialogueIntent.ToggleSelection(messageId))
    }
}
