package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
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
import kotlinx.coroutines.delay

private val GreetingAvatarSize = 64.dp
private val RegularAvatarSize = 48.dp
private val GreetingNameSizeSp = 20f
private val RegularNameSizeSp = 16f
private val HeaderGap = 4.dp

private val GreetingVerticalGap = 16.dp
private val MessageVerticalGap = 16.dp

private val SelectionTintAlpha = 0.08f
private val SelectionRowPaddingH = 12.dp
private val SelectionRowPaddingTop = 16.dp

private val MessageActionsPaddingTop = 12.dp
private val MessageContentPaddingTopCompact = 4.dp

@Composable
internal fun AssistantMessage(
    message: Message,
    interactionState: MessageInteractionState,
    character: Character,
    messageWidth: MessageWidth,
    isGreeting: Boolean,
    onActionRowEvent: (ActionRowEvent) -> Unit,
    onEditFieldEvent: (EditFieldEvent) -> Unit,
    onMessageItemEvent: (MessageItemEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val avatarSize by animateDpAsState(
        targetValue = if (isGreeting) GreetingAvatarSize else RegularAvatarSize,
        animationSpec = tween(ForgeAnimation.DurationStateTransition),
        label = "avatarSize",
    )
    var requestedAvatarSize by remember { mutableStateOf(GreetingAvatarSize) }

    LaunchedEffect(isGreeting) {
        val target = if (isGreeting) GreetingAvatarSize else RegularAvatarSize
        if (requestedAvatarSize != target) {
            delay(ForgeAnimation.DurationStateTransition.toLong())
            requestedAvatarSize = target
        }
    }
    val nameSizeSp by animateFloatAsState(
        targetValue = if (isGreeting) GreetingNameSizeSp else RegularNameSizeSp,
        animationSpec = tween(ForgeAnimation.DurationStateTransition),
        label = "nameSizeSp",
    )
    val verticalGap by animateDpAsState(
        targetValue = if (isGreeting) GreetingVerticalGap else MessageVerticalGap,
        animationSpec = tween(ForgeAnimation.DurationStateTransition),
        label = "verticalGap",
    )

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
                    start = if (isCompact) avatarVisualInsetFor(avatarSize) else 0.dp,
                    bottom = verticalGap,
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
                    else -> AssistantContent(
                        name = character.name,
                        imageProvider = imageProvider,
                        text = message.text,
                        avatarSize = avatarSize,
                        requestedAvatarSize = requestedAvatarSize,
                        nameSizeSp = nameSizeSp,
                    )
                }
            }

            if (!isSelectionMode) {
                val isActionsExpanded = (interactionState as? MessageInteractionState.Browsing)?.isActionsExpanded ?: false
                ActionRow(
                    role = MessageRole.Assistant,
                    visible = isCompact || isActionsExpanded,
                    onActionRowEvent = onActionRowEvent,
                    startPadding = if (!isCompact) assistantActionIndent(avatarSize) else 0.dp,
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
private fun AssistantContent(
    name: String,
    imageProvider: ImageProvider,
    text: String,
    avatarSize: Dp,
    requestedAvatarSize: Dp,
    nameSizeSp: Float,
) {
    if (windowClass == WindowClass.Compact) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AssistantHeader(
                name = name,
                imageProvider = imageProvider,
                avatarSize = avatarSize,
                nameSize = nameSizeSp.sp,
                modifier = Modifier.padding(bottom = HeaderGap),
                targetSizeDp = requestedAvatarSize,
            )
            Spacer(Modifier.height(MessageContentPaddingTopCompact))
            MarkdownText(
                text = text,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = AssistantTextSize,
                lineHeight = AssistantLineHeight,
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AssistantHeaderGap),
            verticalAlignment = Alignment.Top,
        ) {
            AssistantAvatar(imageProvider = imageProvider, avatarSize = avatarSize, targetSizeDp = requestedAvatarSize)
            Column(modifier = Modifier.weight(1f)) {
                AssistantName(
                    name = name,
                    nameSize = nameSizeSp.sp,
                    modifier = Modifier.padding(bottom = HeaderGap),
                )
                MarkdownText(
                    text = text,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = AssistantTextSize,
                    lineHeight = AssistantLineHeight,
                )
            }
        }
    }
}

internal fun assistantActionIndent(avatarSize: Dp): Dp =
    avatarSize + AssistantHeaderGap + AssistantTextAlignmentCorrection
