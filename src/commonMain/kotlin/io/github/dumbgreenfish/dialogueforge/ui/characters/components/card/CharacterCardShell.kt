package io.github.dumbgreenfish.dialogueforge.ui.characters.components.card

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.menu.CharacterContextSheet
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.menu.ContextAction
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.menu.characterContextActions
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character

private val CardBorderWidth = 1.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CharacterCardShell(
    char: Character,
    onClick: () -> Unit,
    onDeleteRequest: (Character) -> Unit,
    isCompact: Boolean,
    content: @Composable (
        menuExpanded: Boolean,
        onMoreClick: () -> Unit,
        onMenuDismiss: () -> Unit,
        actions: List<ContextAction>,
    ) -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val borderColor = if (char.pinned) cs.outlineVariant else cs.outline
    var menuExpanded by remember { mutableStateOf(false) }
    var sheetExpanded by remember { mutableStateOf(false) }
    val actions = characterContextActions(onDelete = { onDeleteRequest(char) })

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(CardBorderWidth, borderColor, MaterialTheme.shapes.medium)
            .combinedClickable(
                onClick = onClick,
                onLongClick = if (isCompact) {
                    { sheetExpanded = true }
                } else {
                    null
                },
            ),
        shape = MaterialTheme.shapes.medium,
        color = cs.surface,
    ) {
        content(
            menuExpanded,
            { menuExpanded = true },
            { menuExpanded = false },
            actions,
        )
    }
    CharacterContextSheet(
        expanded = sheetExpanded,
        onDismiss = { sheetExpanded = false },
        actions = actions,
    )
}
