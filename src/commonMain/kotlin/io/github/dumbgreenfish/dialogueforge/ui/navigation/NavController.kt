package io.github.dumbgreenfish.dialogueforge.ui.navigation

import io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs.CharactersTab
import io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs.NavTabs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

@Single
class NavController {

    private val _activeTab = MutableStateFlow(NavTabs.Characters)
    val activeTab: StateFlow<NavTabs> = _activeTab.asStateFlow()

    fun switchTab(tab: NavTabs) { _activeTab.value = tab }

    fun openChatFromNotification(characterId: String) {
        val characters = NavTabs.Characters.tabObject as CharactersTab
        characters.stack.clear()
        characters.stack.add(CharactersTab.Screen.MainScreen)
        characters.forwardStack.clear()
        characters.navigateTo(CharactersTab.Screen.ChatScreen(characterId))
        _activeTab.value = NavTabs.Characters
    }
}
