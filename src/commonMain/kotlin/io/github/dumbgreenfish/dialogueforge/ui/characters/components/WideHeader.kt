package io.github.dumbgreenfish.dialogueforge.ui.characters.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.characters_create
import io.github.dumbgreenfish.dialogueforge.generated.resources.characters_import
import io.github.dumbgreenfish.dialogueforge.generated.resources.characters_subtitle
import io.github.dumbgreenfish.dialogueforge.generated.resources.nav_characters
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersIntent
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersState
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharacterFilter
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharactersViewMode
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun WideHeader(state: CharactersState, onIntent: (CharactersIntent) -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(Res.string.nav_characters), style = MaterialTheme.typography.displaySmall, color = cs.onSurface)
            Text("${state.displayed.size}", style = MaterialTheme.typography.titleMedium, color = ForgeColors.onSurfaceFaint)
        }
        Spacer(Modifier.height(4.dp))
        Text(stringResource(Res.string.characters_subtitle), style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            SearchField(
                value = state.query,
                onChange = { onIntent(CharactersIntent.SearchChanged(it)) },
                modifier = Modifier.weight(1f),
            )
            OutlinedButton(onClick = {}) {
                Icon(Icons.Filled.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(Res.string.characters_import))
            }
            FilledTonalButton(
                onClick = {},
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = cs.primary,
                    contentColor = cs.onPrimary,
                ),
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(Res.string.characters_create))
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            FilterChipsRow(
                activeFilter = state.activeFilter,
                availableTags = state.availableTags,
                onIntent = onIntent,
                modifier = Modifier.weight(1f),
            )
            ViewToggle(mode = state.viewMode, onToggle = { onIntent(CharactersIntent.ViewModeChanged(it)) })
        }
    }
}
