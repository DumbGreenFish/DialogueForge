package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.data.cache.ImageCache
import io.github.dumbgreenfish.dialogueforge.data.model.TavernCardData
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterEntity
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterRepository
import io.github.dumbgreenfish.dialogueforge.design.DialogueForgeTheme
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.MessageWidth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.KoinApplication
import org.koin.dsl.module
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertFalse

@OptIn(ExperimentalTestApi::class)
class MessagesListRuntimeMarkdownCameraTest {
    @Test
    fun runtime_markdown_updates_do_not_move_camera_in_either_direction_after_small_detach() =
        runComposeUiTest {
            val listState = LazyListState()
            val messages = mutableStateOf(messagesWithStreamingText(InitialStreamingMarkdown))

            setContent {
                TestMessagesList(
                    messages = messages.value,
                    listState = listState,
                )
            }

            onNodeWithText(StreamingHeading).assertIsDisplayed()
            onNodeWithTag(MessagesListTag).performTouchInput {
                down(center)
                advanceEventTime(DetachGestureDurationMillis)
                moveTo(percentOffset(DragCenterX, DragEndY))
                up()
            }
            waitForIdle()

            val anchorNode = onNodeWithText(PersistedAnchorText, substring = true)
            anchorNode.assertIsDisplayed()
            val anchorTop = anchorNode.fetchSemanticsNode().boundsInRoot.top
            val anchorKey = listState.firstVisibleKey()
            assertFalse(listState.isAtBottom())

            var cameraMoved = false
            runOnUiThread {
                messages.value = messagesWithStreamingText(UpdatedStreamingMarkdown)
            }
            waitUntil(
                conditionDescription = "updated runtime Markdown is rendered without moving the camera",
                timeoutMillis = MarkdownRenderTimeoutMillis,
            ) {
                val currentAnchorNodes = onAllNodesWithText(
                    PersistedAnchorText,
                    substring = true,
                ).fetchSemanticsNodes()
                val currentAnchorTop = currentAnchorNodes.singleOrNull()?.boundsInRoot?.top
                if (
                    listState.firstVisibleKey() != anchorKey ||
                    currentAnchorTop == null ||
                    abs(currentAnchorTop - anchorTop) > CameraPositionTolerancePx
                ) {
                    cameraMoved = true
                }

                onAllNodesWithText(UpdatedRuntimeMarker).fetchSemanticsNodes().isNotEmpty()
            }

            assertFalse(cameraMoved)
            assertFalse(listState.isAtBottom())
            anchorNode.assertIsDisplayed()
            assertFalse(
                abs(anchorNode.fetchSemanticsNode().boundsInRoot.top - anchorTop) >
                    CameraPositionTolerancePx,
            )
        }

    @Composable
    private fun TestMessagesList(
        messages: List<Message>,
        listState: LazyListState,
    ) {
        KoinApplication(application = { modules(TestModule) }) {
            DialogueForgeTheme {
                MessagesList(
                    data = MessagesListData(
                        messages = messages,
                        isLoadingOlder = false,
                        hasMoreOlderMessages = false,
                        onLoadOlder = {},
                    ),
                    itemContext = ItemContext,
                    modifier = ListModifier,
                    listState = listState,
                )
            }
        }
    }

    private fun messagesWithStreamingText(text: String): List<Message> = buildList {
        add(message(StreamingMessageId, text, MessageRole.Assistant, StreamingTimestamp))
        repeat(PersistedMessageCount) { index ->
            add(
                message(
                    id = "persisted-$index",
                    text = if (index == 0) {
                        "$PersistedAnchorText\n".repeat(PersistedMessageLineCount)
                    } else {
                        "Older message $index\n".repeat(PersistedMessageLineCount)
                    },
                    role = if (index % 2 == 0) MessageRole.User else MessageRole.Assistant,
                    timestamp = PersistedTimestamp,
                ),
            )
        }
    }

    private fun message(
        id: String,
        text: String,
        role: MessageRole,
        timestamp: Long,
    ) = Message(
        id = id,
        role = role,
        text = text,
        timestamp = timestamp,
    )

    private fun LazyListState.firstVisibleKey(): Any? =
        layoutInfo.visibleItemsInfo.firstOrNull { it.index == firstVisibleItemIndex }?.key

    private fun LazyListState.isAtBottom(): Boolean =
        firstVisibleItemIndex == BottomItemIndex && firstVisibleItemScrollOffset == 0

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

    private companion object {
        const val MessagesListTag = "runtime_markdown_camera_list"
        const val StreamingMessageId = "runtime-markdown-stream"
        const val StreamingHeading = "Runtime stream"
        const val PersistedAnchorText = "Stable camera anchor"
        const val UpdatedRuntimeMarker = "Updated runtime marker"
        const val StreamingTimestamp = 0L
        const val PersistedTimestamp = 8_640_000_000L
        const val PersistedMessageCount = 12
        const val PersistedMessageLineCount = 3
        const val BottomItemIndex = 0
        const val DetachGestureDurationMillis = 500L
        const val MarkdownRenderTimeoutMillis = 5_000L
        const val CameraPositionTolerancePx = 0.5f
        const val DragCenterX = 0.5f
        const val DragEndY = 0.68f

        const val InitialStreamingMarkdown =
            "# Runtime stream\n\nThe response is still generating."

        val UpdatedStreamingMarkdown = buildString {
            append(InitialStreamingMarkdown)
            append("\n\n")
            repeat(ReferenceDefinitionCount) { index ->
                append("[reference-")
                append(index)
                append("]: https://example.com/")
                append(index)
                append('\n')
            }
            append("\nUpdated runtime marker")
        }

        const val ReferenceDefinitionCount = 2_000

        val ListModifier = Modifier
            .width(420.dp)
            .height(480.dp)
            .testTag(MessagesListTag)

        val TestModule = module {
            single { ImageCache(FakeCharacterRepository()) }
        }

        val ItemContext = MessageItemContext(
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
