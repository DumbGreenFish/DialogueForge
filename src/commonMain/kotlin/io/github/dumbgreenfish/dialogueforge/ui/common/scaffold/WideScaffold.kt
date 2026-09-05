package io.github.dumbgreenfish.dialogueforge.ui.common.scaffold

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

@Composable
fun WideScaffold(content: @Composable (PaddingValues) -> Unit) {
    Scaffold(content = content)
}
