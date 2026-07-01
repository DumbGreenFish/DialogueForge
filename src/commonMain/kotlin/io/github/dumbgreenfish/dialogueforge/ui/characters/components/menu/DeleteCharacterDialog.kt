package io.github.dumbgreenfish.dialogueforge.ui.characters.components.menu

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.character_delete_cancel
import io.github.dumbgreenfish.dialogueforge.generated.resources.character_delete_confirm
import io.github.dumbgreenfish.dialogueforge.generated.resources.character_delete_message
import io.github.dumbgreenfish.dialogueforge.generated.resources.character_delete_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DeleteCharacterDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.character_delete_title)) },
        text = { Text(stringResource(Res.string.character_delete_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(Res.string.character_delete_confirm), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.character_delete_cancel))
            }
        },
    )
}