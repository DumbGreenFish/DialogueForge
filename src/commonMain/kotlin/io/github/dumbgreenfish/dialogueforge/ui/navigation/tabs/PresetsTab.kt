package io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs

import androidx.compose.runtime.Composable
import io.github.dumbgreenfish.dialogueforge.ui.common.ScaffoldForTab

class PresetsTab private constructor(): NavTab<PresetsTab.Screen>(Screen.MainScreen) {
    sealed class Screen : NavScreen() {
        data object MainScreen : Screen() {
            @Composable override fun Render(onBack: () -> Unit) {
                ScaffoldForTab(NavTabs.Presets)
            }
        }
    }

    companion object {
        val instance : PresetsTab by lazy {
            PresetsTab()
        }
    }
}
