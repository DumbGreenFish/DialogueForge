package io.github.dumbgreenfish.dialogueforge.ui.characters.components.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.menu.ContextAction
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.common.CharacterAvatar
import org.jetbrains.compose.resources.stringResource

private const val VISIBLE_TAGS_MAX  = 3
private const val TAGLINE_MAX_LINES = 2
private val MoreButtonSize = 36.dp
private val MoreIconSize   = 20.dp

@Composable
internal fun CharacterCardList(
    char: Character,
    onClick: () -> Unit,
    onDeleteRequest: (Character) -> Unit,
    isCompact: Boolean,
) {
    CharacterCardShell(char, onClick, onDeleteRequest, isCompact) { menuExpanded, onMoreClick, onMenuDismiss, actions ->
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CharacterAvatar(letter = char.letter, modifier = Modifier.size(60.dp), shape = ForgeShape.avatar, fontSize = 25.sp, avatarBytes = char.avatarBytes)
            ListContent(
                char          = char,
                isCompact     = isCompact,
                onMoreClick   = onMoreClick,
                menuExpanded  = menuExpanded,
                onMenuDismiss = onMenuDismiss,
                actions       = actions,
            )
        }
    }
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
            CardTagChips(visibleTags, extraCount)
        }
    }
}