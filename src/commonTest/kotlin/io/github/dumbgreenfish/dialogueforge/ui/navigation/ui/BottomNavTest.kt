package io.github.dumbgreenfish.dialogueforge.ui.navigation.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.github.dumbgreenfish.dialogueforge.design.DialogueForgeTheme
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.nav_characters
import io.github.dumbgreenfish.dialogueforge.generated.resources.nav_persona
import io.github.dumbgreenfish.dialogueforge.generated.resources.nav_presets
import io.github.dumbgreenfish.dialogueforge.generated.resources.nav_settings
import io.github.dumbgreenfish.dialogueforge.testing.TestStrings
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class BottomNavTest {
    @Test
    fun displaysAllTabsAndChangesSelection() = runComposeUiTest {
        var selected by mutableStateOf(NavTab.Characters)
        val strings = TestStrings(
            Res.string.nav_characters,
            Res.string.nav_persona,
            Res.string.nav_presets,
            Res.string.nav_settings,
        )

        setContent {
            strings.provide {
                DialogueForgeTheme {
                    ForgeBottomNav(
                        selected = selected,
                        onSelect = { selected = it },
                    )
                }
            }
        }

        onNodeWithText(strings[Res.string.nav_characters]).assertIsDisplayed().assertIsSelected()
        onNodeWithText(strings[Res.string.nav_persona]).assertIsDisplayed()
        onNodeWithText(strings[Res.string.nav_presets]).assertIsDisplayed()
        onNodeWithText(strings[Res.string.nav_settings]).assertIsDisplayed().assertIsNotSelected().performClick()

        assertEquals(NavTab.Settings, selected)
        onNodeWithText(strings[Res.string.nav_settings]).assertIsSelected()
        onNodeWithText(strings[Res.string.nav_characters]).assertIsNotSelected()
    }
}
