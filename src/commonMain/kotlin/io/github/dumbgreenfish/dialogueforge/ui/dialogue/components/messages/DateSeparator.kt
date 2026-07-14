package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape

private val SeparatorPaddingV = 4.dp
private val SeparatorPaddingH = 12.dp
private val SeparatorMarginV = 8.dp

@Composable
internal fun DateSeparator(
    label: String,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = SeparatorMarginV),
        horizontalArrangement = Arrangement.Center,
    ) {
        Surface(
            shape = ForgeShape.pill,
            color = cs.surfaceContainerLow,
            border = androidx.compose.foundation.BorderStroke(1.dp, cs.outlineVariant),
        ) {
            Text(
                text = label.uppercase(),
                modifier = Modifier.padding(
                    vertical = SeparatorPaddingV,
                    horizontal = SeparatorPaddingH,
                ),
                color = ForgeColors.onSurfaceFaint,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}
