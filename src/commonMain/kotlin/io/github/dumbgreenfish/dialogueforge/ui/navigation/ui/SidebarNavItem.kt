package io.github.dumbgreenfish.dialogueforge.ui.navigation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.ui.navigation.animation.rememberSidebarNavItemAnimation
import org.jetbrains.compose.resources.stringResource

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
            .padding(bottom = 2.dp)
            .scale(anim.rowScale)
            .clip(MaterialTheme.shapes.medium)
            .background(anim.backgroundColor)
            .selectable(
                selected = isActive,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null,
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = if (isActive) item.iconFilled else item.iconOutlined,
            contentDescription = null,
            tint = anim.iconColor,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = stringResource(item.labelRes),
            fontSize = 13.5.sp,
            fontWeight = if (isActive) FontWeight.W600 else FontWeight.W500,
            color = anim.textColor,
            modifier = Modifier.weight(1f),
        )
    }
}
