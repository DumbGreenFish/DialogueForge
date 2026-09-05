package io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import io.github.dumbgreenfish.dialogueforge.ui.common.scaffold.TabScaffoldWithMainScreen
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.DialogueView

class CharactersTab private constructor(): NavTab<CharactersTab.Screen>(Screen.MainScreen) {
    sealed class Screen : NavScreen() {
        data object MainScreen : Screen() {
            @Composable override fun Render(onBack: () -> Unit) {
                TabScaffoldWithMainScreen(NavTabs.Characters)
            }
        }
        class ChatScreen(val characterId: String) : Screen() {
            internal val entryId: Long = _entryCounter++
            @Composable override fun Render(onBack: () -> Unit) {
                key(entryId) {
                    DialogueView(characterId = characterId, onBack = onBack)
                }
            }

            companion object { private var _entryCounter = 0L }
        }
    }

    companion object {
        val instance : CharactersTab by lazy {
            CharactersTab()
        }
    }
}
