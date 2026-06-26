package io.github.dumbgreenfish.dialogueforge.ui.characters.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersIntent
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharacterFilter
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Tag

private val DropdownWidth           = 360.dp
private val DropdownMaxHeight       = 460.dp
private val DropdownGap             = 8.dp
private val DropdownAnchorHeight    = 44.dp   // высота labeled FilterButton
private val DropdownBorderWidth     = 1.dp
private val DropdownShadowElevation = 12.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FilterControl(
    filter: CharacterFilter,
    availableTags: List<Tag>,
    onIntent: (CharactersIntent) -> Unit,
    modifier: Modifier = Modifier,
    iconOnly: Boolean = false,
) {
    val cs = MaterialTheme.colorScheme
    var open by remember { mutableStateOf(false) }

    if (iconOnly) {
        // Телефон/compact: bottom-sheet (заглушка снизу).
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        FilterButton(
            activeCount = filter.activeCount,
            open        = open,
            onClick     = { open = true },
            modifier    = modifier,
            iconOnly    = true,
        )
        if (open) {
            ModalBottomSheet(
                onDismissRequest = { open = false },
                sheetState       = sheetState,
                containerColor   = cs.surfaceVariant,
            ) {
                FilterPanel(filter = filter, availableTags = availableTags, onIntent = onIntent)
            }
        }
    } else {
        // Десктоп/планшет: dropdown под кнопкой, закрывается по клику вне / Esc.
        val density = LocalDensity.current
        Box(modifier = modifier) {
            FilterButton(
                activeCount = filter.activeCount,
                open        = open,
                onClick     = { open = !open },
                iconOnly    = false,
            )
            if (open) {
                Popup(
                    alignment        = Alignment.TopStart,
                    offset           = IntOffset(0, with(density) { (DropdownAnchorHeight + DropdownGap).roundToPx() }),
                    onDismissRequest = { open = false },
                    properties       = PopupProperties(focusable = true),
                ) {
                    Surface(
                        shape           = MaterialTheme.shapes.large,
                        color           = cs.surfaceVariant,
                        border          = BorderStroke(DropdownBorderWidth, cs.outline),
                        shadowElevation = DropdownShadowElevation,
                        modifier        = Modifier.width(DropdownWidth).heightIn(max = DropdownMaxHeight),
                    ) {
                        FilterPanel(filter = filter, availableTags = availableTags, onIntent = onIntent)
                    }
                }
            }
        }
    }
}