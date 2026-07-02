package io.github.dumbgreenfish.dialogueforge.ui.navigation

import androidx.compose.runtime.Composable
import io.github.dumbgreenfish.dialogueforge.ui.common.CompactScaffold
import io.github.dumbgreenfish.dialogueforge.ui.common.WideScaffold
import io.github.dumbgreenfish.dialogueforge.ui.common.isCompact
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab

class SettingsTab : NavBar<SettingsTab.Screen>(Screen.MainScreen) {
    override val tabEnum = NavTab.Settings

    sealed class Screen : NavScreen() {
        data object MainScreen : Screen() {
            @Composable override fun Render(onBack: () -> Unit) {
                if (isCompact) CompactScaffold(NavTab.Settings)
                else WideScaffold(NavTab.Settings)
            }
        }
    }
}
