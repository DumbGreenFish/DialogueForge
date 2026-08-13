package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.setup

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.background_setup_accept
import io.github.dumbgreenfish.dialogueforge.generated.resources.background_setup_decline
import io.github.dumbgreenfish.dialogueforge.generated.resources.background_setup_text
import io.github.dumbgreenfish.dialogueforge.generated.resources.background_setup_title
import io.github.dumbgreenfish.dialogueforge.ui.common.ForgeAlertDialog
import org.jetbrains.compose.resources.stringResource

@Composable
fun BackgroundSetupIntroductionDialog(
    onAccept: () -> Unit,
    onDecline: () -> Unit,
) {
    ForgeAlertDialog(
        onDismissRequest = onDecline,
        title = { Text(stringResource(Res.string.background_setup_title)) },
        text = { Text(stringResource(Res.string.background_setup_text)) },
        confirmButton = {
            TextButton(onClick = onAccept) {
                Text(stringResource(Res.string.background_setup_accept))
            }
        },
        dismissButton = {
            TextButton(onClick = onDecline) {
                Text(stringResource(Res.string.background_setup_decline))
            }
        },
    )
}
