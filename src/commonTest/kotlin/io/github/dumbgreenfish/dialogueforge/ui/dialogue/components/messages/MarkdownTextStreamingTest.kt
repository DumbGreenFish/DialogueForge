package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.DialogueForgeTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class MarkdownTextStreamingTest {
    @Test
    fun visible_markdown_text_updates_before_message_completion() = runComposeUiTest {
        val text = mutableStateOf("First partial")
        setContent {
            DialogueForgeTheme {
                MarkdownText(
                    text = text.value,
                    color = Color.White,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                )
            }
        }

        waitUntil(
            conditionDescription = "initial Markdown partial is rendered",
            timeoutMillis = MarkdownRenderTimeoutMillis,
        ) {
            onAllNodesWithText("First partial").fetchSemanticsNodes().isNotEmpty()
        }
        onNodeWithText("First partial").assertIsDisplayed()
        runOnUiThread { text.value = "Expanded partial" }
        waitUntil(
            conditionDescription = "expanded Markdown partial is rendered",
            timeoutMillis = MarkdownRenderTimeoutMillis,
        ) {
            onAllNodesWithText("Expanded partial").fetchSemanticsNodes().isNotEmpty()
        }
        onNodeWithText("First partial").assertDoesNotExist()
        onNodeWithText("Expanded partial").assertIsDisplayed()
    }

    private companion object {
        const val MarkdownRenderTimeoutMillis = 5_000L
    }
}
