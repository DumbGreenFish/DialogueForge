package io.github.dumbgreenfish.dialogueforge.ui.characters.components.header

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharactersViewMode

private val BorderWidth      = 1.dp
private val ToggleButtonW    = 44.dp
private val ToggleButtonH    = 38.dp
private val ToggleIconSize   = 20.dp

@Composable
internal fun ViewToggle(mode: CharactersViewMode, onToggle: (CharactersViewMode) -> Unit) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .clip(ForgeShape.pill)
            .border(BorderWidth, cs.outlineVariant, ForgeShape.pill)
            .background(Color.Transparent, ForgeShape.pill),
    ) {
        ToggleButton(
            icon = Icons.Outlined.ViewAgenda,
            iconActive = Icons.Filled.ViewAgenda,
            active = mode == CharactersViewMode.List,
            onClick = { onToggle(CharactersViewMode.List) },
        )
        ToggleButton(
            icon = Icons.Outlined.GridView,
            iconActive = Icons.Filled.GridView,
            active = mode == CharactersViewMode.Grid,
            onClick = { onToggle(CharactersViewMode.Grid) },
        )
    }
}

@Composable
private fun ToggleButton(icon: ImageVector, iconActive: ImageVector, active: Boolean, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .size(width = ToggleButtonW, height = ToggleButtonH)
            .background(if (active) cs.primaryContainer else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (active) iconActive else icon,
            contentDescription = null,
            tint = if (active) ForgeColors.spark else cs.onSurfaceVariant,
            modifier = Modifier.size(ToggleIconSize),
        )
    }
}
