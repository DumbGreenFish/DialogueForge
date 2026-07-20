package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeAnimation
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.ui.common.WindowClass
import io.github.dumbgreenfish.dialogueforge.ui.common.windowClass
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.MessageWidth

private val UserTextSize = 14.sp
private val UserLineHeight = 23.8.sp

private val SelectionTintAlpha = 0.08f
private val SelectionRowPaddingH = 12.dp
private val SelectionRowPaddingTop = 4.dp

private val MessageActionsPaddingTop = 12.dp

@Composable
internal fun UserMessage(
    message: Message,
    interactionState: MessageInteractionState,
    messageWidth: MessageWidth,
    onActionRowEvent: (ActionRowEvent) -> Unit,
    onEditFieldEvent: (EditFieldEvent) -> Unit,
    onMessageItemEvent: (MessageItemEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isSelectionMode = interactionState is MessageInteractionState.Selecting
    val isSelected = (interactionState as? MessageInteractionState.Selecting)?.isSelected ?: false
    val isCompact = windowClass == WindowClass.Compact

    val tintAlpha by animateFloatAsState(
        targetValue = if (isSelected) SelectionTintAlpha else 0f,
        animationSpec = tween(ForgeAnimation.DurationStateTransition),
        label = "selectionTint",
    )
    val backgroundColor = ForgeColors.spark.copy(alpha = tintAlpha)

    val tapModifier = rememberTapModifier(
        isSelectionMode = isSelectionMode,
        onToggleSelection = { onMessageItemEvent(MessageItemEvent.ToggleSelection) },
        onToggleActions = { onMessageItemEvent(MessageItemEvent.ToggleActions) },
    )
    val hoverModifier = rememberHoverModifier(isSelectionMode, interactionSource)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(
                start = SelectionRowPaddingH,
                end = SelectionRowPaddingH,
                top = SelectionRowPaddingTop,
            ),
        verticalAlignment = Alignment.Top,
    ) {
        if (!isCompact) {
            SelectionCheckboxSlot(
                isSelectionMode = isSelectionMode,
                isSelected = isSelected,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .then(tapModifier)
                .then(hoverModifier)
                .padding(
                    end = if (isCompact) AvatarVisualInset else 0.dp,
                    bottom = MessageGap,
                ),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd,
            ) {
                when (interactionState) {
                    is MessageInteractionState.Editing -> EditField(
                        text = interactionState.text,
                        onEditFieldEvent = onEditFieldEvent,
                        role = MessageRole.User,
                        messageWidth = messageWidth,
                    )
                    else -> UserMessageContent(
                        text = message.text,
                        messageWidth = messageWidth,
                        isCompact = isCompact,
                    )
                }
            }

            if (!isSelectionMode) {
                val isActionsExpanded = (interactionState as? MessageInteractionState.Browsing)?.isActionsExpanded ?: false
                ActionRow(
                    role = MessageRole.User,
                    visible = isCompact || isActionsExpanded,
                    onActionRowEvent = onActionRowEvent,
                    interactionSource = interactionSource,
                    modifier = Modifier.padding(top = MessageActionsPaddingTop),
                )
            }
        }
    }
}

@Composable
private fun UserMessageContent(
    text: String,
    messageWidth: MessageWidth,
    isCompact: Boolean,
) {
    val cs = MaterialTheme.colorScheme
    val textMeasurer = rememberTextMeasurer()
    val style = MaterialTheme.typography.bodyLarge.copy(
        fontSize = UserTextSize,
        lineHeight = UserLineHeight,
    )

    BoxWithConstraints(modifier = userBubbleModifier(isCompact, messageWidth)) {
        val lineCount = textMeasurer.measure(
            text = AnnotatedString(text),
            style = style,
            maxLines = Int.MAX_VALUE,
            constraints = Constraints(maxWidth = constraints.maxWidth),
        ).lineCount

        val isSingleLine = lineCount <= 1

        if (isSingleLine) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd,
            ) {
                MarkdownText(
                    text = text,
                    color = cs.secondary,
                    fontSize = UserTextSize,
                    lineHeight = UserLineHeight,
                )
            }
        } else {
            MarkdownText(
                text = text,
                modifier = Modifier.wrapContentWidth(),
                color = cs.secondary,
                fontSize = UserTextSize,
                lineHeight = UserLineHeight,
            )
        }
    }
}
