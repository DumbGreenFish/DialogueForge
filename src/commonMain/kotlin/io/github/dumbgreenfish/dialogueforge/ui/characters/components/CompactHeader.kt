package io.github.dumbgreenfish.dialogueforge.ui.characters.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersIntent
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersState

@Composable
internal fun CompactHeader(state: CharactersState, onIntent: (CharactersIntent) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SearchField(
            value = state.query,
            onChange = { onIntent(CharactersIntent.SearchChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
        )
        FilterChipsRow(
            activeFilter = state.activeFilter,
            availableTags = state.availableTags,
            onIntent = onIntent,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${state.displayed.size}", style = MaterialTheme.typography.labelSmall, color = ForgeColors.onSurfaceFaint)
            Spacer(Modifier.weight(1f))
            ViewToggle(mode = state.viewMode, onToggle = { onIntent(CharactersIntent.ViewModeChanged(it)) })
        }
    }
}
