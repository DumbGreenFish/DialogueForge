package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.CircleX
import com.composables.icons.lucide.Key
import com.composables.icons.lucide.Lucide
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_error_api_key_not_set
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_error_dismiss
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_error_network
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_error_retry
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_error_server
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_error_unknown
import io.github.dumbgreenfish.dialogueforge.ui.common.WindowClass
import io.github.dumbgreenfish.dialogueforge.ui.common.isMobilePlatform
import io.github.dumbgreenfish.dialogueforge.ui.common.windowClass
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatError
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.ChatErrorType
import org.jetbrains.compose.resources.stringResource

private val ErrorCardShape = RoundedCornerShape(16.dp)
private val ErrorCardPadding = 12.dp
private val ErrorIconSize = 18.dp
private val ErrorIconGap = 8.dp
private val ErrorDetailGap = 4.dp
private val ErrorButtonRowGap = 8.dp
private val ErrorRetryButtonPaddingH = 12.dp
private val ErrorRetryButtonPaddingV = 6.dp

private val ErrorStartIndent = 71.dp
private val CompactStartIndent = 12.dp

@Composable
internal fun ChatErrorItem(
    error: ChatError,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    val detailMaxLines = if (isMobilePlatform) 2 else 5
    val startIndent = if (windowClass == WindowClass.Compact) CompactStartIndent else ErrorStartIndent

    val style = errorStyle(error.type, cs)
    val message = when (error.type) {
        ChatErrorType.NoApiKey -> stringResource(Res.string.dialogue_error_api_key_not_set)
        ChatErrorType.Network -> stringResource(Res.string.dialogue_error_network)
        ChatErrorType.Server -> stringResource(Res.string.dialogue_error_server)
        ChatErrorType.Unknown -> stringResource(Res.string.dialogue_error_unknown)
    }

    Surface(
        modifier = modifier.padding(start = startIndent, end = 16.dp, top = 8.dp, bottom = 8.dp),
        shape = ErrorCardShape,
        color = style.containerColor,
        border = style.border,
    ) {
        Column(modifier = Modifier.padding(ErrorCardPadding)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = style.icon,
                    contentDescription = null,
                    tint = style.iconTint,
                    modifier = Modifier.size(ErrorIconSize),
                )
                Spacer(Modifier.width(ErrorIconGap))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = cs.onSurface,
                )
            }

            if (error.details.isNotBlank()) {
                Spacer(Modifier.height(ErrorDetailGap))
                Text(
                    text = error.details,
                    style = MaterialTheme.typography.bodySmall,
                    color = cs.onSurfaceVariant,
                    maxLines = detailMaxLines,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.height(ErrorButtonRowGap))
            Row(horizontalArrangement = Arrangement.spacedBy(ErrorButtonRowGap)) {
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = style.buttonColor,
                        contentColor = style.buttonContentColor,
                    ),
                    contentPadding = PaddingValues(
                        horizontal = ErrorRetryButtonPaddingH,
                        vertical = ErrorRetryButtonPaddingV,
                    ),
                ) {
                    Text(text = stringResource(Res.string.dialogue_error_retry))
                }
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(Res.string.dialogue_error_dismiss),
                        color = cs.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private data class ErrorStyle(
    val containerColor: Color,
    val border: BorderStroke?,
    val icon: ImageVector,
    val iconTint: Color,
    val buttonColor: Color,
    val buttonContentColor: Color,
)

@Composable
private fun errorStyle(type: ChatErrorType, cs: androidx.compose.material3.ColorScheme): ErrorStyle = when (type) {
    ChatErrorType.NoApiKey -> ErrorStyle(
        containerColor = Color.Transparent,
        border = BorderStroke(1.dp, ForgeColors.spark.copy(alpha = 0.5f)),
        icon = Lucide.Key,
        iconTint = ForgeColors.spark,
        buttonColor = ForgeColors.spark.copy(alpha = 0.12f),
        buttonContentColor = ForgeColors.spark,
    )
    ChatErrorType.Network,
    ChatErrorType.Server -> ErrorStyle(
        containerColor = cs.error.copy(alpha = 0.04f),
        border = BorderStroke(1.5.dp, cs.error.copy(alpha = 0.4f)),
        icon = Lucide.CircleAlert,
        iconTint = cs.error,
        buttonColor = cs.error.copy(alpha = 0.12f),
        buttonContentColor = cs.error,
    )
    ChatErrorType.Unknown -> ErrorStyle(
        containerColor = cs.errorContainer,
        border = null,
        icon = Lucide.CircleX,
        iconTint = cs.error,
        buttonColor = cs.error,
        buttonContentColor = cs.onError,
    )
}

