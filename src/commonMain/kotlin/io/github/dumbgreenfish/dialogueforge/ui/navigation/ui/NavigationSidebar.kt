package io.github.dumbgreenfish.dialogueforge.ui.navigation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.BuildConfig
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.ForgeSettings
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.ModelNameProvider
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.design.LabelSmallText
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.app_name
import io.github.dumbgreenfish.dialogueforge.generated.resources.sidebar_active_model_label
import io.github.dumbgreenfish.dialogueforge.generated.resources.sidebar_model_placeholder
import io.github.dumbgreenfish.dialogueforge.ui.common.ForgeMark
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

private val BrandPaddingH         = 18.dp
private val BrandPaddingTop       = 20.dp
private val BrandPaddingBottom    = 16.dp
private val BrandMarkSize         = 26.dp
private val BrandGap              = 10.dp
private val NavPaddingH           = 12.dp
private val NavPaddingTop         = 6.dp
private val FooterPaddingH        = 16.dp
private val FooterPaddingTop      = 14.dp
private val FooterPaddingBottom   = 16.dp
private val FooterInnerPadding    = 14.dp
private val FooterDotSize         = 8.dp
private val FooterGap             = 10.dp
private val FooterIconSize        = 16.dp

@Composable
fun NavigationSidebar(
    selected: NavTab,
    onSelect: (NavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val modelNameProvider = koinInject<ModelNameProvider>()
    val forgeSettings = koinInject<ForgeSettings>()
    val modelName by modelNameProvider.modelName.collectAsState()
    val sidebarWidthDp by forgeSettings.sidebarWidthDp.collectAsState()

    Column(
        modifier = modifier
            .width(sidebarWidthDp.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Row(
            modifier = Modifier.padding(start = BrandPaddingH, top = BrandPaddingTop, end = BrandPaddingH, bottom = BrandPaddingBottom),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(BrandGap),
        ) {
            ForgeMark(Modifier.size(BrandMarkSize))
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
                .padding(start = NavPaddingH, end = NavPaddingH, top = NavPaddingTop)
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
                .padding(start = FooterPaddingH, end = FooterPaddingH, top = FooterPaddingTop, bottom = FooterPaddingBottom)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(ForgeColors.surfaceContainerHigh)
                .clickable { onSelect(NavTab.Presets) }
                .padding(FooterInnerPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FooterGap),
        ) {
            Box(
                Modifier
                    .size(FooterDotSize)
                    .background(ForgeColors.spark, shape = ForgeShape.pill)
            )
            Column(Modifier.weight(1f)) {
                LabelSmallText(
                    text = stringResource(Res.string.sidebar_active_model_label),
                    color = ForgeColors.onSurfaceFaint,
                )
                Text(
                    text = modelName.ifBlank { stringResource(Res.string.sidebar_model_placeholder) },
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
                modifier = Modifier.size(FooterIconSize),
            )
        }
    }
}
