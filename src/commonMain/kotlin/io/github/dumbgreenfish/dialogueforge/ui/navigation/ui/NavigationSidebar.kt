package io.github.dumbgreenfish.dialogueforge.ui.navigation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.BuildConfig
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.design.LabelSmallText
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.app_name
import io.github.dumbgreenfish.dialogueforge.generated.resources.sidebar_active_model_label
import io.github.dumbgreenfish.dialogueforge.generated.resources.sidebar_model_placeholder
import io.github.dumbgreenfish.dialogueforge.ui.ForgeMark
import org.jetbrains.compose.resources.stringResource

@Composable
fun NavigationSidebar(
    selected: NavTab,
    onSelect: (NavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(260.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Row(
            modifier = Modifier.padding(start = 20.dp, top = 18.dp, end = 20.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ForgeMark(Modifier.size(26.dp))
            Column {
                Text(
                    text = stringResource(Res.string.app_name),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "v${BuildConfig.VERSION}",
                    style = MaterialTheme.typography.labelSmall,
                    color = ForgeColors.onSurfaceFaint,
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .selectableGroup(),
        ) {
            navItems.forEach { item ->
                SidebarNavItem(
                    item = item,
                    isActive = item.tab == selected,
                    onClick = { onSelect(item.tab) },
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .padding(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 14.dp)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(ForgeColors.surfaceContainerHigh)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                Modifier
                    .size(8.dp)
                    .background(ForgeColors.spark, shape = ForgeShape.pill)
            )
            Column(Modifier.weight(1f)) {
                LabelSmallText(
                    text = stringResource(Res.string.sidebar_active_model_label),
                    color = ForgeColors.onSurfaceFaint,
                )
                Text(
                    text = stringResource(Res.string.sidebar_model_placeholder),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Icon(
                imageVector = Icons.Outlined.Tune,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
