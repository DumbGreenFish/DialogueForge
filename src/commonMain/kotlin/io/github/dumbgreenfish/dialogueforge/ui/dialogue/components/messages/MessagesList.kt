package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

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
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.scaffold.DialogueLayout
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatError
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.MessageWidth

private val ContentPaddingV = 24.dp
private val LoadMoreThreshold = 2

private data class ChatItem(val dateLabel: String?, val message: Message?)

data class MessagesListData(
    val messages: List<Message>,
    val isLoadingOlder: Boolean,
    val hasMoreOlderMessages: Boolean,
    val onLoadOlder: () -> Unit,
    val chatError: ChatError? = null,
    val onRetryChatError: () -> Unit = {},
    val onDismissChatError: () -> Unit = {},
)

data class MessageItemContext(
    val character: Character,
    val isGenerating: Boolean,
    val messageWidth: MessageWidth,
    val expandedActionsMessageId: String?,
    val editingMessageId: String?,
    val editingText: TextFieldValue,
    val selectedMessageIds: Set<String>,
    val greetingMessageId: String?,
    val onActionRowEvent: (String, ActionRowEvent) -> Unit,
    val onEditFieldEvent: (String, EditFieldEvent) -> Unit,
    val onMessageItemEvent: (String, MessageItemEvent) -> Unit,
)

@Composable
internal fun MessagesList(
    data: MessagesListData,
    itemContext: MessageItemContext,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    val hasUserMessages = data.messages.any { it.role == MessageRole.User }
    val isOnlyGreeting = !hasUserMessages && data.messages.size == 1
    val firstAssistantId = data.messages.firstOrNull { it.role == MessageRole.Assistant }?.id

    if (isOnlyGreeting) {
        val greetingMessage = data.messages.first()
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            MessageItem(
                message = greetingMessage,
                isGreeting = true,
                itemContext = itemContext,
            )
        }
        return
    }

    val items = remember(data.messages) { buildItems(data.messages) }

    val shouldLoadOlder by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleIndex >= layoutInfo.totalItemsCount - 1 - LoadMoreThreshold &&
                    layoutInfo.totalItemsCount > 0 &&
                    data.hasMoreOlderMessages &&
                    !data.isLoadingOlder
        }
    }

    LaunchedEffect(shouldLoadOlder) {
        if (shouldLoadOlder) data.onLoadOlder()
    }

    LazyColumn(
        state = listState,
        reverseLayout = true,
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = ContentPaddingV),
    ) {
        item { Spacer(Modifier.height(MessageGap)) }

        if (data.chatError != null) {
            item(key = "chat_error") {
                Box(modifier = calculateBoxModifier()) {
                    ChatErrorItem(
                        error = data.chatError,
                        onRetry = data.onRetryChatError,
                        onDismiss = data.onDismissChatError,
                    )
                }
            }
        }

        itemsIndexed(
            items = items,
            key = { index, item -> item.message?.id ?: item.dateLabel ?: "sep-$index" },
        ) { _, item ->
            when {
                item.dateLabel != null -> DateSeparator(label = item.dateLabel)
                item.message != null -> {
                    val isGreeting = item.message.role == MessageRole.Assistant &&
                            item.message.id == firstAssistantId
                    MessageItem(
                        message = item.message,
                        isGreeting = isGreeting,
                        itemContext = itemContext,
                    )
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
private fun MessageItem(
    message: Message,
    isGreeting: Boolean,
    itemContext: MessageItemContext,
) {
    val interactionState = when {
        itemContext.editingMessageId == message.id -> MessageInteractionState.Editing(
            itemContext.editingText
        )
        itemContext.isGenerating -> MessageInteractionState.Generating
        itemContext.selectedMessageIds.isNotEmpty() -> MessageInteractionState.Selecting(
            message.id in itemContext.selectedMessageIds
        )
        else -> MessageInteractionState.Browsing(itemContext.expandedActionsMessageId == message.id)
    }

    Box(modifier = calculateBoxModifier()) {
        when (message.role) {
            MessageRole.User -> UserMessage(
                message = message,
                interactionState = interactionState,
                messageWidth = itemContext.messageWidth,
                onActionRowEvent = { event -> itemContext.onActionRowEvent(message.id, event) },
                onEditFieldEvent = { event -> itemContext.onEditFieldEvent(message.id, event) },
                onMessageItemEvent = { event -> itemContext.onMessageItemEvent(message.id, event) },
            )
            MessageRole.Assistant -> AssistantMessage(
                message = message,
                interactionState = interactionState,
                character = itemContext.character,
                messageWidth = itemContext.messageWidth,
                isGreeting = isGreeting,
                greetingMessageId = itemContext.greetingMessageId,
                onActionRowEvent = { event -> itemContext.onActionRowEvent(message.id, event) },
                onEditFieldEvent = { event -> itemContext.onEditFieldEvent(message.id, event) },
                onMessageItemEvent = { event -> itemContext.onMessageItemEvent(message.id, event) },
            )
            MessageRole.System -> Unit
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
