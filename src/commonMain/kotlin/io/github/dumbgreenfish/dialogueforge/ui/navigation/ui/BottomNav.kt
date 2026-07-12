package io.github.dumbgreenfish.dialogueforge.ui.navigation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeAnimation
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.ui.navigation.animation.rememberNavItemAnimation
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.modifier.navItemSelectable
import org.jetbrains.compose.resources.stringResource

private val DividerHeight     = 1.dp
private val BarHeight         = 80.dp
private val BarPaddingTop     = 12.dp
private val BarPaddingBottom  = 16.dp
private val ItemPaddingH      = 12.dp
private val ItemGap           = 4.dp
private val PillWidth         = 64.dp
private val PillHeight        = 32.dp
private val IconSize          = 22.dp
private val LabelFontSize     = 11.5.sp
private val LabelLineHeight   = 14.sp

@Composable
fun ForgeBottomNav(
    selected: NavTab,
    onSelect: (NavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(cs.background)
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(DividerHeight)
                .background(cs.outline)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(BarHeight)
                .padding(top = BarPaddingTop, bottom = BarPaddingBottom)
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top,
        ) {
            navItems.forEach { item ->
                NavItemColumn(
                    item = item,
                    isActive = item.tab == selected,
                    onClick = { onSelect(item.tab) },
                )
            }
        }
    }
}

@Composable
private fun NavItemColumn(
    item: NavItemDef,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val anim = rememberNavItemAnimation(
        isActive = isActive,
        interactionSource = interactionSource,
        pressScale = ForgeAnimation.PressScaleEmphasized,
    ) { active, stateAlpha, colors ->
        if (active || stateAlpha > 0f) colors.onSurface else colors.onSurfaceVariant
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .navItemSelectable(isActive, onClick, interactionSource)
            .padding(horizontal = ItemPaddingH),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ItemGap, Alignment.Top),
    ) {
        Box(
            modifier = Modifier
                .width(PillWidth)
                .height(PillHeight)
                .scale(anim.scale)
                .background(color = anim.backgroundColor, shape = ForgeShape.pill),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = item.icon(isActive),
                contentDescription = stringResource(item.labelRes),
                modifier = Modifier.size(IconSize),
                tint = anim.iconColor,
            )
        }
        Text(
            text = stringResource(item.labelRes),
            fontSize = LabelFontSize,
            lineHeight = LabelLineHeight,
            fontWeight = if (isActive) FontWeight.W600 else FontWeight.W500,
            color = anim.labelColor,
        )
    }
}
