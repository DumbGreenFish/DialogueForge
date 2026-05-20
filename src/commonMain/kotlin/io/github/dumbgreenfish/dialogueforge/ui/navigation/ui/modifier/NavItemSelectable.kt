package io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.modifier

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role

@Composable
internal fun Modifier.navItemSelectable(
    isActive: Boolean,
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource,
) = selectable(
    selected = isActive,
    onClick = onClick,
    role = Role.Tab,
    interactionSource = interactionSource,
    indication = null,
).pointerHoverIcon(PointerIcon.Hand)
