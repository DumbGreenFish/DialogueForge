package io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs

import androidx.compose.runtime.Composable
import io.github.dumbgreenfish.dialogueforge.ui.common.scaffold.TabScaffoldWithMainScreen

class PresetsTab private constructor(): NavTab<PresetsTab.Screen>(Screen.MainScreen) {
    sealed class Screen : NavScreen() {
        data object MainScreen : Screen() {
            @Composable override fun Render(onBack: () -> Unit) {
                TabScaffoldWithMainScreen(NavTabs.Presets)
            }
        }
    }

    companion object {
        val instance : PresetsTab by lazy {
            PresetsTab()
        }
    }
}
