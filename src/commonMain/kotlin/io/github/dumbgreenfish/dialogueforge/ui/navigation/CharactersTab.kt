package io.github.dumbgreenfish.dialogueforge.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import io.github.dumbgreenfish.dialogueforge.ui.common.CompactScaffold
import io.github.dumbgreenfish.dialogueforge.ui.common.TabletScaffold
import io.github.dumbgreenfish.dialogueforge.ui.common.WideScaffold
import io.github.dumbgreenfish.dialogueforge.ui.common.WindowClass
import io.github.dumbgreenfish.dialogueforge.ui.common.windowClass
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.DialogueView
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab

class CharactersTab : NavBar<CharactersTab.Screen>(Screen.MainScreen) {
    override val tabEnum = NavTab.Characters

    sealed class Screen : NavScreen() {
        data object MainScreen : Screen() {
            @Composable override fun Render(onBack: () -> Unit) {
                when (windowClass) {
                    WindowClass.Compact -> CompactScaffold(NavTab.Characters)
                    WindowClass.Tablet  -> TabletScaffold(NavTab.Characters)
                    WindowClass.Wide    -> WideScaffold(NavTab.Characters)
                }
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
}
