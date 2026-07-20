package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeAnimation
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
    val density = LocalDensity.current
    var viewportHeightDp by remember { mutableStateOf(0.dp) }
    var greetingHeightDp by remember { mutableStateOf(0.dp) }
    var hasEverMeasured by remember { mutableStateOf(false) }

    val hasUserMessages = data.messages.any { it.role == MessageRole.User }
    val isOnlyGreeting = !hasUserMessages && data.messages.size == 1
    val items = remember(data.messages, isOnlyGreeting) { buildItems(data.messages, isOnlyGreeting) }
    val firstAssistantId = data.messages.firstOrNull { it.role == MessageRole.Assistant }?.id

    val isMeasured = viewportHeightDp > 0.dp && greetingHeightDp > 0.dp
    LaunchedEffect(isMeasured) { if (isMeasured) hasEverMeasured = true }

    val centeredSpacerHeight = if (isOnlyGreeting && isMeasured) {
        ((viewportHeightDp - greetingHeightDp) / 2 - ContentPaddingV).coerceAtLeast(0.dp)
    } else {
        MessageGap
    }

    val bottomSpacerHeight by animateDpAsState(
        targetValue = centeredSpacerHeight,
        animationSpec = if (isOnlyGreeting && !hasEverMeasured) snap() else tween(ForgeAnimation.DurationStateTransition),
        label = "bottomSpacer",
    )

    val contentAlpha = if (isOnlyGreeting && !isMeasured) 0f else 1f

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

    Box(modifier = Modifier.fillMaxSize().graphicsLayer(alpha = contentAlpha)) {
    LazyColumn(
        state = listState,
        reverseLayout = true,
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { viewportHeightDp = with(density) { it.height.toDp() } },
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = ContentPaddingV),
    ) {
        item { Spacer(Modifier.height(bottomSpacerHeight)) }

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
                    val message = item.message
                    val isGreeting = isOnlyGreeting &&
                            message.role == MessageRole.Assistant &&
                            message.id == firstAssistantId
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
                    Box(modifier = calculateBoxModifier()
                        .then(
                            if (isGreeting && isOnlyGreeting) {
                                Modifier.onSizeChanged { greetingHeightDp = with(density) { it.height.toDp() } }
                            } else Modifier
                        )
                    ) {
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

private fun buildItems(messages: List<Message>, isOnlyGreeting: Boolean): List<ChatItem> {
    if (isOnlyGreeting) return messages.map { ChatItem(dateLabel = null, message = it) }
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
