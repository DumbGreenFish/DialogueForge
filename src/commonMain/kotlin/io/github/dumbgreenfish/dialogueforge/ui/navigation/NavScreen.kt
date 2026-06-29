package io.github.dumbgreenfish.dialogueforge.ui.navigation

import androidx.compose.runtime.Composable

sealed class NavScreen {
    @Composable abstract fun Render(onBack: () -> Unit)
}
