package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.bubble

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape

private val SystemChipPaddingV = 4.dp
private val SystemChipPaddingH = 12.dp
private val SystemInfoIconSize = 12.dp
private val SystemChipMargin = 8.dp

@Composable
internal fun SystemMessageChip(text: String, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Surface(
            shape = ForgeShape.pill,
            color = cs.surfaceVariant,
            border = BorderStroke(1.dp, cs.outline),
            modifier = Modifier.padding(vertical = SystemChipMargin),
        ) {
            Row(
                modifier = Modifier.padding(
                    vertical = SystemChipPaddingV,
                    horizontal = SystemChipPaddingH,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = null,
                    modifier = Modifier.size(SystemInfoIconSize),
                    tint = ForgeColors.onSurfaceFaint,
                )
                Spacer(Modifier.size(6.dp))
                Text(
                    text = text,
                    color = ForgeColors.onSurfaceFaint,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
