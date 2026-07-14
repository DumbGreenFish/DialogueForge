package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_edit_cancel
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_edit_save
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.common.isMobilePlatform
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.MessageWidth
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

sealed interface EditFieldEvent {
    data class TextChanged(val value: TextFieldValue) : EditFieldEvent
    data object Save : EditFieldEvent
    data object Cancel : EditFieldEvent
}

sealed interface MessageItemEvent {
    data object ToggleActions : MessageItemEvent
    data object ToggleSelection : MessageItemEvent
}

sealed interface MessageDisplayStyle {
    data object Regular : MessageDisplayStyle
    data object Greeting : MessageDisplayStyle
}

sealed interface MessagePosition {
    data object Normal : MessagePosition
    data object LastAssistant : MessagePosition
}

sealed interface MessageInteractionState {
    data class Browsing(val isActionsExpanded: Boolean = false) : MessageInteractionState
    data class Editing(val text: TextFieldValue) : MessageInteractionState
    data object Generating : MessageInteractionState
    data class Selecting(val isSelected: Boolean) : MessageInteractionState
}

data class MessageItemState(
    val message: Message,
    val displayStyle: MessageDisplayStyle,
    val position: MessagePosition,
    val interactionState: MessageInteractionState,
    val character: Character,
    val messageWidth: MessageWidth,
)

data class MessageItemCallbacks(
    val onActionRowEvent: (ActionRowEvent) -> Unit,
    val onEditFieldEvent: (EditFieldEvent) -> Unit,
    val onVariantSelectorEvent: (VariantSelectorEvent) -> Unit,
    val onMessageItemEvent: (MessageItemEvent) -> Unit,
)

private val UserTextSize = 14.sp
private val AssistantTextSize = 14.5.sp
private val UserLineHeight = 23.8.sp
private val AssistantLineHeight = 25.35.sp
private val AssistantNameSize = 11.sp
private val AssistantNameGreetingSize = 16.sp
private val GreetingAvatarSize = 48.dp
private val RegularAvatarSize = 28.dp
private val GreetingHeaderGap = 16.dp
private val RegularHeaderGap = 10.dp

private val MessageVerticalGap = 32.dp
private val GreetingVerticalGap = 40.dp

private val EditFieldRadius = 16.dp
private val EditFieldPaddingH = 14.dp
private val EditFieldPaddingV = 10.dp
private val EditFieldMinHeight = 40.dp
private val EditFieldFontSize = 14.sp
private val EditButtonsGap = 8.dp

private val SwipeThreshold = 48.dp
private val SelectionTintAlpha = 0.08f
private val SelectionRowPaddingH = 12.dp
private val SelectionCheckboxPaddingTop = 8.dp
private val AssistantTextAlignmentCorrection = 1.dp

@Composable
internal fun MessageItem(
    state: MessageItemState,
    callbacks: MessageItemCallbacks,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val interaction = state.interactionState

    val isGreeting = state.displayStyle is MessageDisplayStyle.Greeting
    val isSelectionMode = interaction is MessageInteractionState.Selecting
    val isSelected = (interaction as? MessageInteractionState.Selecting)?.isSelected ?: false

    val tapModifier = rememberTapModifier(
        isSelectionMode = isSelectionMode,
        onToggleSelection = { callbacks.onMessageItemEvent(MessageItemEvent.ToggleSelection) },
        onToggleActions = { callbacks.onMessageItemEvent(MessageItemEvent.ToggleActions) },
    )
    val hoverModifier = rememberHoverModifier(isSelectionMode, interactionSource)
    val swipeModifier = if (state.position is MessagePosition.LastAssistant && isMobilePlatform && !isSelectionMode) {
        rememberSwipeModifier(
            enabled = interaction !is MessageInteractionState.Generating,
            onVariantSelectorEvent = callbacks.onVariantSelectorEvent,
        )
    } else {
        Modifier
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(if (isSelected) ForgeColors.spark.copy(alpha = SelectionTintAlpha) else ForgeColors.spark.copy(alpha = 0f))
            .padding(horizontal = SelectionRowPaddingH),
        verticalAlignment = Alignment.Top,
    ) {
        if (state.message.role == MessageRole.Assistant && isSelectionMode) {
            SelectionCheckbox(isSelected = isSelected)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isGreeting) GreetingVerticalGap else MessageVerticalGap)
                .then(hoverModifier),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(swipeModifier)
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
                    is MessageInteractionState.Generating,
                    is MessageInteractionState.Selecting -> {
                        MessageContent(
                            message = state.message,
                            displayStyle = state.displayStyle,
                            character = state.character,
                            messageWidth = state.messageWidth,
                        )
                    }
                }
            }

            if (!isSelectionMode) {
                val isActionsExpanded =
                    (interaction as? MessageInteractionState.Browsing)?.isActionsExpanded ?: false
                MessageActions(
                    message = state.message,
                    isGreeting = isGreeting,
                    position = state.position,
                    isActionsExpanded = isActionsExpanded,
                    interactionSource = interactionSource,
                    onActionRowEvent = callbacks.onActionRowEvent,
                    onVariantSelectorEvent = callbacks.onVariantSelectorEvent,
                )
            }
        }

        if (state.message.role == MessageRole.User && isSelectionMode) {
            SelectionCheckbox(isSelected = isSelected)
        }
    }
}

/**
 * Checkbox shown on the outer edge of a message row in selection mode.
 * Was duplicated for both User (trailing) and Assistant (leading) sides.
 */
@Composable
private fun SelectionCheckbox(isSelected: Boolean) {
    Box(modifier = Modifier.padding(top = SelectionCheckboxPaddingTop)) {
        CheckIndicator(isSelected = isSelected)
    }
}

/**
 * Tap handling shared between selection-mode and normal-mode taps on mobile.
 * isSelectionMode is part of the pointerInput key so the gesture detector
 * restarts correctly when the mode toggles (matches the previous branching behavior).
 */
@Composable
private fun rememberTapModifier(
    isSelectionMode: Boolean,
    onToggleSelection: () -> Unit,
    onToggleActions: () -> Unit,
): Modifier {
    if (!isMobilePlatform) return Modifier
    return Modifier.pointerInput(isSelectionMode) {
        detectTapGestures {
            if (isSelectionMode) onToggleSelection() else onToggleActions()
        }
    }
}

@Composable
private fun rememberHoverModifier(
    isSelectionMode: Boolean,
    interactionSource: MutableInteractionSource,
): Modifier {
    return if (!isSelectionMode && !isMobilePlatform) {
        Modifier.hoverable(interactionSource)
    } else {
        Modifier
    }
}

/**
 * Decides between the "last assistant message" action row (with variant selector)
 * and the plain action row for everything else, so MessageItem doesn't carry
 * that branching + indent math inline.
 */
@Composable
private fun MessageActions(
    message: Message,
    isGreeting: Boolean,
    position: MessagePosition,
    isActionsExpanded: Boolean,
    interactionSource: MutableInteractionSource,
    onActionRowEvent: (ActionRowEvent) -> Unit,
    onVariantSelectorEvent: (VariantSelectorEvent) -> Unit,
) {
    val assistantTextIndent = if (message.role == MessageRole.Assistant) {
        (if (isGreeting) GreetingAvatarSize else RegularAvatarSize) + AssistantHeaderGap + AssistantTextAlignmentCorrection
    } else {
        0.dp
    }

    @Composable
    fun SpecificActionRaw() {
        ActionRow(
            role = message.role,
            visible = isActionsExpanded,
            showRefresh = position is MessagePosition.LastAssistant,
            onActionRowEvent = onActionRowEvent,
            startPadding = if (position is MessagePosition.LastAssistant) 0.dp else assistantTextIndent,
            interactionSource = interactionSource,
        )
    }

    if (position is MessagePosition.LastAssistant) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = assistantTextIndent),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SpecificActionRaw()
            VariantSelector(
                variantIndex = message.variantIndex,
                variantCount = message.variantCount,
                onVariantSelectorEvent = onVariantSelectorEvent,
                enabled = message.variantCount > 1,
            )
        }
    } else {
        SpecificActionRaw()
    }
}

@Composable
private fun rememberSwipeModifier(
    enabled: Boolean,
    onVariantSelectorEvent: (VariantSelectorEvent) -> Unit,
): Modifier {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val draggableState = rememberDraggableState { delta ->
        offsetX += delta
    }
    return Modifier
        .offset { IntOffset(offsetX.roundToInt(), 0) }
        .draggable(
            state = draggableState,
            orientation = Orientation.Horizontal,
            enabled = enabled,
            onDragStopped = { velocity ->
                val threshold = SwipeThreshold.value
                when {
                    offsetX > threshold || velocity > 1000f -> {
                        onVariantSelectorEvent(VariantSelectorEvent.OnNextClick)
                        offsetX = 0f
                    }
                    offsetX < -threshold || velocity < -1000f -> {
                        onVariantSelectorEvent(VariantSelectorEvent.OnPrevClick)
                        offsetX = 0f
                    }
                    else -> offsetX = 0f
                }
            },
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
            letter = character.letter,
            avatarBytes = character.avatarBytes,
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
    letter: String,
    avatarBytes: ByteArray?,
    isGreeting: Boolean,
) {
    val avatarSize = if (isGreeting) GreetingAvatarSize else RegularAvatarSize
    val nameSize = if (isGreeting) AssistantNameGreetingSize else AssistantNameSize
    val headerGap = if (isGreeting) GreetingHeaderGap else RegularHeaderGap
    val avatarFontSize = if (isGreeting) 12.sp else 10.sp
    val textIndent = avatarSize + AssistantHeaderGap + AssistantTextAlignmentCorrection

    Column(modifier = Modifier.fillMaxWidth()) {
        AssistantHeader(
            name = name,
            letter = letter,
            avatarBytes = avatarBytes,
            avatarSize = avatarSize,
            nameSize = nameSize,
            avatarFontSize = avatarFontSize,
            modifier = Modifier.padding(bottom = headerGap),
        )
        MarkdownText(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = textIndent),
        )
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