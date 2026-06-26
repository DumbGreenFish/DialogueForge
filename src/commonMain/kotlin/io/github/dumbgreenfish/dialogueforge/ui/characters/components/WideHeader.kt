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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.characters_create
import io.github.dumbgreenfish.dialogueforge.generated.resources.characters_import
import io.github.dumbgreenfish.dialogueforge.generated.resources.characters_subtitle
import io.github.dumbgreenfish.dialogueforge.generated.resources.characters_total
import io.github.dumbgreenfish.dialogueforge.generated.resources.nav_characters
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersIntent
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersState
import io.github.dumbgreenfish.dialogueforge.ui.common.rememberFilePicker
import org.jetbrains.compose.resources.stringResource

private val AcceptedFileTypes = listOf(".png", ".json", ".charx")

private val HeaderPaddingTop     = 24.dp
private val HeaderPaddingBottom  = 10.dp
private val TitleCountGap        = 14.dp
private val TitleSubtitleSpacer  = 6.dp
private val SubtitleSearchSpacer = 18.dp
private val SearchRowGap         = 14.dp
private val SearchFilterSpacer   = 16.dp
private val FilterRowGap         = 10.dp

@Composable
internal fun WideHeader(state: CharactersState, onIntent: (CharactersIntent) -> Unit) {
    val cs = MaterialTheme.colorScheme
    val launchPicker = rememberFilePicker { bytes, filename ->
        onIntent(CharactersIntent.ImportFile(bytes, filename))
    }
    Column(modifier = Modifier.padding(top = HeaderPaddingTop, bottom = HeaderPaddingBottom)) {
        Row(horizontalArrangement = Arrangement.spacedBy(SearchRowGap), verticalAlignment = Alignment.CenterVertically) {
            SearchField(
                value = state.query,
                onChange = { onIntent(CharactersIntent.SearchChanged(it)) },
                modifier = Modifier.weight(1f),
            )
            OutlinedButton(onClick = launchPicker) {
                Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(18.dp))
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
        Spacer(Modifier.height(SearchFilterSpacer))
        Row(horizontalArrangement = Arrangement.spacedBy(FilterRowGap), verticalAlignment = Alignment.CenterVertically) {
            FilterControl(
                filter        = state.filter,
                availableTags = state.availableTags,
                onIntent      = onIntent,
            )
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
}