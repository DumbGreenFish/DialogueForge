package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_edit_cancel
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_edit_save
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.common.ImageProvider
import io.github.dumbgreenfish.dialogueforge.ui.common.WindowClass
import io.github.dumbgreenfish.dialogueforge.ui.common.isMobilePlatform
import io.github.dumbgreenfish.dialogueforge.ui.common.rememberImageProvider
import io.github.dumbgreenfish.dialogueforge.ui.common.windowClass
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.MessageWidth
import org.jetbrains.compose.resources.stringResource

sealed interface EditFieldEvent {
    data class TextChanged(val value: TextFieldValue) : EditFieldEvent
    data object Save : EditFieldEvent
    data object Cancel : EditFieldEvent
}

sealed interface MessageItemEvent {
    data object ToggleActions : MessageItemEvent
}

sealed interface MessageDisplayStyle {
    data object Regular : MessageDisplayStyle
    data object Greeting : MessageDisplayStyle
}

sealed interface MessageInteractionState {
    data class Browsing(val isActionsExpanded: Boolean = false) : MessageInteractionState
    data class Editing(val text: TextFieldValue) : MessageInteractionState
    data object Generating : MessageInteractionState
}

data class MessageItemState(
    val message: Message,
    val displayStyle: MessageDisplayStyle,
    val interactionState: MessageInteractionState,
    val character: Character,
    val messageWidth: MessageWidth,
)

data class MessageItemCallbacks(
    val onActionRowEvent: (ActionRowEvent) -> Unit,
    val onEditFieldEvent: (EditFieldEvent) -> Unit,
    val onMessageItemEvent: (MessageItemEvent) -> Unit,
)

// Tweakable layout constants for the chat message item.
// These control avatar size, name size, gaps, and action-row spacing.
private val UserTextSize = 14.sp
private val AssistantTextSize = 12.sp
private val UserLineHeight = 23.8.sp
private val AssistantLineHeight = 25.35.sp
private val AssistantNameSize = 16.sp
private val AssistantNameGreetingSize = 16.sp
private val GreetingAvatarSize = 64.dp
private val RegularAvatarSize = 48.dp
private val GreetingHeaderGap = 4.dp        // gap between assistant name and message text in greeting mode
private val RegularHeaderGap = 4.dp         // gap between assistant name and message text in normal mode
// AssistantHeaderGap lives in AssistantHeader.kt and controls the gap between avatar and name.

private val MessageVerticalGap = 32.dp
private val GreetingVerticalGap = 40.dp

private val EditFieldRadius = 16.dp
private val EditFieldPaddingH = 14.dp
private val EditFieldPaddingV = 10.dp
private val EditFieldMinHeight = 40.dp
private val EditFieldFontSize = 14.sp
private val EditButtonsGap = 8.dp

private val AssistantTextAlignmentCorrection = 1.dp

private val MessageActionsPaddingTop = 12.dp

// Horizontal inset that visually aligns message content with the circular part
// of the assistant avatar. The source image has invisible padding inside its
// clipping box, so text and action bar need the same visual offset.
private val AvatarVisualInset = 6.dp

private fun contentInsetFor(avatarSize: Dp): Dp =
    AvatarVisualInset * (avatarSize / RegularAvatarSize)

@Composable
internal fun MessageItem(
    state: MessageItemState,
    callbacks: MessageItemCallbacks,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val interaction = state.interactionState

    val isGreeting = state.displayStyle is MessageDisplayStyle.Greeting
    val isCompact = windowClass == WindowClass.Compact

    val tapModifier = rememberTapModifier(
        onToggleActions = { callbacks.onMessageItemEvent(MessageItemEvent.ToggleActions) },
    )
    val hoverModifier = rememberHoverModifier(interactionSource)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        val contentHorizontalInset = when {
            isCompact && state.message.role == MessageRole.Assistant -> {
                contentInsetFor(if (isGreeting) GreetingAvatarSize else RegularAvatarSize)
            }
            isCompact && state.message.role == MessageRole.User -> AvatarVisualInset
            else -> 0.dp
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = if (state.message.role == MessageRole.Assistant) contentHorizontalInset else 0.dp,
                    end = if (state.message.role == MessageRole.User) contentHorizontalInset else 0.dp,
                    bottom = if (isGreeting) GreetingVerticalGap else MessageVerticalGap,
                )
                .then(hoverModifier),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(tapModifier),
                contentAlignment = if (state.message.role == MessageRole.User) Alignment.TopEnd else Alignment.TopStart,
            ) {
                when (interaction) {
                    is MessageInteractionState.Editing -> {
                        EditField(
                            text = interaction.text,
                            onEditFieldEvent = callbacks.onEditFieldEvent,
                            role = state.message.role,
                            messageWidth = state.messageWidth,
                        )
                    }
                    is MessageInteractionState.Browsing,
                    is MessageInteractionState.Generating -> {
                        MessageContent(
                            message = state.message,
                            displayStyle = state.displayStyle,
                            character = state.character,
                            messageWidth = state.messageWidth,
                        )
                    }
                }
            }

            val isActionsExpanded = (interaction as? MessageInteractionState.Browsing)?.isActionsExpanded ?: false
            MessageActions(
                message = state.message,
                isGreeting = isGreeting,
                isActionsExpanded = isActionsExpanded,
                interactionSource = interactionSource,
                onActionRowEvent = callbacks.onActionRowEvent,
                modifier = Modifier.padding(top = MessageActionsPaddingTop)
            )
        }
    }
}

/**
 * Tap handling for normal-mode taps on mobile to toggle the action row.
 */
@Composable
private fun rememberTapModifier(
    onToggleActions: () -> Unit,
): Modifier {
    if (!isMobilePlatform) return Modifier
    return Modifier.pointerInput(Unit) {
        detectTapGestures {
            onToggleActions()
        }
    }
}

@Composable
private fun rememberHoverModifier(
    interactionSource: MutableInteractionSource,
): Modifier {
    return if (!isMobilePlatform) {
        Modifier.hoverable(interactionSource)
    } else {
        Modifier
    }
}

/**
 * Wraps the action row for a message, applying the correct start indent
 * based on avatar size on wide screens.
 */
@Composable
private fun MessageActions(
    message: Message,
    isGreeting: Boolean,
    isActionsExpanded: Boolean,
    interactionSource: MutableInteractionSource,
    onActionRowEvent: (ActionRowEvent) -> Unit,
    modifier: Modifier,
) {
    val assistantTextIndent = if (message.role == MessageRole.Assistant && windowClass != WindowClass.Compact) {
        (if (isGreeting) GreetingAvatarSize else RegularAvatarSize) + AssistantHeaderGap + AssistantTextAlignmentCorrection
    } else {
        0.dp
    }

    ActionRow(
        role = message.role,
        visible = isActionsExpanded,
        onActionRowEvent = onActionRowEvent,
        startPadding = assistantTextIndent,
        interactionSource = interactionSource,
        modifier = modifier
    )
}

@Composable
private fun MessageContent(
    message: Message,
    displayStyle: MessageDisplayStyle,
    character: Character,
    messageWidth: MessageWidth,
) {
    when (message.role) {
        MessageRole.User -> UserMessage(
            text = message.text,
            messageWidth = messageWidth,
        )
        MessageRole.Assistant -> AssistantMessage(
            text = message.text,
            name = character.name,
            imageProvider = rememberImageProvider(character.id),
            isGreeting = displayStyle is MessageDisplayStyle.Greeting,
        )
        MessageRole.System -> Unit
    }
}

/**
 * Shared bubble-width logic: was copy-pasted between UserMessage and EditField.
 * On mobile the bubble takes a fraction of the row width; on desktop it's
 * additionally capped by messageWidth.desktopMaxWidthDp.
 */
@Composable
private fun bubbleWidthModifier(messageWidth: MessageWidth): Modifier {
    val isCompact = isMobilePlatform
    val fraction = if (isCompact) messageWidth.compactFraction else messageWidth.desktopFraction
    val maxWidthModifier = if (isCompact) Modifier else Modifier.widthIn(max = messageWidth.desktopMaxWidthDp.dp)
    return maxWidthModifier.fillMaxWidth(fraction)
}

@Composable
private fun UserMessage(
    text: String,
    messageWidth: MessageWidth,
) {
    Text(
        text = text,
        modifier = Modifier.then(bubbleWidthModifier(messageWidth)),
        color = MaterialTheme.colorScheme.secondary,
        textAlign = TextAlign.End,
        style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = UserTextSize,
            lineHeight = UserLineHeight,
        ),
    )
}

@Composable
private fun AssistantMessage(
    text: String,
    name: String,
    imageProvider: ImageProvider,
    isGreeting: Boolean,
) {
    val avatarSize = if (isGreeting) GreetingAvatarSize else RegularAvatarSize
    val nameSize = if (isGreeting) AssistantNameGreetingSize else AssistantNameSize
    val headerGap = if (isGreeting) GreetingHeaderGap else RegularHeaderGap
    val isCompactLayout = windowClass == WindowClass.Compact

    if (isCompactLayout) {
        // Mobile: avatar + name share one row; text flows below at full width.
        Column(modifier = Modifier.fillMaxWidth()) {
            AssistantHeader(
                name = name,
                imageProvider = imageProvider,
                avatarSize = avatarSize,
                nameSize = nameSize,
                modifier = Modifier.padding(bottom = headerGap),
            )
            MarkdownText(
                text = text,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    } else {
        // Desktop / tablet: avatar sits in a left column; name and text sit in a right column.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AssistantHeaderGap),
            verticalAlignment = Alignment.Top,
        ) {
            AssistantAvatar(imageProvider = imageProvider, avatarSize = avatarSize)
            Column(modifier = Modifier.weight(1f)) {
                AssistantName(
                    name = name,
                    nameSize = nameSize,
                    modifier = Modifier.padding(bottom = headerGap),
                )
                MarkdownText(
                    text = text,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun EditField(
    text: TextFieldValue,
    onEditFieldEvent: (EditFieldEvent) -> Unit,
    role: MessageRole,
    messageWidth: MessageWidth,
) {
    val cs = MaterialTheme.colorScheme
    val fieldBgIdle = cs.onSurface.copy(alpha = 0.05f)
    val fieldBorderFocus = cs.onSurface.copy(alpha = 0.12f)

    Column(
        modifier = Modifier.then(bubbleWidthModifier(messageWidth)),
        horizontalAlignment = if (role == MessageRole.User) Alignment.End else Alignment.Start,
    ) {
        BasicTextField(
            value = text,
            onValueChange = { onEditFieldEvent(EditFieldEvent.TextChanged(it)) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = cs.onSurface,
                fontSize = EditFieldFontSize,
            ),
            cursorBrush = SolidColor(ForgeColors.spark),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            singleLine = false,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = EditFieldMinHeight)
                .clip(RoundedCornerShape(EditFieldRadius))
                .background(fieldBgIdle)
                .border(
                    width = 1.dp,
                    color = fieldBorderFocus,
                    shape = RoundedCornerShape(EditFieldRadius),
                )
                .padding(horizontal = EditFieldPaddingH, vertical = EditFieldPaddingV),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(EditButtonsGap),
        ) {
            TextButton(onClick = { onEditFieldEvent(EditFieldEvent.Cancel) }) {
                Text(stringResource(Res.string.dialogue_edit_cancel))
            }
            TextButton(onClick = { onEditFieldEvent(EditFieldEvent.Save) }) {
                Text(stringResource(Res.string.dialogue_edit_save))
            }
        }
    }
}