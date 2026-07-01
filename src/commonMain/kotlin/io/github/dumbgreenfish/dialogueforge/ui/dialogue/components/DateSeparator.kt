package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private val SeparatorPaddingV = 3.dp
private val SeparatorPaddingH = 12.dp
private val SeparatorFontSize = 11.sp
private val SeparatorMarginT = 14.dp
private val SeparatorMarginB = 8.dp

internal fun formatDateLabel(ms: Long): String {
    val dt = Instant.fromEpochMilliseconds(ms).toLocalDateTime(TimeZone.currentSystemDefault())
    val m = dt.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    return "${dt.dayOfMonth} $m ${dt.year}"
}

@Composable
internal fun DateSeparator(
    label: String,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = SeparatorMarginT, bottom = SeparatorMarginB),
        horizontalArrangement = Arrangement.Center,
    ) {
        Surface(
            shape = RoundedCornerShape(100.dp),
            color = cs.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, cs.outline),
        ) {
            Text(
                text = label.uppercase(),
                modifier = Modifier.padding(
                    vertical = SeparatorPaddingV,
                    horizontal = SeparatorPaddingH,
                ),
                color = ForgeColors.onSurfaceFaint,
                fontSize = SeparatorFontSize,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
