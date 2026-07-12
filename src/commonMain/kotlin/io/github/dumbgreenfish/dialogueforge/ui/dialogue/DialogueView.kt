package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.ForgeSettings
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_delete_cancel
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_delete_confirm
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_delete_message
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_delete_title
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_error_dismiss
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_error_retry
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_generating
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_placeholder
import io.github.dumbgreenfish.dialogueforge.ui.common.WindowClass
import io.github.dumbgreenfish.dialogueforge.ui.common.formatDateLabel
import io.github.dumbgreenfish.dialogueforge.ui.common.windowClass
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.ChatHeader
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.Composer
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.DateSeparator
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.MessageBubble
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.SelectedHeader
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.bubble.model.MessageBubbleActions
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.bubble.model.MessageBubbleUiState
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.MessageWidth
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

private val BodyPaddingH = 12.dp
private const val DialogueContentWidthFraction = 0.65f
private const val DialogueComposerWidthFraction = 0.80f
private val IndicatorPaddingV = 8.dp
private val IndicatorGap = 8.dp
private val IndicatorSpinnerSize = 14.dp
private val IndicatorStroke = 2.dp
private val ErrorTextPaddingStart = 4.dp
private const val DEFAULT_CHARACTER_NAME = "Character"

private data class ChatItem(val dateLabel: String?, val message: Message?)

@Composable
@OptIn(KoinExperimentalAPI::class)
fun DialogueView(characterId: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<DialogueViewModel>()
    val state by viewModel.state.collectAsState()
    val forgeSettings = koinInject<ForgeSettings>()
    val messageWidth by forgeSettings.messageWidth.collectAsState()

    LaunchedEffect(characterId) {
        viewModel.handle(DialogueIntent.LoadCharacter(characterId))
    }

    val cs = MaterialTheme.colorScheme
    val listState = rememberLazyListState()
    var needsInstantScroll by remember { mutableStateOf(true) }

    LaunchedEffect(state.isLoading, state.messages.size) {
        if (state.messages.isNotEmpty()) {
            withFrameNanos { }
            if (needsInstantScroll) {
                listState.scrollToItem(0)
                needsInstantScroll = false
            } else {
                listState.animateScrollToItem(0, 0)
            }
        }
    }

    val clipboard = LocalClipboardManager.current
    val isSelectionMode = state.selectedMessageIds.isNotEmpty()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier,
    ) { _ ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding(),
            color = cs.background,
        ) {
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ForgeColors.spark)
                }
                return@Surface
            }

            val compact = windowClass != WindowClass.Wide
            val items = buildChatItems(state.messages).reversed()

            Column(Modifier.fillMaxSize()) {
                if (isSelectionMode) {
                    SelectedHeader(
                        selectedCount = state.selectedMessageIds.size,
                        onClearSelection = { viewModel.handle(DialogueIntent.ClearSelection) },
                        onCopySelected = {
                            val text = state.messages
                                .filter { it.id in state.selectedMessageIds }
                                .joinToString("\n\n") { it.text }
                            clipboard.setText(AnnotatedString(text))
                            viewModel.handle(DialogueIntent.ClearSelection)
                        },
                        onDeleteSelected = { viewModel.handle(DialogueIntent.ShowDeleteDialog(null)) },
                    )
                } else {
                    ChatHeader(
                        character = state.character,
                        modelName = state.modelName,
                        onBack = onBack,
                    )
                }
                HorizontalDivider(color = cs.outline)

                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = if (compact) Alignment.TopStart else Alignment.TopCenter,
                ) {
                    Column(
                        modifier = if (compact) {
                            Modifier.fillMaxSize()
                        } else {
                            Modifier.fillMaxWidth(DialogueContentWidthFraction).fillMaxHeight()
                        },
                    ) {
                        MessageList(
                            items = items,
                            listState = listState,
                            state = state,
                            messageWidth = messageWidth,
                            isSelectionMode = isSelectionMode,
                            clipboard = clipboard,
                            viewModel = viewModel,
                        )
                        if (compact) {
                            HorizontalDivider(color = cs.outline)
                            GeneratingIndicator(
                                visible = state.isGenerating,
                                characterName = state.character?.name ?: DEFAULT_CHARACTER_NAME,
                            )
                            ErrorBanner(
                                error = state.error,
                                onRetry = { viewModel.handle(DialogueIntent.Regenerate) },
                                onDismiss = { viewModel.handle(DialogueIntent.DismissError) },
                            )
                        }
                    }
                }

                if (!compact) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(DialogueComposerWidthFraction)) {
                            GeneratingIndicator(
                                visible = state.isGenerating,
                                characterName = state.character?.name ?: DEFAULT_CHARACTER_NAME,
                            )
                            ErrorBanner(
                                error = state.error,
                                onRetry = { viewModel.handle(DialogueIntent.Regenerate) },
                                onDismiss = { viewModel.handle(DialogueIntent.DismissError) },
                            )
                        }
                    }
                }

                DialogueComposer(compact = compact, state = state, viewModel = viewModel)
            }

            if (state.showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.handle(DialogueIntent.DismissDeleteDialog) },
                    title = { Text(stringResource(Res.string.dialogue_delete_title)) },
                    text = { Text(stringResource(Res.string.dialogue_delete_message)) },
                    confirmButton = {
                        TextButton(onClick = { viewModel.handle(DialogueIntent.ConfirmDelete) }) {
                            Text(stringResource(Res.string.dialogue_delete_confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.handle(DialogueIntent.DismissDeleteDialog) }) {
                            Text(stringResource(Res.string.dialogue_delete_cancel))
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.MessageList(
    items: List<ChatItem>,
    listState: LazyListState,
    state: DialogueState,
    messageWidth: MessageWidth,
    isSelectionMode: Boolean,
    clipboard: ClipboardManager,
    viewModel: DialogueViewModel,
) {
    if (items.isEmpty()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = BodyPaddingH),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.dialogue_placeholder),
                style = MaterialTheme.typography.bodyLarge,
                color = ForgeColors.onSurfaceFaint,
                textAlign = TextAlign.Center,
            )
        }
        return
    }

    LazyColumn(
        state = listState,
        reverseLayout = true,
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
    ) {
        itemsIndexed(items) { _, item ->
            when {
                item.dateLabel != null -> DateSeparator(label = item.dateLabel)
                item.message != null -> {
                    val message = item.message
                    MessageBubble(
                        message = message,
                        uiState = MessageBubbleUiState(
                            isSelected = message.id in state.selectedMessageIds,
                            inSelectionMode = isSelectionMode,
                            showActionRow = state.activeActionRowMessageId == message.id,
                            isEditing = state.editingMessageId == message.id,
                            isGenerating = state.isGenerating,
                            editText = state.editText,
                        ),
                        actions = MessageBubbleActions(
                            onToggleSelection = { id -> viewModel.handle(DialogueIntent.ToggleMessageSelection(id)) },
                            onEnterSelectionMode = { id -> viewModel.handle(DialogueIntent.ToggleMessageSelection(id)) },
                            onShowActionRow = { id -> viewModel.handle(DialogueIntent.ShowActionRow(id)) },
                            onCopy = { id ->
                                state.messages.find { it.id == id }?.text?.let { text ->
                                    clipboard.setText(AnnotatedString(text))
                                    viewModel.handle(DialogueIntent.HideActionRow)
                                }
                            },
                            onEdit = { id -> viewModel.handle(DialogueIntent.StartEditing(id, message.text)) },
                            onDelete = { id -> viewModel.handle(DialogueIntent.ShowDeleteDialog(id)) },
                            onSave = { viewModel.handle(DialogueIntent.SaveEdit) },
                            onCancel = { viewModel.handle(DialogueIntent.CancelEdit) },
                            onEditTextChange = { value -> viewModel.handle(DialogueIntent.UpdateEditText(value)) },
                        ),
                        messageWidth = messageWidth,
                        modifier = Modifier.padding(horizontal = BodyPaddingH),
                    )
                }
            }
        }
    }
}

@Composable
private fun GeneratingIndicator(visible: Boolean, characterName: String) {
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = BodyPaddingH, vertical = IndicatorPaddingV),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(IndicatorGap),
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(IndicatorSpinnerSize),
                strokeWidth = IndicatorStroke,
                color = ForgeColors.spark,
            )
            Text(
                text = stringResource(Res.string.dialogue_generating, characterName),
                style = MaterialTheme.typography.bodySmall,
                color = ForgeColors.onSurfaceFaint,
            )
        }
    }
}

@Composable
private fun ErrorBanner(error: String?, onRetry: () -> Unit, onDismiss: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    AnimatedVisibility(visible = error != null, enter = fadeIn(), exit = fadeOut()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = BodyPaddingH, vertical = IndicatorPaddingV),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(IndicatorGap),
        ) {
            Text(
                text = error ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = cs.error,
                modifier = Modifier.weight(1f).padding(start = ErrorTextPaddingStart),
            )
            TextButton(onClick = onRetry) {
                Text(
                    stringResource(Res.string.dialogue_error_retry),
                    color = ForgeColors.spark,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(Res.string.dialogue_error_dismiss),
                    color = ForgeColors.onSurfaceFaint,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
private fun DialogueComposer(compact: Boolean, state: DialogueState, viewModel: DialogueViewModel) {
    if (compact) {
        Composer(
            textFieldValue = state.inputText,
            onInputChange = { viewModel.handle(DialogueIntent.UpdateInput(it)) },
            onSend = { viewModel.handle(DialogueIntent.Send) },
            isGenerating = state.isGenerating,
            onStop = { viewModel.handle(DialogueIntent.StopGeneration) },
        )
    } else {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Box(modifier = Modifier.fillMaxWidth(DialogueComposerWidthFraction)) {
                Composer(
                    textFieldValue = state.inputText,
                    onInputChange = { viewModel.handle(DialogueIntent.UpdateInput(it)) },
                    onSend = { viewModel.handle(DialogueIntent.Send) },
                    isGenerating = state.isGenerating,
                    onStop = { viewModel.handle(DialogueIntent.StopGeneration) },
                )
            }
        }
    }
}

private fun buildChatItems(messages: List<Message>): List<ChatItem> {
    val result = mutableListOf<ChatItem>()
    var lastDate: String? = null
    for (msg in messages) {
        val date = formatDateLabel(msg.timestamp)
        if (date != lastDate) {
            result.add(ChatItem(dateLabel = date, message = null))
            lastDate = date
        }
        result.add(ChatItem(dateLabel = null, message = msg))
    }
    return result
}
