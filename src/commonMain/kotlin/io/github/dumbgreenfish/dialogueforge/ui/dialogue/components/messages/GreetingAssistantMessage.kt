package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeAnimation
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.common.ImageProvider
import io.github.dumbgreenfish.dialogueforge.ui.common.WindowClass
import io.github.dumbgreenfish.dialogueforge.ui.common.rememberImageProvider
import io.github.dumbgreenfish.dialogueforge.ui.common.windowClass
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.MessageWidth

private val GreetingAvatarSize = 64.dp
private val AssistantNameGreetingSize = 16.sp
private val GreetingHeaderGap = 4.dp

private val GreetingVerticalGap = 40.dp

private val SelectionTintAlpha = 0.08f
private val SelectionRowPaddingH = 12.dp
private val SelectionRowPaddingTop = 8.dp

private val MessageActionsPaddingTop = 12.dp

@Composable
internal fun GreetingAssistantMessage(
    message: Message,
    interactionState: MessageInteractionState,
    character: Character,
    messageWidth: MessageWidth,
    onActionRowEvent: (ActionRowEvent) -> Unit,
    onEditFieldEvent: (EditFieldEvent) -> Unit,
    onMessageItemEvent: (MessageItemEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val imageProvider = rememberImageProvider(character.id)
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
            .height(IntrinsicSize.Min)
            .background(backgroundColor)
            .padding(
                start = SelectionRowPaddingH,
                end = SelectionRowPaddingH,
                top = SelectionRowPaddingTop,
            ),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .then(tapModifier)
                .then(hoverModifier)
                .padding(
                    start = if (isCompact) avatarVisualInsetFor(GreetingAvatarSize) else 0.dp,
                    bottom = GreetingVerticalGap,
                ),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopStart,
            ) {
                when (interactionState) {
                    is MessageInteractionState.Editing -> EditField(
                        text = interactionState.text,
                        onEditFieldEvent = onEditFieldEvent,
                        role = MessageRole.Assistant,
                        messageWidth = messageWidth,
                    )
                    else -> GreetingAssistantContent(
                        name = character.name,
                        imageProvider = imageProvider,
                        text = message.text,
                    )
                }
            }

            if (!isSelectionMode) {
                val isActionsExpanded = (interactionState as? MessageInteractionState.Browsing)?.isActionsExpanded ?: false
                ActionRow(
                    role = MessageRole.Assistant,
                    visible = isActionsExpanded,
                    onActionRowEvent = onActionRowEvent,
                    startPadding = if (!isCompact) assistantActionIndent(GreetingAvatarSize) else 0.dp,
                    interactionSource = interactionSource,
                    modifier = Modifier.padding(top = MessageActionsPaddingTop),
                )
            }
        }

        if (!isCompact) {
            SelectionCheckboxSlot(
                isSelectionMode = isSelectionMode,
                isSelected = isSelected,
            )
        }
    }
}

@Composable
private fun GreetingAssistantContent(
    name: String,
    imageProvider: ImageProvider,
    text: String,
) {
    if (windowClass == WindowClass.Compact) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AssistantHeader(
                name = name,
                imageProvider = imageProvider,
                avatarSize = GreetingAvatarSize,
                nameSize = AssistantNameGreetingSize,
                modifier = Modifier.padding(bottom = GreetingHeaderGap),
            )
            MarkdownText(
                text = text,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AssistantHeaderGap),
            verticalAlignment = Alignment.Top,
        ) {
            AssistantAvatar(imageProvider = imageProvider, avatarSize = GreetingAvatarSize)
            Column(modifier = Modifier.weight(1f)) {
                AssistantName(
                    name = name,
                    nameSize = AssistantNameGreetingSize,
                    modifier = Modifier.padding(bottom = GreetingHeaderGap),
                )
                MarkdownText(
                    text = text,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
