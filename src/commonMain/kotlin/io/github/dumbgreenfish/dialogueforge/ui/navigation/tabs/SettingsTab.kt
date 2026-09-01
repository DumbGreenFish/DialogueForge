package io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs

import androidx.compose.runtime.Composable
import io.github.dumbgreenfish.dialogueforge.ui.common.ScaffoldForTab

class SettingsTab private constructor(): NavTab<SettingsTab.Screen>(Screen.MainScreen) {
    sealed class Screen : NavScreen() {
        data object MainScreen : Screen() {
            @Composable override fun Render(onBack: () -> Unit) {
                ScaffoldForTab(NavTabs.Settings)
            }
        }
    }

    companion object {
        val instance: SettingsTab by lazy {
            SettingsTab()
        }
    }
}
