package io.github.dumbgreenfish.dialogueforge.ui.characters.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Tag

private val InputHeight     = 40.dp
private val InputPaddingH    = 12.dp
private val InputGap         = 8.dp
private val LeadingIconSize  = 16.dp
private val InputFontSize    = 14.sp
private val BorderWidth      = 1.dp
private val DotSize          = 6.dp
private val SuggestionsGap        = 4.dp
private val SuggestionsMaxHeight  = 200.dp
private val SuggestionsPadding    = 4.dp
private val SuggestionPaddingH    = 10.dp
private val SuggestionPaddingV    = 8.dp
private val SuggestionGap         = 8.dp
private val SuggestionFontSize    = 13.5.sp
private const val MAX_SUGGESTIONS = 8

@Composable
internal fun FilterTagInput(
    placeholder: String,
    leadingIcon: ImageVector,
    accent: Color,
    chosen: List<Tag>,
    otherChosen: List<Tag>,
    allTags: List<Tag>,
    onAdd: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    var query by remember { mutableStateOf("") }
    var focused by remember { mutableStateOf(false) }

    val taken = remember(chosen, otherChosen) { (chosen + otherChosen).toSet() }
    val ql = query.trim().lowercase()
    val suggestions = remember(ql, taken, allTags) {
        allTags.filter { it !in taken && (ql.isEmpty() || it.value.contains(ql)) }.take(MAX_SUGGESTIONS)
    }
    val showSuggestions = query.isNotEmpty() && suggestions.isNotEmpty()

    fun add(tag: String) {
        val v = tag.trim().lowercase()
        if (v.isNotEmpty() && Tag(v) !in taken) {
            onAdd(v)
            query = ""
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(InputHeight)
                .clip(MaterialTheme.shapes.small)
                .background(cs.background)
                .border(BorderWidth, if (focused) accent else cs.outlineVariant, MaterialTheme.shapes.small)
                .padding(horizontal = InputPaddingH),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(InputGap),
        ) {
            Icon(leadingIcon, contentDescription = null, tint = ForgeColors.onSurfaceFaint, modifier = Modifier.size(LeadingIconSize))
            BasicTextField(
                value           = query,
                onValueChange   = { query = it },
                modifier        = Modifier.weight(1f).onFocusChanged { focused = it.isFocused },
                singleLine      = true,
                textStyle       = MaterialTheme.typography.bodyMedium.copy(fontSize = InputFontSize, color = cs.onSurface),
                cursorBrush     = SolidColor(accent),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { add(suggestions.firstOrNull()?.value ?: query) }),
                decorationBox   = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (query.isEmpty()) {
                            Text(placeholder, style = MaterialTheme.typography.bodyMedium.copy(fontSize = InputFontSize), color = ForgeColors.onSurfaceFaint)
                        }
                        inner()
                    }
                },
            )
        }
        if (showSuggestions) {
            Spacer(Modifier.height(SuggestionsGap))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = SuggestionsMaxHeight)
                    .clip(MaterialTheme.shapes.small)
                    .background(ForgeColors.surfaceContainerHighest)
                    .border(BorderWidth, cs.outlineVariant, MaterialTheme.shapes.small)
                    .verticalScroll(rememberScrollState())
                    .padding(SuggestionsPadding),
            ) {
                suggestions.forEach { tag ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.extraSmall)
                            .clickable { add(tag.value) }
                            .padding(horizontal = SuggestionPaddingH, vertical = SuggestionPaddingV),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(SuggestionGap),
                    ) {
                        Box(Modifier.size(DotSize).clip(CircleShape).background(accent))
                        Text(tag.value, color = cs.onSurface, fontSize = SuggestionFontSize, fontWeight = FontWeight.W500)
                    }
                }
            }
        }
    }
}