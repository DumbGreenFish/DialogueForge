package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.ChatHeader
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.Composer
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.DateSeparator
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.MessageBubble
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.formatDateLabel
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

private val BodyPaddingH = 12.dp

private data class ChatItem(val dateLabel: String?, val message: Message?)

@Composable
@OptIn(KoinExperimentalAPI::class)
fun DialogueView(characterId: String, onBack: () -> Unit) {
    val viewModel = koinViewModel<DialogueViewModel>()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(characterId) {
        viewModel.handle(DialogueIntent.LoadCharacter(characterId))
    }

    val cs = MaterialTheme.colorScheme
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(buildChatItems(state.messages).size - 1)
        }
    }

    var deleteTargetId by remember { mutableStateOf<String?>(null) }

    Scaffold { innerPadding ->
        Surface(modifier = Modifier.fillMaxSize().padding(innerPadding), color = cs.background) {
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ForgeColors.spark)
                }
            } else {
                Column(Modifier.fillMaxSize()) {
                    ChatHeader(
                        character = state.character,
                        presetName = state.presetName,
                        modelName = state.modelName,
                        onBack = onBack,
                    )
                    HorizontalDivider(color = cs.outline)
                    val messages = state.messages
                    if (messages.isEmpty()) {
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
                    } else {
                        val items = buildChatItems(messages)
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        ) {
                            itemsIndexed(
                                items = items,
                                key = { _, item -> item.message?.id ?: "date-${item.dateLabel}" },
                            ) { _, item ->
                                when {
                                    item.dateLabel != null -> DateSeparator(label = item.dateLabel)
                                    item.message != null -> MessageBubble(
                                        message = item.message,
                                        onLongPress = { deleteTargetId = item.message.id },
                                        modifier = Modifier.padding(horizontal = BodyPaddingH),
                                    )
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = cs.outline)
                    AnimatedVisibility(
                        visible = state.isGenerating,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = BodyPaddingH, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = ForgeColors.spark,
                            )
                            Text(
                                text = stringResource(Res.string.dialogue_generating, state.character?.name ?: "Character"),
                                style = MaterialTheme.typography.bodySmall,
                                color = ForgeColors.onSurfaceFaint,
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = state.error != null,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = BodyPaddingH, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = state.error ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = cs.error,
                                modifier = Modifier.weight(1f).padding(start = 4.dp),
                            )
                            TextButton(onClick = { viewModel.handle(DialogueIntent.Regenerate) }) {
                                Text(
                                    stringResource(Res.string.dialogue_error_retry),
                                    color = ForgeColors.spark,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                            TextButton(onClick = { viewModel.handle(DialogueIntent.DismissError) }) {
                                Text(
                                    stringResource(Res.string.dialogue_error_dismiss),
                                    color = ForgeColors.onSurfaceFaint,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }
                    }
                    Composer(
                        inputText = state.inputText,
                        onInputChange = { viewModel.handle(DialogueIntent.UpdateInput(it)) },
                        onSend = { viewModel.handle(DialogueIntent.Send) },
                        isGenerating = state.isGenerating,
                        onStop = { viewModel.handle(DialogueIntent.StopGeneration) },
                    )
                }
            }
        }
    }

    deleteTargetId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteTargetId = null },
            title = { Text(stringResource(Res.string.dialogue_delete_title)) },
            text = { Text(stringResource(Res.string.dialogue_delete_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.handle(DialogueIntent.DeleteMessage(id))
                    deleteTargetId = null
                }) {
                    Text(stringResource(Res.string.dialogue_delete_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTargetId = null }) {
                    Text(stringResource(Res.string.dialogue_delete_cancel))
                }
            },
        )
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
