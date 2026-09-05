package io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs

import androidx.compose.runtime.Composable
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_category_ui
import io.github.dumbgreenfish.dialogueforge.ui.common.scaffold.TabScaffold
import io.github.dumbgreenfish.dialogueforge.ui.common.scaffold.TabScaffoldWithCustomTopBar
import io.github.dumbgreenfish.dialogueforge.ui.common.scaffold.TabScaffoldWithMainScreen
import io.github.dumbgreenfish.dialogueforge.ui.common.topbar.CustomTopBar
import io.github.dumbgreenfish.dialogueforge.ui.common.topbar.NamedCustomTopBar
import io.github.dumbgreenfish.dialogueforge.ui.settings.screen.UiSettingsView
import org.jetbrains.compose.resources.stringResource

class SettingsTab private constructor() : NavTab<SettingsTab.Screen>(Screen.MainScreen) {
    sealed class Screen : NavScreen() {
        data object MainScreen : Screen() {
            @Composable
            override fun Render(onBack: () -> Unit) {
                TabScaffoldWithMainScreen(NavTabs.Settings)
            }
        }

        data object UiSettingsScreen : Screen() {
            @Composable
            override fun Render(onBack: () -> Unit) {
                TabScaffoldWithCustomTopBar(NavTabs.Settings, { onMenuClick ->
                    NamedCustomTopBar(
                        onMenuClick = onMenuClick,
                        title = stringResource(Res.string.settings_category_ui),
                    )
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
