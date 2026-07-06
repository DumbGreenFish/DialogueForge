package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.ForgeSettings
import io.github.dumbgreenfish.dialogueforge.design.ForgeAnimation
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.ui.common.WindowClass
import io.github.dumbgreenfish.dialogueforge.ui.common.windowClass
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject

private val BubblePaddingT = 8.dp
private val BubblePaddingB = 8.dp
private val BubblePaddingH = 12.dp
private val TimestampUserAlpha = 0.55f
private val CheckmarkSize = 12.dp
private val SystemChipPaddingV = 4.dp
private val SystemChipPaddingH = 12.dp
private val SystemInfoIconSize = 12.dp
private val RowPaddingH = 2.dp
private val RowPaddingV = 2.dp
private val TimestampTop = 2.dp
private val SystemChipMargin = 8.dp
private val CheckIndicatorSize = 24.dp
private val CheckIndicatorStroke = 2.dp
private val CheckIconSize = 14.dp
private const val SelectionTintAlpha = 0.12f
private val IndicatorOuterGap = 8.dp
private val IndicatorLeftPadding = 8.dp

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
    isSelected: Boolean = false,
    inSelectionMode: Boolean = false,
    onToggleSelection: ((String) -> Unit)? = null,
    onEnterSelectionMode: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    if (message.role == MessageRole.System) {
        SystemMessageChip(message.text, modifier)
        return
    }

    val cs = MaterialTheme.colorScheme
    val compact = windowClass != WindowClass.Wide
    val isUser = message.role == MessageRole.User
    val duration = ForgeAnimation.DurationStateTransition

    val forgeSettings = koinInject<ForgeSettings>()
    val messageWidth by forgeSettings.messageWidth.collectAsState()

    val bg = if (isUser) ForgeColors.copperDim else cs.surfaceVariant
    val fg = if (isUser) cs.onPrimaryContainer else cs.onSurface
    val shape = if (isUser) ForgeShape.bubbleUser else ForgeShape.bubbleAssistant
    val boxAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart

    val timeAlpha = if (isUser) TimestampUserAlpha else 1f
    val timestampColor = if (isUser) cs.onPrimaryContainer.copy(alpha = timeAlpha)
    else ForgeColors.onSurfaceFaint

    val tintAlpha by animateFloatAsState(
        targetValue = if (isSelected) SelectionTintAlpha else 0f,
        animationSpec = tween(duration, easing = FastOutSlowInEasing),
        label = "tint",
    )

    val clickModifier = when {
        inSelectionMode -> Modifier.combinedClickable(
            onClick = { onToggleSelection?.invoke(message.id) },
            onLongClick = null,
        )
        else -> Modifier.combinedClickable(
            onClick = {},
            onLongClick = { onEnterSelectionMode?.invoke(message.id) },
            onDoubleClick = { onEnterSelectionMode?.invoke(message.id) },
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = RowPaddingH, vertical = RowPaddingV),
        contentAlignment = boxAlignment,
    ) {
        val bubbleWidth = if (compact) {
            maxWidth * messageWidth.compactFraction
        } else {
            minOf(maxWidth * messageWidth.desktopFraction, messageWidth.desktopMaxWidthDp.dp)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (tintAlpha > 0f) Modifier.drawWithContent {
                        drawContent()
                        val extra = 2.dp.toPx()
                        drawRect(
                            topLeft = Offset(0f, -extra),
                            size = Size(size.width, size.height + extra * 2),
                            color = cs.onSurface.copy(alpha = tintAlpha),
                        )
                    } else Modifier
                )
                .then(clickModifier),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedVisibility(
                visible = inSelectionMode,
                enter = expandHorizontally(tween(duration, easing = FastOutSlowInEasing)) +
                    fadeIn(tween(duration, easing = FastOutSlowInEasing)),
                exit = shrinkHorizontally(tween(duration, easing = FastOutSlowInEasing)) +
                    fadeOut(tween(duration, easing = FastOutSlowInEasing)),
            ) {
                Row {
                    Spacer(Modifier.size(IndicatorLeftPadding))
                    CheckIndicator(isSelected = isSelected)
                    Spacer(Modifier.size(IndicatorOuterGap))
                }
            }

            if (isUser) {
                Spacer(Modifier.weight(1f))
            }

            Box(
                modifier = Modifier
                    .width(bubbleWidth)
                    .clip(shape)
                    .background(bg),
            ) {
                Column(
                    modifier = Modifier.padding(
                        top = BubblePaddingT,
                        bottom = BubblePaddingB,
                        start = BubblePaddingH,
                        end = BubblePaddingH,
                    ),
                ) {
                    if (isSelected) {
                        SelectionContainer {
                            Text(
                                text = message.text,
                                color = fg,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    } else {
                        Text(
                            text = message.text,
                            color = fg,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
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
                            style = MaterialTheme.typography.labelSmall,
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

            if (!isUser) {
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CheckIndicator(isSelected: Boolean) {
    val cs = MaterialTheme.colorScheme
    val duration = ForgeAnimation.DurationStateTransition

    val fillAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(duration, easing = FastOutSlowInEasing),
        label = "checkFill",
    )
    val checkScale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(
            durationMillis = duration / 2,
            delayMillis = if (isSelected) duration / 3 else 0,
            easing = FastOutSlowInEasing,
        ),
        label = "checkScale",
    )

    Box(
        modifier = Modifier
            .size(CheckIndicatorSize)
            .background(
                color = ForgeColors.spark.copy(alpha = fillAlpha),
                shape = CircleShape,
            )
            .border(
                width = CheckIndicatorStroke,
                color = ForgeColors.spark.copy(alpha = if (isSelected) 0f else 1f),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            Icon(
                Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier
                    .size(CheckIconSize)
                    .graphicsLayer(
                        scaleX = checkScale,
                        scaleY = checkScale,
                    ),
                tint = cs.onTertiary,
            )
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
            shape = ForgeShape.pill,
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
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
