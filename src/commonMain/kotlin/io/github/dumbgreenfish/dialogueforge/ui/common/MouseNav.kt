package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun Modifier.mouseNav(
    onBack: () -> Unit,
    onForward: () -> Unit,
): Modifier
