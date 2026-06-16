package io.github.dumbgreenfish.dialogueforge.ui.characters.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeAnimation
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.characters_create
import io.github.dumbgreenfish.dialogueforge.generated.resources.characters_import
import org.jetbrains.compose.resources.stringResource

private val FabSize                = 56.dp
private val FabCorner              = 16.dp
private val FabOpenRotation        = 45f

private val MiniFabSize            = 40.dp
private val MiniFabCorner          = 12.dp
private val MiniFabIconSize        = 18.dp

private val DialGap                = 12.dp
private val RowGap                 = 10.dp

private val LabelCorner            = 6.dp
private val LabelPaddingH          = 10.dp
private val LabelPaddingV          = 5.dp
private val LabelBorderWidth       = 1.dp
private val LabelShadowElevation   = 4.dp
private val LabelFontSize          = 12.sp
private val LabelLetterSpacing     = 0.2.sp

@Composable
internal fun CharactersSpeedDial(
    expanded: Boolean,
    onToggle: () -> Unit,
    onImport: () -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconRotation by animateFloatAsState(
        targetValue    = if (expanded) FabOpenRotation else 0f,
        animationSpec  = tween(ForgeAnimation.DurationStateTransition),
        label          = "speed-dial-rotation",
    )

    Column(
        modifier              = modifier,
        horizontalAlignment   = Alignment.End,
        verticalArrangement   = Arrangement.spacedBy(DialGap),
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter   = fadeIn(tween(ForgeAnimation.DurationStateTransition)) +
                      slideInVertically(tween(ForgeAnimation.DurationStateTransition)) { it / 2 },
            exit    = fadeOut(tween(ForgeAnimation.DurationStateTransition)) +
                      slideOutVertically(tween(ForgeAnimation.DurationStateTransition)) { it / 2 },
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(DialGap),
                horizontalAlignment = Alignment.End,
            ) {
                SpeedDialRow(
                    label   = stringResource(Res.string.characters_import),
                    icon    = Icons.Filled.Download,
                    onClick = onImport,
                )
                SpeedDialRow(
                    label   = stringResource(Res.string.characters_create),
                    icon    = Icons.Filled.Edit,
                    onClick = onCreateClick,
                )
            }
        }

        FloatingActionButton(
            onClick        = onToggle,
            modifier       = Modifier.size(FabSize),
            shape          = RoundedCornerShape(FabCorner),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor   = MaterialTheme.colorScheme.onPrimary,
        ) {
            Icon(
                imageVector        = Icons.Filled.Add,
                contentDescription = null,
                modifier           = Modifier.rotate(iconRotation),
            )
        }
    }
}

@Composable
private fun SpeedDialRow(label: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(RowGap),
    ) {
        SpeedDialLabel(text = label)
        SmallFloatingActionButton(
            onClick        = onClick,
            modifier       = Modifier.size(MiniFabSize),
            shape          = RoundedCornerShape(MiniFabCorner),
            containerColor = ForgeColors.surfaceContainerHighest,
            contentColor   = MaterialTheme.colorScheme.onSurface,
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = label,
                modifier           = Modifier.size(MiniFabIconSize),
            )
        }
    }
}

@Composable
private fun SpeedDialLabel(text: String) {
    Surface(
        shape           = RoundedCornerShape(LabelCorner),
        color           = ForgeColors.surfaceContainerHighest,
        shadowElevation = LabelShadowElevation,
        modifier        = Modifier.border(
            width = LabelBorderWidth,
            color = MaterialTheme.colorScheme.outline,
            shape = RoundedCornerShape(LabelCorner),
        ),
    ) {
        Text(
            text     = text,
            modifier = Modifier.padding(horizontal = LabelPaddingH, vertical = LabelPaddingV),
            style    = MaterialTheme.typography.labelSmall.copy(
                fontSize      = LabelFontSize,
                fontWeight    = FontWeight.SemiBold,
                letterSpacing = LabelLetterSpacing,
            ),
        )
    }
}
