package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VariantCounterFontSize = 11.sp
private val VariantIconSize = 18.dp
private val VariantTouchTargetSize = 28.dp

sealed interface VariantSelectorEvent {
    data object OnPrevClick : VariantSelectorEvent
    data object OnNextClick : VariantSelectorEvent
}

@Composable
internal fun VariantSelector(
    variantIndex: Int,
    variantCount: Int,
    onVariantSelectorEvent: (VariantSelectorEvent) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val cs = MaterialTheme.colorScheme

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        IconButton(
            onClick = { onVariantSelectorEvent(VariantSelectorEvent.OnPrevClick) },
            modifier = Modifier.size(VariantTouchTargetSize),
            enabled = enabled && variantIndex > 0,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                modifier = Modifier.size(VariantIconSize),
                tint = cs.onSurfaceVariant,
            )
        }

        Text(
            text = "${variantIndex + 1}/$variantCount",
            fontSize = VariantCounterFontSize,
            color = cs.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 2.dp),
            maxLines = 1,
            softWrap = false,
        )

        IconButton(
            onClick = { onVariantSelectorEvent(VariantSelectorEvent.OnNextClick) },
            modifier = Modifier.size(VariantTouchTargetSize),
            enabled = enabled && variantIndex < variantCount - 1,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(VariantIconSize),
                tint = cs.onSurfaceVariant,
            )
        }
    }
}
