package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors

private val SeparatorMarginV = 16.dp
private val LineHeight = 1.dp
private val TextPaddingH = 12.dp
private val TextPaddingV = 4.dp
private val LineAlpha = 0.08f
private val PillRadius = 8.dp

@Composable
internal fun DateSeparator(
    label: String,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    val lineColor = cs.onSurface.copy(alpha = LineAlpha)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = SeparatorMarginV),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(LineHeight)
                    .background(lineColor),
            )
            Text(
                text = label.uppercase(),
                modifier = Modifier
                    .padding(horizontal = TextPaddingH)
                    .clip(RoundedCornerShape(PillRadius))
                    .background(cs.background.copy(alpha = 0.7f))
                    .padding(horizontal = TextPaddingH, vertical = TextPaddingV),
                color = ForgeColors.onSurfaceFaint,
                style = MaterialTheme.typography.labelSmall,
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(LineHeight)
                    .background(lineColor),
            )
        }
    }
}
