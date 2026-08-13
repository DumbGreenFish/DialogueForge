package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.setup

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.github.dumbgreenfish.dialogueforge.design.DialogueForgeTheme
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.background_work_continue
import io.github.dumbgreenfish.dialogueforge.generated.resources.background_work_open_settings
import io.github.dumbgreenfish.dialogueforge.generated.resources.background_work_text
import io.github.dumbgreenfish.dialogueforge.generated.resources.background_work_title
import io.github.dumbgreenfish.dialogueforge.testing.TestStrings
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class BackgroundWorkDialogTest {
    @Test
    fun actions_open_settings_or_continue() = runComposeUiTest {
        var openedSettings = false
        var continued = false
        val strings = TestStrings(
            Res.string.background_work_title,
            Res.string.background_work_text,
            Res.string.background_work_open_settings,
            Res.string.background_work_continue,
        )

        setContent {
            strings.provide {
                DialogueForgeTheme {
                    BackgroundWorkDialog(
                        onOpenSettings = { openedSettings = true },
                        onContinue = { continued = true },
                    )
                }
            }
        }

        onNodeWithText(strings[Res.string.background_work_open_settings]).performClick()
        onNodeWithText(strings[Res.string.background_work_continue]).performClick()

        assertTrue(openedSettings)
        assertTrue(continued)
    }
}
