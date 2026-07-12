package io.github.dumbgreenfish.dialogueforge.ui.navigation

import androidx.compose.runtime.Composable
import io.github.dumbgreenfish.dialogueforge.ui.common.ScaffoldForTab
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab

class PresetsTab : NavBar<PresetsTab.Screen>(Screen.MainScreen) {
    override val tabEnum = NavTab.Presets

    sealed class Screen : NavScreen() {
        data object MainScreen : Screen() {
            @Composable override fun Render(onBack: () -> Unit) {
                ScaffoldForTab(NavTab.Presets)
            }
        }
    }
}
