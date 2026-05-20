package io.github.dumbgreenfish.dialogueforge.ui.navigation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.ui.navigation.animation.rememberBottomNavItemAnimation
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.modifier.navItemSelectable
import org.jetbrains.compose.resources.stringResource

@Composable
fun ForgeBottomNav(
    selected: NavTab,
    onSelect: (NavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outline)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(top = 12.dp, bottom = 16.dp)
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
    val anim = rememberBottomNavItemAnimation(isActive, interactionSource)

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .navItemSelectable(isActive, onClick, interactionSource)
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
    ) {
        Box(
            modifier = Modifier
                .width(64.dp)
                .height(32.dp)
                .scale(anim.pillScale)
                .background(color = anim.pillColor, shape = ForgeShape.pill),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = item.icon(isActive),
                contentDescription = stringResource(item.labelRes),
                modifier = Modifier.size(22.dp),
                tint = anim.iconColor,
            )
        }
        Text(
            text = stringResource(item.labelRes),
            fontSize = 11.5.sp,
            lineHeight = 14.sp,
            fontWeight = if (isActive) FontWeight.W600 else FontWeight.W500,
            color = anim.labelColor,
        )
    }
}
