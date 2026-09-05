package io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs

import androidx.compose.runtime.Composable
import io.github.dumbgreenfish.dialogueforge.ui.common.scaffold.TabScaffoldWithMainScreen

class PersonaTab private constructor(): NavTab<PersonaTab.Screen>(Screen.MainScreen) {
    sealed class Screen : NavScreen() {
        data object MainScreen : Screen() {
            @Composable override fun Render(onBack: () -> Unit) {
                TabScaffoldWithMainScreen(NavTabs.Persona)
            }
        }
    }

    companion object {
        val instance : PersonaTab by lazy {
            PersonaTab()
        }
    }
}
