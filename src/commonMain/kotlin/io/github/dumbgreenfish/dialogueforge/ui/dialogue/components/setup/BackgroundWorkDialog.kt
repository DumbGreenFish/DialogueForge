package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.setup

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.background_work_continue
import io.github.dumbgreenfish.dialogueforge.generated.resources.background_work_open_settings
import io.github.dumbgreenfish.dialogueforge.generated.resources.background_work_text
import io.github.dumbgreenfish.dialogueforge.generated.resources.background_work_title
import io.github.dumbgreenfish.dialogueforge.ui.common.ForgeAlertDialog
import org.jetbrains.compose.resources.stringResource

@Composable
fun BackgroundWorkDialog(
    onOpenSettings: () -> Unit,
    onContinue: () -> Unit,
) {
    ForgeAlertDialog(
        onDismissRequest = onContinue,
        title = { Text(stringResource(Res.string.background_work_title)) },
        text = { Text(stringResource(Res.string.background_work_text)) },
        confirmButton = {
            TextButton(onClick = onOpenSettings) {
                Text(stringResource(Res.string.background_work_open_settings))
            }
        },
        dismissButton = {
            TextButton(onClick = onContinue) {
                Text(stringResource(Res.string.background_work_continue))
            }
        },
    )
}
