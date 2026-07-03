package io.github.dumbgreenfish.dialogueforge.ui.navigation

import androidx.compose.runtime.mutableStateListOf
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab

abstract class NavBar<T : NavScreen>(mainScreen: T) {
    abstract val tabEnum: NavTab
    val stack = mutableStateListOf(mainScreen)
    val forwardStack = mutableStateListOf<T>()

    fun popBack() {
        if (stack.size > 1) {
            @Suppress("UNCHECKED_CAST")
            forwardStack.add(stack.removeLast() as T)
        }
    }

    fun popForward() {
        if (forwardStack.isNotEmpty()) stack.add(forwardStack.removeLast())
    }

    fun navigateTo(screen: T) {
        forwardStack.clear()
        stack.add(screen)
    }
}
