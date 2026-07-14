package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_error_dismiss
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_error_retry
import org.jetbrains.compose.resources.stringResource

private val SnackbarPadding = 16.dp

@Composable
internal fun ChatSnackbar(
    message: String,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Snackbar(
        modifier = modifier
            .fillMaxWidth()
            .padding(SnackbarPadding),
        action = {
            if (onRetry != null) {
                TextButton(
                    onClick = onRetry,
                    colors = ButtonDefaults.textButtonColors(contentColor = ForgeColors.spark),
                ) {
                    Text(stringResource(Res.string.dialogue_error_retry))
                }
            }
        },
        dismissAction = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
            ) {
                Text(stringResource(Res.string.dialogue_error_dismiss))
            }
        },
    ) {
        Text(text = message)
    }
}
