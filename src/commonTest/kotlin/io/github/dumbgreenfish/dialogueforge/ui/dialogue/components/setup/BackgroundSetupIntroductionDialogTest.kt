package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.setup

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.github.dumbgreenfish.dialogueforge.design.DialogueForgeTheme
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.background_setup_accept
import io.github.dumbgreenfish.dialogueforge.generated.resources.background_setup_decline
import io.github.dumbgreenfish.dialogueforge.generated.resources.background_setup_text
import io.github.dumbgreenfish.dialogueforge.generated.resources.background_setup_title
import io.github.dumbgreenfish.dialogueforge.testing.TestStrings
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class BackgroundSetupIntroductionDialogTest {
    @Test
    fun actions_accept_or_decline_guided_setup() = runComposeUiTest {
        var accepted = false
        var declined = false
        val strings = TestStrings(
            Res.string.background_setup_title,
            Res.string.background_setup_text,
            Res.string.background_setup_accept,
            Res.string.background_setup_decline,
        )

        setContent {
            strings.provide {
                DialogueForgeTheme {
                    BackgroundSetupIntroductionDialog(
                        onAccept = { accepted = true },
                        onDecline = { declined = true },
                    )
                }
            }
        }

        onNodeWithText(strings[Res.string.background_setup_accept]).performClick()
        onNodeWithText(strings[Res.string.background_setup_decline]).performClick()

        assertTrue(accepted)
        assertTrue(declined)
    }
}
