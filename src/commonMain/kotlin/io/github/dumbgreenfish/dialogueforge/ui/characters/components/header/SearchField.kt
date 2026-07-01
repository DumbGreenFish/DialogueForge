package io.github.dumbgreenfish.dialogueforge.ui.characters.components.header

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.characters_search_placeholder
import org.jetbrains.compose.resources.stringResource

private val FieldHeight    = 48.dp
private val FieldPaddingH  = 16.dp
private val IconGap        = 12.dp
private val SearchIconSize = 20.dp
private val BorderWidth    = 1.dp
private val InputFontSize  = 15.5.sp

@Composable
internal fun SearchField(value: String, onChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val textStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = InputFontSize,
        color    = cs.onSurface,
    )
    BasicTextField(
        value         = value,
        onValueChange = onChange,
        modifier      = modifier.height(FieldHeight),
        singleLine    = true,
        textStyle     = textStyle,
        cursorBrush   = SolidColor(cs.primary),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(cs.surface, ForgeShape.pill)
                    .border(BorderWidth, cs.outline, ForgeShape.pill)
                    .padding(horizontal = FieldPaddingH),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(IconGap),
            ) {
                Icon(
                    imageVector        = Icons.Filled.Search,
                    contentDescription = null,
                    tint               = cs.onSurfaceVariant,
                    modifier           = Modifier.size(SearchIconSize),
                )
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(
                            text  = stringResource(Res.string.characters_search_placeholder),
                            style = textStyle,
                            color = cs.onSurfaceVariant,
                        )
                    }
                    innerTextField()
                }
            }
        },
    )
}