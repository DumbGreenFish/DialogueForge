package io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs

import androidx.compose.runtime.Composable
import io.github.dumbgreenfish.dialogueforge.ui.common.scaffold.TabScaffold
import io.github.dumbgreenfish.dialogueforge.ui.common.scaffold.TabScaffoldWithCustomTopBar
import io.github.dumbgreenfish.dialogueforge.ui.common.scaffold.TabScaffoldWithMainScreen
import io.github.dumbgreenfish.dialogueforge.ui.settings.screen.UiSettingsView

class SettingsTab private constructor(): NavTab<SettingsTab.Screen>(Screen.MainScreen) {
    sealed class Screen : NavScreen() {
        data object MainScreen : Screen() {
            @Composable override fun Render(onBack: () -> Unit) {
                TabScaffoldWithMainScreen(NavTabs.Settings)
            }
        }

        data class UiSettingsScreen(val title: String) : Screen() {
            @Composable
            override fun Render(onBack: () -> Unit) {
                TabScaffoldWithCustomTopBar(NavTabs.Settings, {

                }) {
                    UiSettingsView()
                }
            }
        }
    }

    companion object {
        val instance: SettingsTab by lazy {
            SettingsTab()
        }
    }
}
