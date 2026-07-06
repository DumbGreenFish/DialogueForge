package io.github.dumbgreenfish.dialogueforge.ui.navigation

import androidx.compose.runtime.Composable
import io.github.dumbgreenfish.dialogueforge.ui.common.CompactScaffold
import io.github.dumbgreenfish.dialogueforge.ui.common.TabletScaffold
import io.github.dumbgreenfish.dialogueforge.ui.common.WideScaffold
import io.github.dumbgreenfish.dialogueforge.ui.common.WindowClass
import io.github.dumbgreenfish.dialogueforge.ui.common.windowClass
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab

class PresetsTab : NavBar<PresetsTab.Screen>(Screen.MainScreen) {
    override val tabEnum = NavTab.Presets

    sealed class Screen : NavScreen() {
        data object MainScreen : Screen() {
            @Composable override fun Render(onBack: () -> Unit) {
                when (windowClass) {
                    WindowClass.Compact -> CompactScaffold(NavTab.Presets)
                    WindowClass.Tablet  -> TabletScaffold(NavTab.Presets)
                    WindowClass.Wide    -> WideScaffold(NavTab.Presets)
                }
            }
        }
    }
}
