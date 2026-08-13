package io.github.dumbgreenfish.dialogueforge.ui.characters.components.menu

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.github.dumbgreenfish.dialogueforge.design.DialogueForgeTheme
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.character_delete_cancel
import io.github.dumbgreenfish.dialogueforge.generated.resources.character_delete_confirm
import io.github.dumbgreenfish.dialogueforge.generated.resources.character_delete_message
import io.github.dumbgreenfish.dialogueforge.generated.resources.character_delete_title
import io.github.dumbgreenfish.dialogueforge.testing.TestStrings
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class DeleteCharacterDialogTest {
    @Test
    fun confirm_action_invokes_callback() = runComposeUiTest {
        var confirmed = false
        val strings = deleteCharacterStrings()

        setContent {
            strings.provide {
                DialogueForgeTheme {
                    DeleteCharacterDialog(
                        onConfirm = { confirmed = true },
                        onDismiss = {},
                    )
                }
            }
        }

        onNodeWithText(strings[Res.string.character_delete_title]).assertIsDisplayed()
        onNodeWithText(strings[Res.string.character_delete_message])
            .assertIsDisplayed()
        onNodeWithText(strings[Res.string.character_delete_confirm]).performClick()

        assertTrue(confirmed)
    }

    @Test
    fun dismiss_action_invokes_callback() = runComposeUiTest {
        var dismissed = false
        val strings = deleteCharacterStrings()

        setContent {
            strings.provide {
                DialogueForgeTheme {
                    DeleteCharacterDialog(
                        onConfirm = {},
                        onDismiss = { dismissed = true },
                    )
                }
            }
        }

        onNodeWithText(strings[Res.string.character_delete_cancel]).assertIsDisplayed().performClick()

        assertTrue(dismissed)
    }

    private fun deleteCharacterStrings() = TestStrings(
        Res.string.character_delete_title,
        Res.string.character_delete_message,
        Res.string.character_delete_confirm,
        Res.string.character_delete_cancel,
    )
}
