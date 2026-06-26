package io.github.dumbgreenfish.dialogueforge.ui.characters.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.filter_remove_tag
import org.jetbrains.compose.resources.stringResource

private val PillHeight        = 30.dp
private val PillPaddingStart   = 11.dp
private val PillPaddingEnd     = 6.dp
private val PillGap            = 5.dp
private val DotSize            = 6.dp
private val RemoveButtonSize   = 20.dp
private val RemoveIconSize     = 14.dp
private val BorderWidth        = 1.dp
private val PillFontSize       = 13.sp

@Composable
internal fun FilterTagPill(
    label: String,
    accent: Color,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .height(PillHeight)
            .clip(ForgeShape.pill)
            .background(ForgeColors.surfaceContainerHigh)
            .border(BorderWidth, cs.outlineVariant, ForgeShape.pill)
            .padding(start = PillPaddingStart, end = PillPaddingEnd),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PillGap),
    ) {
        Box(Modifier.size(DotSize).clip(CircleShape).background(accent))
        Text(label, color = cs.onSurface, fontSize = PillFontSize, fontWeight = FontWeight.W600)
        Box(
            modifier = Modifier.size(RemoveButtonSize).clip(CircleShape).clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = stringResource(Res.string.filter_remove_tag),
                tint = ForgeColors.onSurfaceFaint,
                modifier = Modifier.size(RemoveIconSize),
            )
        }
    }
}