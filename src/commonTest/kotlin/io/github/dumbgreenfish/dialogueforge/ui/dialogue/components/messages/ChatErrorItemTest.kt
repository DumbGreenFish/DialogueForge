package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.DialogueForgeTheme
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_error_dismiss
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_error_retry
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_error_server
import io.github.dumbgreenfish.dialogueforge.testing.TestStrings
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatError
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatErrorType
import kotlin.test.Test
import kotlin.test.assertTrue

private val ExpandedDetailsMinimumHeight = 110.dp

@OptIn(ExperimentalTestApi::class)
class ChatErrorItemTest {
    @Test
    fun server_error_shows_complete_details_and_invokes_actions() = runComposeUiTest {
        val details = (1..8).joinToString("\n") { "Diagnostic line $it" }
        var retried = false
        var dismissed = false
        val strings = TestStrings(
            Res.string.dialogue_error_server,
            Res.string.dialogue_error_retry,
            Res.string.dialogue_error_dismiss,
        )

        setContent {
            strings.provide {
                DialogueForgeTheme {
                    ChatErrorItem(
                        error = ChatError(ChatErrorType.Server, details),
                        onRetry = { retried = true },
                        onDismiss = { dismissed = true },
                    )
                }
            }
        }

        onNodeWithText(strings[Res.string.dialogue_error_server]).assertIsDisplayed()
        onNodeWithText(details).assertIsDisplayed().assertHeightIsAtLeast(ExpandedDetailsMinimumHeight)
        onNodeWithText(strings[Res.string.dialogue_error_retry]).performClick()
        onNodeWithText(strings[Res.string.dialogue_error_dismiss]).performClick()

        assertTrue(retried)
        assertTrue(dismissed)
    }
}
