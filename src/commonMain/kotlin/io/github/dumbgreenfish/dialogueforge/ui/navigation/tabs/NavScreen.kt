package io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs

import androidx.compose.runtime.Composable

sealed class NavScreen {
    @Composable
    abstract fun Render(onBack: () -> Unit)
}