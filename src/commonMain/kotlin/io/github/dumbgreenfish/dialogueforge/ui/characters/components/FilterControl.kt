package io.github.dumbgreenfish.dialogueforge.ui.characters.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersIntent
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharacterFilter
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Tag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FilterControl(
    filter: CharacterFilter,
    availableTags: List<Tag>,
    onIntent: (CharactersIntent) -> Unit,
    modifier: Modifier = Modifier,
    iconOnly: Boolean = false,
) {
    var open by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    FilterButton(
        activeCount = filter.activeCount,
        open        = open,
        onClick     = { open = true },
        modifier    = modifier,
        iconOnly    = iconOnly,
    )

    if (open) {
        ModalBottomSheet(
            onDismissRequest = { open = false },
            sheetState       = sheetState,
            containerColor   = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            FilterPanel(
                filter        = filter,
                availableTags = availableTags,
                onIntent      = onIntent,
            )
        }
    }
}