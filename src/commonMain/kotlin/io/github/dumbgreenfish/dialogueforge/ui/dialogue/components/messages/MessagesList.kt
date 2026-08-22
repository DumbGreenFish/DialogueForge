package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.common.WindowClass
import io.github.dumbgreenfish.dialogueforge.ui.common.formatDateLabel
import io.github.dumbgreenfish.dialogueforge.ui.common.windowClass
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.scaffold.DialogueLayout
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.isStreamingMessage
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatError
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.MessageWidth

private val ContentPaddingV = 24.dp
private val LoadMoreThreshold = 2
private const val StreamingMessageTimestamp = 0L
private const val CameraDebugLoggingEnabled = false
private data class ChatItem(val dateLabel: String?, val message: Message?)
private class CameraDebugGeometryState(var lastSnapshot: String? = null)
private class StreamingRowLayoutState(
    attachedHeight: Int = 0,
) {
    var attachedHeight by mutableIntStateOf(attachedHeight)
}
private class StreamingCameraState {
    var isDetached by mutableStateOf(false)
    var isUserDragActive by mutableStateOf(false)
    var isUserScrollSessionActive by mutableStateOf(false)
}

private data class UserScrollSnapshot(
    val isAtBottom: Boolean,
    val isScrollInProgress: Boolean,
    val isUserDragActive: Boolean,
    val isUserScrollSessionActive: Boolean,
)

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
    val onAvatarClick: () -> Unit,
)

@Composable
internal fun MessagesList(
    data: MessagesListData,
    itemContext: MessageItemContext,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    val isOnlyGreeting = data.messages.singleOrNull()?.let { message ->
        usesGreetingPresentation(data.messages, message)
    } == true

    if (isOnlyGreeting) {
        val greetingMessage = data.messages.first()
        BoxWithConstraints(modifier = modifier.fillMaxSize()) {
            val viewportHeight = maxHeight - ContentPaddingV * 2
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(vertical = ContentPaddingV),
            ) {
                item {
                    Box(
                        modifier = Modifier.heightIn(min = viewportHeight),
                        contentAlignment = Alignment.Center,
                    ) {
                        MessageItem(
                            message = greetingMessage,
                            isGreeting = true,
                            itemContext = itemContext,
                        )
                    }
                }
            }
        }
        return
    }

    val streamingMessage = data.messages.firstOrNull()?.takeIf { message ->
        message.isStreamingMessage() ||
            itemContext.isGenerating &&
            message.role == MessageRole.Assistant &&
            message.timestamp == StreamingMessageTimestamp
    }
    val persistedMessages = if (streamingMessage == null) data.messages else data.messages.drop(1)
    val items = remember(persistedMessages) { buildItems(persistedMessages) }
    val streamingRowLayoutState = remember(streamingMessage?.id) { StreamingRowLayoutState() }
    val streamingCameraState = remember(listState) { StreamingCameraState() }

    LaunchedEffect(listState, streamingMessage?.id) {
        listState.interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is DragInteraction.Start -> {
                    streamingMessage?.id?.let { streamingMessageId ->
                        listState.layoutInfo.visibleItemsInfo
                            .firstOrNull { item -> item.key == streamingMessageId }
                            ?.let { item -> streamingRowLayoutState.attachedHeight = item.size }
                    }
                    streamingCameraState.isUserDragActive = true
                    streamingCameraState.isUserScrollSessionActive = true
                    cameraDebugLog { "drag=start ${listState.cameraDebugSnapshot()}" }
                }
                is DragInteraction.Stop,
                is DragInteraction.Cancel,
                -> {
                    streamingCameraState.isUserDragActive = false
                    cameraDebugLog { "drag=end ${listState.cameraDebugSnapshot()}" }
                }
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            UserScrollSnapshot(
                isAtBottom = listState.isAtBottom(),
                isScrollInProgress = listState.isScrollInProgress,
                isUserDragActive = streamingCameraState.isUserDragActive,
                isUserScrollSessionActive = streamingCameraState.isUserScrollSessionActive,
            )
        }.collect { snapshot ->
            cameraDebugLog {
                "scroll=$snapshot detached=${streamingCameraState.isDetached} " +
                    listState.cameraDebugSnapshot()
            }
            if (snapshot.isUserScrollSessionActive) {
                streamingCameraState.isDetached = !snapshot.isAtBottom
                if (!snapshot.isUserDragActive && !snapshot.isScrollInProgress) {
                    streamingCameraState.isUserScrollSessionActive = false
                }
            }
        }
    }

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

        if (streamingMessage != null) {
            item(key = streamingMessage.id) {
                val shouldRetainStreamingHeight =
                    streamingCameraState.isDetached ||
                        streamingCameraState.isUserScrollSessionActive && !listState.isAtBottom()
                MessageItem(
                    message = streamingMessage,
                    isGreeting = false,
                    itemContext = itemContext,
                    modifier = Modifier.retainStreamingRowHeightWhileDetached(
                        layoutState = streamingRowLayoutState,
                        shouldRetainHeight = shouldRetainStreamingHeight,
                    ),
                )
            }
        }

        itemsIndexed(
            items = items,
            key = { index, item -> item.message?.id ?: item.dateLabel ?: "sep-$index" },
        ) { _, item ->
            when {
                item.dateLabel != null -> DateSeparator(label = item.dateLabel)
                item.message != null -> {
                    MessageItem(
                        message = item.message,
                        isGreeting = usesGreetingPresentation(data.messages, item.message),
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
private fun Modifier.retainStreamingRowHeightWhileDetached(
    layoutState: StreamingRowLayoutState,
    shouldRetainHeight: Boolean,
): Modifier {
    val retainedHeight = layoutState.attachedHeight
    cameraDebugLog { "height retain=$shouldRetainHeight retained=$retainedHeight" }
    if (!shouldRetainHeight || retainedHeight == 0) return this
    val retainedHeightDp = with(LocalDensity.current) { retainedHeight.toDp() }
    return height(retainedHeightDp).wrapContentHeight(
        align = Alignment.Top,
        unbounded = true,
    )
}

private fun LazyListState.isAtBottom(): Boolean =
    firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0

private fun LazyListState.cameraDebugSnapshot(): String {
    val visibleItems = layoutInfo.visibleItemsInfo.joinToString(",") { item ->
        "${item.index}:${item.key}@${item.offset}+${item.size}"
    }
    return "first=$firstVisibleItemIndex/$firstVisibleItemScrollOffset visible=[$visibleItems]"
}

private inline fun cameraDebugLog(message: () -> String) {
    if (CameraDebugLoggingEnabled) println("DF_CAMERA ${message()}")
}

internal fun usesGreetingPresentation(messages: List<Message>, message: Message): Boolean {
    val onlyMessage = messages.singleOrNull() ?: return false
    return onlyMessage.id == message.id && onlyMessage.role == MessageRole.Assistant
}

@Composable
private fun MessageItem(
    message: Message,
    isGreeting: Boolean,
    itemContext: MessageItemContext,
    modifier: Modifier = Modifier,
) {
    val cameraDebugGeometryState = remember(message.id) { CameraDebugGeometryState() }
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

    Box(
        modifier = modifier.then(calculateBoxModifier()).cameraDebugGeometry(
            label = message.id,
            state = cameraDebugGeometryState,
        ),
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
                onAvatarClick = itemContext.onAvatarClick,
            )
            MessageRole.System -> Unit
        }
    }
}

private fun Modifier.cameraDebugGeometry(
    label: String,
    state: CameraDebugGeometryState,
): Modifier {
    if (!CameraDebugLoggingEnabled) return this
    return onGloballyPositioned { coordinates ->
        val position = coordinates.positionInRoot()
        val snapshot = "$label top=${position.y} left=${position.x} size=${coordinates.size}"
        if (snapshot != state.lastSnapshot) {
            state.lastSnapshot = snapshot
            cameraDebugLog { "geometry $snapshot" }
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
