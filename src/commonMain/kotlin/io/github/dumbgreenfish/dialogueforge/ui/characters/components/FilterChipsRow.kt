package io.github.dumbgreenfish.dialogueforge.ui.characters.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.filter_all
import io.github.dumbgreenfish.dialogueforge.generated.resources.filter_pinned
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersIntent
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharacterFilter
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FilterChipsRow(
    activeFilter: CharacterFilter,
    availableTags: List<String>,
    onIntent: (CharactersIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            label = stringResource(Res.string.filter_all),
            active = activeFilter == CharacterFilter.All,
            onClick = { onIntent(CharactersIntent.FilterChanged(CharacterFilter.All)) },
        )
        FilterChip(
            label = stringResource(Res.string.filter_pinned),
            active = activeFilter == CharacterFilter.Pinned,
            icon = Icons.Filled.PushPin,
            onClick = { onIntent(CharactersIntent.FilterChanged(CharacterFilter.Pinned)) },
        )
        availableTags.forEach { tag ->
            FilterChip(
                label = tag,
                active = activeFilter == CharacterFilter.Tag(tag),
                onClick = { onIntent(CharactersIntent.FilterChanged(CharacterFilter.Tag(tag))) },
            )
        }
    }
}

@Composable
private fun FilterChip(label: String, active: Boolean, onClick: () -> Unit, icon: ImageVector? = null) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .height(38.dp)
            .border(1.dp, if (active) cs.primary else cs.outlineVariant, ForgeShape.pill)
            .background(if (active) cs.primaryContainer else Color.Transparent, ForgeShape.pill)
            .clickable(onClick = onClick)
            .padding(horizontal = if (icon != null) 12.dp else 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (active) ForgeColors.spark else cs.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (active) cs.onPrimaryContainer else cs.onSurface,
        )
    }
}
