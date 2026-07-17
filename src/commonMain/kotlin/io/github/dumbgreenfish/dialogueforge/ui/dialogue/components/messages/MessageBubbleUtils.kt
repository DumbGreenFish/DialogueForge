package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.ui.common.isMobilePlatform
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.MessageWidth

private val RegularAvatarSize = 48.dp
internal val AvatarVisualInset = 6.dp
internal val AssistantTextAlignmentCorrection = 1.dp
internal val AssistantTextSize = 14.5.sp
internal val AssistantLineHeight = 25.35.sp
internal val MessageGap = 16.dp

@Composable
internal fun bubbleWidthModifier(messageWidth: MessageWidth): Modifier {
    val isCompact = isMobilePlatform
    val fraction = if (isCompact) messageWidth.compactFraction else messageWidth.desktopFraction
    val maxWidthModifier = if (isCompact) Modifier else Modifier.widthIn(max = messageWidth.desktopMaxWidthDp.dp)
    return maxWidthModifier.fillMaxWidth(fraction)
}

internal fun avatarVisualInsetFor(avatarSize: Dp): Dp =
    AvatarVisualInset * (avatarSize / RegularAvatarSize)

@Composable
internal fun userBubbleModifier(messageWidth: MessageWidth): Modifier {
    val isCompact = isMobilePlatform
    val maxConstraint = if (isCompact) Modifier else Modifier.widthIn(max = messageWidth.desktopMaxWidthDp.dp)
    return maxConstraint.wrapContentWidth()
}

@Composable
internal fun rememberTapModifier(
    isSelectionMode: Boolean,
    onToggleSelection: () -> Unit,
    onToggleActions: () -> Unit,
): Modifier {
    return when {
        isSelectionMode -> Modifier.clickable(
            indication = null,
            interactionSource = null,
            onClick = onToggleSelection,
        )
        isMobilePlatform -> Modifier.clickable(
            indication = null,
            interactionSource = null,
            onClick = onToggleActions,
        )
        else -> Modifier
    }
}

@Composable
internal fun rememberHoverModifier(
    isSelectionMode: Boolean,
    interactionSource: MutableInteractionSource,
): Modifier {
    if (isMobilePlatform) return Modifier
    return if (isSelectionMode) {
        Modifier.pointerHoverIcon(PointerIcon.Hand)
    } else {
        Modifier.hoverable(interactionSource)
    }
}
