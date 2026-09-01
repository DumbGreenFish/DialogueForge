package io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs

import androidx.compose.runtime.mutableStateListOf

abstract class NavTab<T : NavScreen>(mainScreen: T) {
    val stack = mutableStateListOf(mainScreen)
    val forwardStack = mutableStateListOf<T>()

    fun popBack() {
        if (stack.size > 1) {
            forwardStack.add(stack.removeAt(stack.lastIndex))
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