package io.github.dumbgreenfish.dialogueforge.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
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
    val iconColor = if (isActive) ForgeColors.spark else MaterialTheme.colorScheme.onSurfaceVariant
    val labelColor = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
    val labelWeight = if (isActive) FontWeight.W600 else FontWeight.W500

    Column(
        modifier = Modifier
            .selectable(
                selected = isActive,
                onClick = onClick,
                role = Role.Tab,
            )
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .width(64.dp)
                .height(32.dp)
                .then(
                    if (isActive) Modifier.background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = ForgeShape.pill,
                    ) else Modifier
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (isActive) item.iconFilled else item.iconOutlined,
                contentDescription = stringResource(item.labelRes),
                modifier = Modifier.size(22.dp),
                tint = iconColor,
            )
        }
        Text(
            text = stringResource(item.labelRes),
            fontSize = 11.5.sp,
            fontWeight = labelWeight,
            color = labelColor,
        )
    }
}
