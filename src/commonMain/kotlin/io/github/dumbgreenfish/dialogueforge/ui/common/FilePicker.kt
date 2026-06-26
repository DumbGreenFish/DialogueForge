package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.runtime.Composable

@Composable
expect fun rememberFilePicker(onResult: (ByteArray, String) -> Unit): () -> Unit
