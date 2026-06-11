package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private const val FALLBACK_FILENAME = "character"

@Composable
actual fun rememberFilePicker(accept: List<String>, onResult: (ByteArray, String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: return@rememberLauncherForActivityResult
        val filename = uri.lastPathSegment?.substringAfterLast('/') ?: FALLBACK_FILENAME
        onResult(bytes, filename)
    }
    return remember(accept) { { launcher.launch(accept.toTypedArray()) } }
}
