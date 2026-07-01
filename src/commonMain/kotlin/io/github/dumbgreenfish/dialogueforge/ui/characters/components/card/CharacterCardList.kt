package io.github.dumbgreenfish.dialogueforge.ui.characters.components.card

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.character_menu_more
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.menu.CharacterContextMenu
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.menu.CharacterContextSheet
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.menu.ContextAction
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.menu.characterContextActions
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.common.CharacterAvatar
import org.jetbrains.compose.resources.stringResource

private const val VISIBLE_TAGS_MAX  = 3
private const val TAGLINE_MAX_LINES = 2
private val MoreButtonSize = 36.dp
private val MoreIconSize   = 20.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CharacterCardList(
    char: Character,
    onClick: () -> Unit,
    onDeleteRequest: (Character) -> Unit,
    isCompact: Boolean,
) {
    val cs = MaterialTheme.colorScheme
    val borderColor = if (char.pinned) cs.outlineVariant else cs.outline
    var menuExpanded by remember { mutableStateOf(false) }
    var sheetExpanded by remember { mutableStateOf(false) }
    val actions = characterContextActions(onDelete = { onDeleteRequest(char) })
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, MaterialTheme.shapes.medium)
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
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CharacterAvatar(letter = char.letter, modifier = Modifier.size(60.dp), shape = ForgeShape.avatar, fontSize = 25.sp, avatarBytes = char.avatarBytes)
            ListContent(
                char          = char,
                isCompact     = isCompact,
                onMoreClick   = { menuExpanded = true },
                menuExpanded  = menuExpanded,
                onMenuDismiss = { menuExpanded = false },
                actions       = actions,
            )
        }
    }
    CharacterContextSheet(
        expanded = sheetExpanded,
        onDismiss = { sheetExpanded = false },
        actions = actions
    )
}

@Composable
private fun RowScope.ListContent(
    char: Character,
    isCompact: Boolean,
    onMoreClick: () -> Unit,
    menuExpanded: Boolean,
    onMenuDismiss: () -> Unit,
    actions: List<ContextAction>,
) {
    val cs = MaterialTheme.colorScheme
    val visibleTags = char.tags.take(VISIBLE_TAGS_MAX)
    val extraCount = char.tags.size - visibleTags.size
    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(char.name, style = MaterialTheme.typography.titleSmall, color = cs.onSurface)
            if (char.pinned) {
                Spacer(Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Filled.PushPin,
                    contentDescription = null,
                    tint = ForgeColors.spark,
                    modifier = Modifier.size(15.dp).graphicsLayer { rotationZ = ForgeShape.pinRotationDeg },
                )
            }
            Spacer(Modifier.weight(1f))
            Text(char.lastUsed, style = MaterialTheme.typography.bodySmall, color = ForgeColors.onSurfaceFaint)
            if (!isCompact) {
                Spacer(Modifier.width(4.dp))
                Box {
                    IconButton(onClick = onMoreClick, modifier = Modifier.size(MoreButtonSize)) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(Res.string.character_menu_more),
                            tint = cs.onSurfaceVariant,
                            modifier = Modifier.size(MoreIconSize),
                        )
                    }
                    CharacterContextMenu(
                        expanded = menuExpanded,
                        onDismiss = onMenuDismiss,
                        actions = actions
                    )
                }
            }
        }
        Text(
            text = char.tagline,
            style = MaterialTheme.typography.bodyMedium,
            color = cs.onSurfaceVariant,
            maxLines = TAGLINE_MAX_LINES,
            overflow = TextOverflow.Ellipsis,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            visibleTags.forEachIndexed { i, tag ->
                CharacterTag(label = tag.value, tone = if (i == 0) CharacterTagTone.Primary else CharacterTagTone.Secondary)
            }
            if (extraCount > 0) CharacterTag(label = "+$extraCount")
        }
    }
}