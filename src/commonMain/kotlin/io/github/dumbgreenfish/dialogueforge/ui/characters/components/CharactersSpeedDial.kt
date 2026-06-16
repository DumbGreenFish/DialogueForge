package io.github.dumbgreenfish.dialogueforge.ui.characters.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.characters_create
import io.github.dumbgreenfish.dialogueforge.generated.resources.characters_import
import org.jetbrains.compose.resources.stringResource

private val FabSize             = 56.dp
private val FabCorner           = 16.dp

private val MenuCorner          = 24.dp
private val MenuMinWidth        = 200.dp
private val MenuPadding         = 8.dp
private val MenuRowGap          = 4.dp
private val MenuShadowElevation = 10.dp
private val MenuBorderWidth     = 1.dp

private val RowHeight           = 52.dp
private val RowCorner           = 16.dp
private val RowPaddingH         = 16.dp
private val RowIconGap          = 14.dp
private val RowIconSize         = 22.dp
private val RowFontSize         = 16.sp

private val MorphDuration       = 240
private val ContentFadeDuration = 120
private val ContentFadeDelay    = 80
private val MorphEasing         = CubicBezierEasing(0.2f, 0f, 0f, 1f)

@Composable
internal fun CharactersSpeedDial(
    expanded: Boolean,
    onToggle: () -> Unit,
    onImport: () -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme

    val cornerRadius by animateDpAsState(
        targetValue   = if (expanded) MenuCorner else FabCorner,
        animationSpec = tween(MorphDuration, easing = MorphEasing),
        label         = "morph-corner",
    )
    val containerColor by androidx.compose.animation.animateColorAsState(
        targetValue   = if (expanded) ForgeColors.surfaceContainerHighest else cs.primary,
        animationSpec = tween(MorphDuration, easing = MorphEasing),
        label         = "morph-bg",
    )
    val borderColor by androidx.compose.animation.animateColorAsState(
        targetValue   = if (expanded) cs.outline else cs.outline.copy(alpha = 0f),
        animationSpec = tween(MorphDuration, easing = MorphEasing),
        label         = "morph-border",
    )

    val shape = RoundedCornerShape(cornerRadius)

    Surface(
        shape           = shape,
        color           = containerColor,
        shadowElevation = MenuShadowElevation,
        modifier        = modifier
            .border(MenuBorderWidth, borderColor, shape),
    ) {
        AnimatedContent(
            targetState      = expanded,
            contentAlignment = Alignment.BottomEnd,
            transitionSpec   = {
                val enter = fadeIn(tween(ContentFadeDuration, delayMillis = if (targetState) ContentFadeDelay else 0))
                val exit  = fadeOut(tween(ContentFadeDuration))
                (enter togetherWith exit) using SizeTransform(clip = true) { _, _ ->
                    tween(MorphDuration, easing = MorphEasing)
                }
            },
            label = "morph-content",
        ) { isExpanded ->
            if (isExpanded) {
                MenuContent(
                    onImport      = onImport,
                    onCreateClick = onCreateClick,
                )
            } else {
                Box(
                    modifier         = Modifier.size(FabSize).clickable { onToggle() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector        = Icons.Filled.Add,
                        contentDescription = null,
                        tint               = cs.onPrimary,
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuContent(onImport: () -> Unit, onCreateClick: () -> Unit) {
    Column(
        modifier            = Modifier.padding(MenuPadding).widthIn(min = MenuMinWidth).width(IntrinsicSize.Min),
        verticalArrangement = Arrangement.spacedBy(MenuRowGap),
    ) {
        MenuRow(
            label   = stringResource(Res.string.characters_import),
            icon    = Icons.Filled.Download,
            primary = false,
            onClick = onImport,
        )
        MenuRow(
            label   = stringResource(Res.string.characters_create),
            icon    = Icons.Filled.Add,
            primary = true,
            onClick = onCreateClick,
        )
    }
}

@Composable
private fun MenuRow(label: String, icon: ImageVector, primary: Boolean, onClick: () -> Unit) {
    val cs             = MaterialTheme.colorScheme
    val containerColor = if (primary) cs.primary else Color.Transparent
    val contentColor   = if (primary) cs.onPrimary else cs.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(RowHeight)
            .clip(RoundedCornerShape(RowCorner))
            .background(containerColor)
            .clickable { onClick() }
            .padding(horizontal = RowPaddingH),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(RowIconGap),
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            modifier           = Modifier.size(RowIconSize),
            tint               = contentColor,
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize   = RowFontSize,
                fontWeight = if (primary) FontWeight.Bold else FontWeight.SemiBold,
                color      = contentColor,
            ),
        )
    }
}
