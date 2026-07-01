package io.github.dumbgreenfish.dialogueforge.ui.characters.components.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.characters_filter
import org.jetbrains.compose.resources.stringResource

private val IconOnlySize     = 48.dp
private val IconOnlyIconSize = 20.dp
private val LabeledHeight    = 44.dp
private val LabeledIconSize  = 18.dp
private val LabeledFontSize  = 14.5.sp
private val LabeledGap       = 8.dp
private val LabeledPaddingStart       = 14.dp
private val LabeledPaddingEndActive   = 10.dp
private val LabeledPaddingEndInactive = 16.dp
private val BorderWidth      = 1.dp

private val BadgeBleed             = 2.dp
private val BadgeIconOnlyHeight    = 16.dp
private val BadgeIconOnlyPaddingH  = 4.dp
private val BadgeIconOnlyFontSize  = 10.5.sp
private val BadgeIconOnlyBorder    = 2.dp
private val BadgeLabeledHeight     = 18.dp
private val BadgeLabeledPaddingH   = 5.dp
private val BadgeLabeledFontSize   = 11.5.sp

@Composable
internal fun FilterButton(
    activeCount: Int,
    open: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconOnly: Boolean = false,
) {
    val cs = MaterialTheme.colorScheme
    val active = activeCount > 0
    val highlighted = open || active
    val borderColor = if (highlighted) cs.primary else cs.outlineVariant
    val bg = if (highlighted) cs.primaryContainer else Color.Transparent
    val iconTint = if (active) ForgeColors.spark else cs.onSurfaceVariant
    val iconVector = if (active) Icons.Filled.Tune else Icons.Outlined.Tune
    val label = stringResource(Res.string.characters_filter)

    if (iconOnly) {
        Box(modifier = modifier) {
            Box(
                modifier = Modifier
                    .size(IconOnlySize)
                    .clip(ForgeShape.pill)
                    .background(bg)
                    .border(BorderWidth, borderColor, ForgeShape.pill)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(iconVector, contentDescription = label, tint = iconTint, modifier = Modifier.size(IconOnlyIconSize))
            }
            if (active) {
                FilterBadge(
                    count       = activeCount,
                    height      = BadgeIconOnlyHeight,
                    paddingH    = BadgeIconOnlyPaddingH,
                    fontSize    = BadgeIconOnlyFontSize,
                    borderWidth = BadgeIconOnlyBorder,
                    modifier    = Modifier.align(Alignment.TopEnd).offset(x = BadgeBleed, y = -BadgeBleed),
                )
            }
        }
    } else {
        Row(
            modifier = modifier
                .height(LabeledHeight)
                .clip(MaterialTheme.shapes.medium)
                .background(bg)
                .border(BorderWidth, borderColor, MaterialTheme.shapes.medium)
                .clickable(onClick = onClick)
                .padding(
                    start = LabeledPaddingStart,
                    end   = if (active) LabeledPaddingEndActive else LabeledPaddingEndInactive,
                ),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(LabeledGap),
        ) {
            Icon(iconVector, contentDescription = null, tint = iconTint, modifier = Modifier.size(LabeledIconSize))
            Text(label, color = cs.onSurface, fontSize = LabeledFontSize, fontWeight = FontWeight.W600)
            if (active) {
                FilterBadge(
                    count       = activeCount,
                    height      = BadgeLabeledHeight,
                    paddingH    = BadgeLabeledPaddingH,
                    fontSize    = BadgeLabeledFontSize,
                    borderWidth = 0.dp,
                )
            }
        }
    }
}

@Composable
private fun FilterBadge(
    count: Int,
    height: Dp,
    paddingH: Dp,
    fontSize: TextUnit,
    borderWidth: Dp,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .height(height)
            .defaultMinSize(minWidth = height)
            .clip(ForgeShape.pill)
            .background(cs.primary)
            .then(if (borderWidth > 0.dp) Modifier.border(borderWidth, cs.background, ForgeShape.pill) else Modifier)
            .padding(horizontal = paddingH),
        contentAlignment = Alignment.Center,
    ) {
        Text(count.toString(), color = cs.onPrimary, fontSize = fontSize, fontWeight = FontWeight.W700)
    }
}