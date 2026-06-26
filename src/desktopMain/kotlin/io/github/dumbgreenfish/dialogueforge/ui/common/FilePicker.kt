package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FilenameFilter
import javax.swing.SwingUtilities

private const val FILE_DIALOG_TITLE = "Choose character card"

@Composable
actual fun rememberFilePicker(onResult: (ByteArray, String) -> Unit): () -> Unit {
    val accept = CharacterFileType.entries.toSet()
    return remember(accept) {
        {
            SwingUtilities.invokeLater {
                val dialog = FileDialog(null as Frame?, FILE_DIALOG_TITLE, FileDialog.LOAD)
                dialog.filenameFilter = FilenameFilter { _, name ->
                    accept.any { name.endsWith(it.extension, ignoreCase = true) }
                }
                dialog.isMultipleMode = false
                dialog.isVisible = true
                val selectedName = dialog.file ?: return@invokeLater
                val selectedDir = dialog.directory ?: return@invokeLater
                val file = File(selectedDir, selectedName)
                onResult(file.readBytes(), file.name)
            }
        }
    }
}