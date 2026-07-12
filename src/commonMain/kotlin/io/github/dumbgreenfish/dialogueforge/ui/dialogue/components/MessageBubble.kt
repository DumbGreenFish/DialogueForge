package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeAnimation
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.ui.common.WindowClass
import io.github.dumbgreenfish.dialogueforge.ui.common.formatMessageTime
import io.github.dumbgreenfish.dialogueforge.ui.common.isMobilePlatform
import io.github.dumbgreenfish.dialogueforge.ui.common.windowClass
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.bubble.CheckIndicator
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.bubble.MessageBubbleContent
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.bubble.MessageBubbleFooter
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.bubble.SystemMessageChip
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.bubble.model.MessageBubbleActions
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.bubble.model.MessageBubbleUiState
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.MessageWidth

private val BubblePaddingT = 8.dp
private val BubblePaddingB = 8.dp
private val BubblePaddingH = 12.dp
private val TimestampUserAlpha = 0.55f
private val RowPaddingH = 2.dp
private val RowPaddingV = 2.dp
private const val SelectionTintAlpha = 0.12f
private val IndicatorOuterGap = 8.dp
private val IndicatorLeftPadding = 8.dp
private val SelectionTintOverflow = 2.dp

@Composable
@OptIn(ExperimentalFoundationApi::class)
internal fun MessageBubble(
    message: Message,
    uiState: MessageBubbleUiState,
    actions: MessageBubbleActions,
    messageWidth: MessageWidth,
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

    val bg = if (isUser) ForgeColors.copperDim else cs.surfaceVariant
    val fg = if (isUser) cs.onPrimaryContainer else cs.onSurface
    val shape = if (isUser) ForgeShape.bubbleUser else ForgeShape.bubbleAssistant
    val boxAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart

    val timeAlpha = if (isUser) TimestampUserAlpha else 1f
    val timestampColor = if (isUser) cs.onPrimaryContainer.copy(alpha = timeAlpha)
    else ForgeColors.onSurfaceFaint

    val tintAlpha by animateFloatAsState(
        targetValue = if (uiState.isSelected) SelectionTintAlpha else 0f,
        animationSpec = tween(duration, easing = FastOutSlowInEasing),
        label = "tint",
    )

    val hoverInteraction = remember { MutableInteractionSource() }
    val isHovered by hoverInteraction.collectIsHoveredAsState()

    val clickModifier = when {
        uiState.inSelectionMode -> Modifier
            .combinedClickable(
                onClick = { actions.onToggleSelection(message.id) },
                onLongClick = null,
            )
        isMobilePlatform -> Modifier
            .pointerInput(message.id) {
                detectTapGestures(
                    onTap = { actions.onShowActionRow(message.id) },
                    onLongPress = if (uiState.isGenerating) null else { { actions.onEnterSelectionMode(message.id) } },
                )
            }
        else -> Modifier
            .combinedClickable(
                onClick = { actions.onShowActionRow(message.id) },
                onDoubleClick = if (uiState.isGenerating) null else { { actions.onEnterSelectionMode(message.id) } },
            )
    }

    val actionRowVisible = (isHovered || uiState.showActionRow) && !uiState.inSelectionMode && !uiState.isGenerating

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

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (tintAlpha > 0f) Modifier.drawWithContent {
                            drawContent()
                            val extra = SelectionTintOverflow.toPx()
                            drawRect(
                                topLeft = Offset(0f, -extra),
                                size = Size(size.width, size.height + extra * 2),
                                color = cs.onSurface.copy(alpha = tintAlpha),
                            )
                        } else Modifier
                    )
                    .hoverable(hoverInteraction)
                    .then(clickModifier),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AnimatedVisibility(
                    visible = uiState.inSelectionMode,
                    enter = expandHorizontally(tween(duration, easing = FastOutSlowInEasing)) +
                        fadeIn(tween(duration, easing = FastOutSlowInEasing)),
                    exit = shrinkHorizontally(tween(duration, easing = FastOutSlowInEasing)) +
                        fadeOut(tween(duration, easing = FastOutSlowInEasing)),
                ) {
                    Row {
                        Spacer(Modifier.size(IndicatorLeftPadding))
                        CheckIndicator(isSelected = uiState.isSelected)
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
                        MessageBubbleContent(
                            text = message.text,
                            isEditing = uiState.isEditing,
                            isSelected = uiState.isSelected,
                            fg = fg,
                            bg = bg,
                            editTextValue = uiState.editText,
                            onEditTextChange = actions.onEditTextChange,
                        )
                        MessageBubbleFooter(
                            isUser = isUser,
                            isEditing = uiState.isEditing,
                            actionRowVisible = actionRowVisible,
                            messageId = message.id,
                            timestampText = formatMessageTime(message.timestamp),
                            timestampColor = timestampColor,
                            onSave = actions.onSave,
                            onCancel = actions.onCancel,
                            onCopy = actions.onCopy,
                            onEdit = actions.onEdit,
                            onDelete = actions.onDelete,
                        )
                    }
                }

                if (!isUser) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}
