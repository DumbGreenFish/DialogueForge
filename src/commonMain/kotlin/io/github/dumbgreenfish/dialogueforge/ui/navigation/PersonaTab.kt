package io.github.dumbgreenfish.dialogueforge.ui.navigation

import androidx.compose.runtime.Composable
import io.github.dumbgreenfish.dialogueforge.ui.common.CompactScaffold
import io.github.dumbgreenfish.dialogueforge.ui.common.WideScaffold
import io.github.dumbgreenfish.dialogueforge.ui.common.isCompact
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab

class PersonaTab : NavBar<PersonaTab.Screen>(Screen.MainScreen) {
    override val tabEnum = NavTab.Persona

    sealed class Screen : NavScreen() {
        data object MainScreen : Screen() {
            @Composable override fun Render(onBack: () -> Unit) {
                if (isCompact) CompactScaffold(NavTab.Persona)
                else WideScaffold(NavTab.Persona)
            }
        }
    }
}
