package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.ui.common.isCompact
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private val BubblePaddingT = 9.dp
private val BubblePaddingB = 6.dp
private val BubblePaddingH = 13.dp
private val BubbleFontSize = 14.sp
private val BubbleLineHeight = 20.sp
private val BubbleWidthCompact = 0.84f
private val BubbleWidthDesktop = 0.76f
private val BubbleMaxWidthDesktop = 588.dp
private val TimestampFontSize = 10.sp
private val TimestampUserAlpha = 0.55f
private val CheckmarkSize = 12.dp
private val SystemChipPaddingV = 4.dp
private val SystemChipPaddingH = 10.dp
private val SystemChipFontSize = 11.sp
private val SystemInfoIconSize = 12.dp
private val RowPaddingH = 2.dp
private val RowPaddingV = 2.dp
private val TimestampTop = 2.dp
private val SystemChipMargin = 8.dp
private val PillRadius = 100.dp

private fun formatTime(ms: Long): String {
    val dt = Instant.fromEpochMilliseconds(ms).toLocalDateTime(TimeZone.currentSystemDefault())
    val h = dt.hour
    val m = dt.minute
    return "${h}:${m.toString().padStart(2, '0')}"
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
internal fun MessageBubble(
    message: Message,
    onLongPress: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    if (message.role == MessageRole.System) {
        SystemMessageChip(message.text, modifier)
        return
    }

    val cs = MaterialTheme.colorScheme
    val compact = isCompact
    val isUser = message.role == MessageRole.User

    val bubbleWidth = if (compact) BubbleWidthCompact else BubbleWidthDesktop

    val bg = if (isUser) ForgeColors.copperDim else cs.surfaceVariant
    val fg = if (isUser) cs.onPrimaryContainer else cs.onSurface
    val shape = if (isUser) ForgeShape.bubbleUser else ForgeShape.bubbleAssistant
    val alignment = if (isUser) Alignment.End else Alignment.Start

    val timeAlpha = if (isUser) TimestampUserAlpha else 1f
    val timestampColor = if (isUser) cs.onPrimaryContainer.copy(alpha = timeAlpha)
    else ForgeColors.onSurfaceFaint

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = RowPaddingH, vertical = RowPaddingV),
        horizontalAlignment = alignment,
    ) {
        Surface(
            shape = shape,
            color = bg,
            modifier = Modifier
                .fillMaxWidth(fraction = bubbleWidth)
                .then(
                    if (!compact) Modifier.widthIn(max = BubbleMaxWidthDesktop)
                    else Modifier
                )
                .then(
                    if (onLongPress != null) Modifier.combinedClickable(
                        onClick = {},
                        onLongClick = onLongPress,
                    ) else Modifier
                ),
        ) {
            Column(
                modifier = Modifier.padding(
                    top = BubblePaddingT,
                    bottom = BubblePaddingB,
                    start = BubblePaddingH,
                    end = BubblePaddingH,
                ),
            ) {
                Text(
                    text = message.text,
                    color = fg,
                    fontSize = BubbleFontSize,
                    lineHeight = BubbleLineHeight,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = TimestampTop),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        text = formatTime(message.timestamp),
                        color = timestampColor,
                        fontSize = TimestampFontSize,
                    )
                    if (isUser) {
                        Spacer(Modifier.size(4.dp))
                        Icon(
                            Icons.Filled.DoneAll,
                            contentDescription = null,
                            modifier = Modifier.size(CheckmarkSize),
                            tint = timestampColor,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SystemMessageChip(text: String, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Surface(
            shape = RoundedCornerShape(PillRadius),
            color = cs.surfaceVariant,
            border = BorderStroke(1.dp, cs.outline),
            modifier = Modifier.padding(vertical = SystemChipMargin),
        ) {
            Row(
                modifier = Modifier.padding(
                    vertical = SystemChipPaddingV,
                    horizontal = SystemChipPaddingH,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = null,
                    modifier = Modifier.size(SystemInfoIconSize),
                    tint = ForgeColors.onSurfaceFaint,
                )
                Spacer(Modifier.size(6.dp))
                Text(
                    text = text,
                    color = ForgeColors.onSurfaceFaint,
                    fontSize = SystemChipFontSize,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
