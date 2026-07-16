package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val CheckboxSlotWidth = 32.dp
private val CheckboxSlotPaddingStart = 8.dp

@Composable
internal fun SelectionCheckboxSlot(
    isSelectionMode: Boolean,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(CheckboxSlotWidth)
            .padding(start = CheckboxSlotPaddingStart),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = isSelectionMode,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            CheckIndicator(isSelected = isSelected)
        }
    }
}
