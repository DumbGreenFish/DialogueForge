package io.github.dumbgreenfish.dialogueforge.ui.characters.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.characters_search_placeholder
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SearchField(value: String, onChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    TextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
        placeholder = { Text(stringResource(Res.string.characters_search_placeholder)) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        singleLine = true,
        shape = ForgeShape.pill,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = cs.surface,
            focusedContainerColor = cs.surface,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedLeadingIconColor = cs.onSurfaceVariant,
            focusedLeadingIconColor = cs.onSurfaceVariant,
        ),
    )
}
