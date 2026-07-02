package io.github.dumbgreenfish.dialogueforge.ui.characters.components.header

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import io.github.dumbgreenfish.dialogueforge.generated.resources.characters_total
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersIntent
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersState
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CompactHeader(state: CharactersState, onIntent: (CharactersIntent) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text  = stringResource(Res.string.characters_total, state.displayed.size),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight          = FontWeight.W500,
                fontFeatureSettings = "tnum",
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.weight(1f))
        ViewToggle(mode = state.viewMode, onToggle = { onIntent(CharactersIntent.ViewModeChanged(it)) })
    }
}
