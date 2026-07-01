package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
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
                            ) { index, item ->
                                when {
                                    item.dateLabel != null -> DateSeparator(label = item.dateLabel)
                                    item.message != null -> MessageBubble(message = item.message, modifier = Modifier.padding(horizontal = BodyPaddingH))
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = cs.outline)
                    Composer(
                        inputText = state.inputText,
                        onInputChange = { viewModel.handle(DialogueIntent.UpdateInput(it)) },
                        onSend = { viewModel.handle(DialogueIntent.Send) },
                    )
                }
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
