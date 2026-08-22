package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.DialogueForgeTheme
import io.github.dumbgreenfish.dialogueforge.data.cache.ImageCache
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterEntity
import io.github.dumbgreenfish.dialogueforge.data.model.TavernCardData
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.MessageWidth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.koin.compose.KoinApplication
import org.koin.dsl.module

@OptIn(ExperimentalTestApi::class)
class MessagesListStreamingScrollTest {
    @Test
    fun streaming_row_is_separate_from_persisted_message_grouping() = runComposeUiTest {
        val listState = LazyListState()

        setContent {
            TestMessagesList(
                messages = messagesWithStreamingText(StreamingMarkdownChunks.first()),
                listState = listState,
            )
        }

        waitForIdle()

        assertEquals(ExpectedSeparatedLazyItemCount, listState.layoutInfo.totalItemsCount)
    }

    @Test
    fun streaming_updates_during_drag_do_not_cancel_user_detachment() = runComposeUiTest {
        val listState = LazyListState()
        val messages = mutableStateOf(messagesWithStreamingText(StreamingMarkdownChunks.first()))

        setContent { TestMessagesList(messages = messages.value, listState = listState) }

        waitForIdle()
        assertEquals(NewestItemIndex, listState.firstVisibleItemIndex)

        onNodeWithTag(MessagesListTag).performTouchInput { down(center) }
        repeat(StreamingUpdateCount) { update ->
            runOnUiThread {
                messages.value = messagesWithStreamingText(streamingText(update + 1))
            }
            onNodeWithTag(MessagesListTag).performTouchInput {
                advanceEventTime(DragStepDurationMillis)
                moveTo(percentOffset(DragCenterX, DragStartY + DragStepY * (update + 1)))
            }
        }
        onNodeWithTag(MessagesListTag).performTouchInput { up() }
        waitForIdle()

        assertFalse(listState.position().isAtBottom)
    }

    @Test
    fun streaming_updates_do_not_reclaim_or_move_scroll_after_user_swipes_up() = runComposeUiTest {
        val listState = LazyListState()
        val messages = mutableStateOf(messagesWithStreamingText(StreamingMarkdownChunks.first()))

        setContent { TestMessagesList(messages = messages.value, listState = listState) }

        waitForIdle()
        assertEquals(NewestItemIndex, listState.firstVisibleItemIndex)

        onNodeWithTag(MessagesListTag).performTouchInput { swipeDown() }
        waitForIdle()

        val detachedPosition = listState.position()
        assertFalse(detachedPosition.isAtBottom)

        repeat(StreamingUpdateCount) { update ->
            runOnUiThread {
                messages.value = messagesWithStreamingText(streamingText(update + 1))
            }
            waitForIdle()

            val currentPosition = listState.position()
            assertFalse(currentPosition.isAtBottom)
            assertEquals(detachedPosition.index, currentPosition.index)
            assertEquals(detachedPosition.offset, currentPosition.offset)
        }
    }

    @Test
    fun streaming_updates_keep_camera_at_bottom_when_user_has_not_detached() = runComposeUiTest {
        val listState = LazyListState()
        val messages = mutableStateOf(messagesWithStreamingText(StreamingMarkdownChunks.first()))

        setContent { TestMessagesList(messages = messages.value, listState = listState) }

        repeat(StreamingUpdateCount) { update ->
            runOnUiThread {
                messages.value = messagesWithStreamingText(streamingText(update + 1))
            }
            waitForIdle()

            assertTrue(listState.position().isAtBottom)
        }
    }

    @Test
    fun completed_stream_does_not_move_camera_after_user_detaches() = runComposeUiTest {
        val listState = LazyListState()
        val messages = mutableStateOf(messagesWithStreamingText(StreamingMarkdownChunks.first()))

        setContent { TestMessagesList(messages = messages.value, listState = listState) }

        waitForIdle()
        onNodeWithTag(MessagesListTag).performTouchInput { swipeDown() }
        waitForIdle()
        val detachedAnchor = listState.anchor()
        assertFalse(listState.position().isAtBottom)

        runOnUiThread {
            messages.value = messagesWithStreamingText(StreamingMarkdownChunks.last()).mapIndexed { index, message ->
                if (index == NewestItemIndex) message.copy(id = "completed-response") else message
            }
        }
        waitForIdle()

        assertEquals(detachedAnchor, listState.anchor())
    }

    @Test
    fun returning_to_bottom_reattaches_camera_to_streaming_updates() = runComposeUiTest {
        val listState = LazyListState()
        val messages = mutableStateOf(messagesWithStreamingText(StreamingMarkdownChunks.first()))

        setContent { TestMessagesList(messages = messages.value, listState = listState) }

        waitForIdle()
        onNodeWithTag(MessagesListTag).performTouchInput { swipeDown() }
        waitForIdle()
        assertFalse(listState.position().isAtBottom)

        repeat(ReturnToBottomSwipeCount) {
            if (!listState.position().isAtBottom) {
                onNodeWithTag(MessagesListTag).performTouchInput { swipeUp() }
                waitForIdle()
            }
        }
        assertTrue(listState.position().isAtBottom)

        runOnUiThread {
            messages.value = messagesWithStreamingText(streamingText(StreamingUpdateCount))
        }
        waitForIdle()

        assertTrue(listState.position().isAtBottom)
    }

    @Composable
    private fun TestMessagesList(
        messages: List<Message>,
        listState: LazyListState,
    ) {
        KoinApplication(application = { modules(TestModule) }) {
            DialogueForgeTheme {
                MessagesList(
                    data = messagesListData(messages),
                    itemContext = itemContext,
                    modifier = listModifier,
                    listState = listState,
                )
            }
        }
    }

    private fun messagesListData(messages: List<Message>) = MessagesListData(
        messages = messages,
        isLoadingOlder = false,
        hasMoreOlderMessages = false,
        onLoadOlder = {},
    )

    private fun streamingText(updateCount: Int): String = StreamingMarkdownChunks[updateCount]

    private fun messagesWithStreamingText(text: String): List<Message> = buildList {
        add(message(StreamingMessageId, text, MessageRole.User))
        repeat(PersistedMessageCount) { index ->
            add(
                message(
                    id = "persisted-$index",
                    text = if (index % 2 == 0) {
                        "Older user message $index\n".repeat(PersistedMessageLineCount)
                    } else {
                        "## Older assistant message $index\n\n" +
                            "- retained item\n".repeat(PersistedMessageLineCount)
                    },
                    role = if (index % 2 == 0) MessageRole.User else MessageRole.Assistant,
                ),
            )
        }
    }

    private fun message(id: String, text: String, role: MessageRole) = Message(
        id = id,
        role = role,
        text = text,
        timestamp = if (id == StreamingMessageId) StreamingTimestamp else PersistedTimestamp,
    )

    private class FakeCharacterRepository : CharacterRepository {
        override val characters: Flow<List<CharacterEntity>> = MutableStateFlow(emptyList())
        override suspend fun getById(id: String): CharacterEntity? = null
        override suspend fun import(data: TavernCardData) = Unit
        override suspend fun delete(id: String) = Unit
        override suspend fun togglePin(id: String) = Unit
        override suspend fun getMainImageThumbnail(id: String): ByteArray? = null
        override suspend fun getFullMainImage(id: String): ByteArray? = null
        override suspend fun getSizedThumbnail(id: String, maxDimension: Int): ByteArray? = null
        override suspend fun existsByName(name: String): Boolean = false
    }

    private fun LazyListState.position() = ScrollPosition(
        index = firstVisibleItemIndex,
        offset = firstVisibleItemScrollOffset,
    )

    private fun LazyListState.anchor() = ScrollAnchor(
        key = checkNotNull(
            layoutInfo.visibleItemsInfo.firstOrNull { it.index == firstVisibleItemIndex }?.key,
        ),
        offset = firstVisibleItemScrollOffset,
    )

    private data class ScrollPosition(
        val index: Int,
        val offset: Int,
    ) {
        val isAtBottom: Boolean
            get() = index == NewestItemIndex && offset == 0
    }

    private data class ScrollAnchor(
        val key: Any,
        val offset: Int,
    )

    private companion object {
        const val MessagesListTag = "messages_list_streaming_scroll"
        const val NewestItemIndex = 0
        const val PersistedMessageCount = 12
        const val PersistedMessageLineCount = 4
        const val ReturnToBottomSwipeCount = 3
        const val StreamingMessageId = "streaming-test"
        const val StreamingTimestamp = 0L
        const val PersistedTimestamp = 8_640_000_000L
        const val DragStepDurationMillis = 32L

        const val ExpectedSeparatedLazyItemCount = PersistedMessageCount + 3

        const val DragCenterX = 0.5f
        const val DragStartY = 0.5f
        const val DragStepY = 0.1f

        val StreamingMarkdownChunks = buildList {
            var markdown = "# Runtime stream"
            add(markdown)
            markdown += "\n\nRuntime marker 1\n\n**bold span"
            add(markdown)
            markdown += " continues**\n\nRuntime marker 2\n\n- first item"
            add(markdown)
            markdown += "\n- second item\n\nRuntime marker 3\n\n```kotlin\nfun answer() {"
            add(markdown)
            markdown += "\n    println(\"stream\")\n}\n```\n\nRuntime marker 4"
            add(markdown)
        }

        val StreamingUpdateCount = StreamingMarkdownChunks.lastIndex

        val ListWidth = 420.dp
        val ListHeight = 360.dp

        val listModifier = Modifier
            .width(ListWidth)
            .height(ListHeight)
            .testTag(MessagesListTag)

        val TestModule = module {
            single { ImageCache(FakeCharacterRepository()) }
        }

        val itemContext = MessageItemContext(
            character = Character(
                id = "character",
                name = "Character",
                tagline = "",
                tags = emptyList(),
                chats = 0,
                lastUsed = "",
                pinned = false,
                source = "test",
            ),
            isGenerating = true,
            messageWidth = MessageWidth.Normal,
            expandedActionsMessageId = null,
            editingMessageId = null,
            editingText = TextFieldValue(),
            selectedMessageIds = emptySet(),
            greetingMessageId = null,
            onActionRowEvent = { _, _ -> },
            onEditFieldEvent = { _, _ -> },
            onMessageItemEvent = { _, _ -> },
            onAvatarClick = {},
        )
    }
}
