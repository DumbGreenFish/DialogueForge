package io.github.dumbgreenfish.dialogueforge.ui.navigation

import io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs.CharactersTab
import io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs.NavTabs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class NotificationNavigationTest {
    @Test
    fun notification_navigation_replaces_existing_character_history_with_list_and_target_chat() {
        val controller = NavController()
        val characters = NavTabs.Characters.tabObject as CharactersTab
        characters.navigateTo(CharactersTab.Screen.ChatScreen("old-character"))
        characters.navigateTo(CharactersTab.Screen.ChatScreen("another-character"))
        controller.switchTab(NavTabs.Settings)

        controller.openChatFromNotification("target-character")

        assertEquals(NavTabs.Characters, controller.activeTab.value)
        assertEquals(2, characters.stack.size)
        assertIs<CharactersTab.Screen.MainScreen>(characters.stack.first())
        assertEquals(
            "target-character",
            (characters.stack.last() as CharactersTab.Screen.ChatScreen).characterId,
        )
        assertEquals(emptyList(), characters.forwardStack)
    }

    @Test
    fun back_after_notification_navigation_always_returns_to_character_list() {
        val controller = NavController()
        val characters = NavTabs.Characters.tabObject as CharactersTab

        controller.openChatFromNotification("target-character")
        characters.popBack()

        assertEquals(1, characters.stack.size)
        assertIs<CharactersTab.Screen.MainScreen>(characters.stack.single())
    }

    @Test
    fun repeated_notification_navigation_does_not_duplicate_the_target_chat() {
        val controller = NavController()
        val characters = NavTabs.Characters.tabObject as CharactersTab

        controller.openChatFromNotification("target-character")
        controller.openChatFromNotification("target-character")

        assertEquals(2, characters.stack.size)
        assertEquals(
            "target-character",
            (characters.stack.last() as CharactersTab.Screen.ChatScreen).characterId,
        )
    }
}
