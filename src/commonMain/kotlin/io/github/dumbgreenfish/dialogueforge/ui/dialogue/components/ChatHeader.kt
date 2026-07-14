package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.common.CharacterAvatar

private val HeaderHeight = 48.dp
private val HeaderPaddingH = 16.dp
private val HeaderGap = 12.dp
private val ActionBtnTarget = 36.dp
private val AvatarSize = 28.dp
private val AvatarFontSize = 11.sp
private val NameFontSize = 13.sp
private val AvatarBorderWidth = 1.dp
private val ModelSelectorGap = 4.dp

@Composable
internal fun ChatHeader(
    character: Character?,
    modelName: String,
    onBack: () -> Unit,
    onHistory: () -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(HeaderHeight)
            .padding(horizontal = HeaderPaddingH),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HeaderGap),
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        character?.let { char ->
            AvatarWithName(char)
        }

        Spacer(Modifier.weight(1f))

        ModelSelector(modelName = modelName)
        HeaderActionButton(Icons.Filled.History, onClick = onHistory)
        HeaderActionButton(Icons.Filled.Add, onClick = onAdd)
    }
}

@Composable
private fun RowScope.AvatarWithName(char: Character) {
    val cs = MaterialTheme.colorScheme

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HeaderGap),
    ) {
        Box(
            modifier = Modifier
                .size(AvatarSize)
                .clip(CircleShape)
                .border(
                    width = AvatarBorderWidth,
                    color = cs.secondary.copy(alpha = 0.2f),
                    shape = CircleShape,
                ),
        ) {
            CharacterAvatar(
                letter = char.letter,
                modifier = Modifier.size(AvatarSize),
                shape = CircleShape,
                fontSize = AvatarFontSize,
                avatarBytes = char.avatarBytes,
            )
        }
        Text(
            text = char.name,
            fontSize = NameFontSize,
            fontWeight = FontWeight.Medium,
            color = cs.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ModelSelector(
    modelName: String,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(enabled = false) {}
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ModelSelectorGap),
    ) {
        Text(
            text = modelName.ifBlank { "Model" },
            fontSize = 11.sp,
            color = cs.onSurfaceVariant,
        )
        Icon(
            Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = cs.onSurfaceVariant,
        )
    }
}

@Composable
private fun HeaderActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    IconButton(
        onClick = onClick,
        modifier = Modifier.requiredSize(ActionBtnTarget),
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = cs.onSurfaceVariant,
        )
    }
}
