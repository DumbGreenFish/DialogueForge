package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.runtime.Composable

@Composable
expect fun rememberFilePicker(accept: List<String>, onResult: (ByteArray, String) -> Unit): () -> Unit
