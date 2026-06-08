package io.github.dumbgreenfish.dialogueforge.ui.navigation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.ui.navigation.animation.rememberSidebarNavItemAnimation
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.modifier.navItemSelectable
import org.jetbrains.compose.resources.stringResource

private val ItemPaddingH    = 14.dp
private val ItemPaddingV    = 10.dp
private val ItemBottomGap   = 3.dp
private val ItemIconSize    = 20.dp
private val ItemIconTextGap = 12.dp

@Composable
internal fun SidebarNavItem(
    item: NavItemDef,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val anim = rememberSidebarNavItemAnimation(isActive, interactionSource)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = ItemBottomGap)
            .scale(anim.rowScale)
            .clip(MaterialTheme.shapes.medium)
            .background(anim.backgroundColor)
            .navItemSelectable(isActive, onClick, interactionSource)
            .padding(horizontal = ItemPaddingH, vertical = ItemPaddingV),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ItemIconTextGap),
    ) {
        Icon(
            imageVector = item.icon(isActive),
            contentDescription = null,
            tint = anim.iconColor,
            modifier = Modifier.size(ItemIconSize),
        )
        Text(
            text = stringResource(item.labelRes),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isActive) FontWeight.W600 else FontWeight.W500,
            color = anim.textColor,
            modifier = Modifier.weight(1f),
        )
    }
}
