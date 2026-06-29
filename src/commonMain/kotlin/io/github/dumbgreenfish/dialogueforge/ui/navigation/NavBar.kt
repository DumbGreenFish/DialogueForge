package io.github.dumbgreenfish.dialogueforge.ui.navigation

import androidx.compose.runtime.mutableStateListOf
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab

abstract class NavBar<T : NavScreen>(mainScreen: T) {
    abstract val tabEnum: NavTab
    val stack = mutableStateListOf(mainScreen)

    fun popBack() { if (stack.size > 1) stack.removeLastOrNull() }
    fun navigateTo(screen: T) { stack.add(screen) }
}
