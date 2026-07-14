package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.common.WindowClass
import io.github.dumbgreenfish.dialogueforge.ui.common.formatDateLabel
import io.github.dumbgreenfish.dialogueforge.ui.common.windowClass
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.MessageWidth

private val ContentPaddingV = 24.dp
private val SpacerBottomHeight = 16.dp
private val LoadMoreThreshold = 2

private data class ChatItem(val dateLabel: String?, val message: Message?)

data class MessagesListData(
    val messages: List<Message>,
    val isLoadingOlder: Boolean,
    val hasMoreOlderMessages: Boolean,
    val onLoadOlder: () -> Unit,
)

data class MessageItemContext(
    val character: Character,
    val isGenerating: Boolean,
    val messageWidth: MessageWidth,
    val expandedActionsMessageId: String?,
    val editingMessageId: String?,
    val editingText: TextFieldValue,
    val selectedMessageIds: Set<String>,
    val onActionRowEvent: (String, ActionRowEvent) -> Unit,
    val onEditFieldEvent: (String, EditFieldEvent) -> Unit,
    val onVariantSelectorEvent: (String, VariantSelectorEvent) -> Unit,
    val onMessageItemEvent: (String, MessageItemEvent) -> Unit,
)

@Composable
internal fun MessagesList(
    data: MessagesListData,
    itemContext: MessageItemContext,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    val items = remember(data.messages) { buildItems(data.messages) }
    val hasUserMessages = data.messages.any { it.role == MessageRole.User }
    val firstAssistantId = data.messages.firstOrNull { it.role == MessageRole.Assistant }?.id
    val lastAssistantId = data.messages.firstOrNull { it.role == MessageRole.Assistant }?.id

    val shouldLoadOlder by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex <= LoadMoreThreshold &&
                    data.hasMoreOlderMessages &&
                    !data.isLoadingOlder
        }
    }

    LaunchedEffect(shouldLoadOlder) {
        if (shouldLoadOlder) data.onLoadOlder()
    }

    LaunchedEffect(data.messages.size) {
        if (data.messages.isNotEmpty() && listState.firstVisibleItemIndex <= LoadMoreThreshold) {
            listState.animateScrollToItem(0)
        }
    }

    LazyColumn(
        state = listState,
        reverseLayout = true,
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = ContentPaddingV),
    ) {
        item { Spacer(Modifier.height(SpacerBottomHeight)) }

        itemsIndexed(
            items = items,
            key = { index, item -> item.message?.id ?: item.dateLabel ?: "sep-$index" },
        ) { _, item ->
            when {
                item.dateLabel != null -> DateSeparator(label = item.dateLabel)
                item.message != null -> {
                    val message = item.message
                    val displayStyle = when {
                        !hasUserMessages &&
                                message.role == MessageRole.Assistant &&
                                message.id == firstAssistantId -> MessageDisplayStyle.Greeting

                        else -> MessageDisplayStyle.Regular
                    }
                    val position =
                        if (message.id == lastAssistantId) MessagePosition.LastAssistant else MessagePosition.Normal
                    val interactionState = when {
                        itemContext.editingMessageId == message.id -> MessageInteractionState.Editing(
                            itemContext.editingText
                        )

                        itemContext.isGenerating -> MessageInteractionState.Generating
                        itemContext.selectedMessageIds.isNotEmpty() -> MessageInteractionState.Selecting(message.id in itemContext.selectedMessageIds)
                        else -> MessageInteractionState.Browsing(itemContext.expandedActionsMessageId == message.id)
                    }
                    Box(modifier = calculateBoxModifier()) {
                        MessageItem(
                            state = MessageItemState(
                                message = message,
                                displayStyle = displayStyle,
                                position = position,
                                interactionState = interactionState,
                                character = itemContext.character,
                                messageWidth = itemContext.messageWidth,
                            ),
                            callbacks = MessageItemCallbacks(
                                onActionRowEvent = { event -> itemContext.onActionRowEvent(message.id, event) },
                                onEditFieldEvent = { event -> itemContext.onEditFieldEvent(message.id, event) },
                                onVariantSelectorEvent = { event ->
                                    itemContext.onVariantSelectorEvent(
                                        message.id,
                                        event
                                    )
                                },
                                onMessageItemEvent = { event -> itemContext.onMessageItemEvent(message.id, event) },
                            ),
                        )
                    }
                }
            }
        }

        if (data.isLoadingOlder) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = ForgeColors.spark)
                }
            }
        }
    }
}

@Composable
private fun calculateBoxModifier(): Modifier = if (windowClass == WindowClass.Compact) {
    Modifier.fillMaxWidth()
} else {
    Modifier
        .widthIn(max = DialogueLayout.ContentMaxWidth)
        .fillMaxWidth()
        .padding(horizontal = DialogueLayout.ContentPaddingH)
}

private fun buildItems(messages: List<Message>): List<ChatItem> {
    val result = mutableListOf<ChatItem>()
    for ((index, msg) in messages.withIndex()) {
        result.add(ChatItem(dateLabel = null, message = msg))
        val currentDate = formatDateLabel(msg.timestamp)
        val nextDate = messages.getOrNull(index + 1)?.let { formatDateLabel(it.timestamp) }
        if (currentDate != nextDate) {
            result.add(ChatItem(dateLabel = currentDate, message = null))
        }
    }
    return result
}
