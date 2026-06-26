package io.github.dumbgreenfish.dialogueforge.ui.characters.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.filter_all
import io.github.dumbgreenfish.dialogueforge.generated.resources.filter_exclude_placeholder
import io.github.dumbgreenfish.dialogueforge.generated.resources.filter_imported
import io.github.dumbgreenfish.dialogueforge.generated.resources.filter_include_placeholder
import io.github.dumbgreenfish.dialogueforge.generated.resources.filter_made
import io.github.dumbgreenfish.dialogueforge.generated.resources.filter_pinned
import io.github.dumbgreenfish.dialogueforge.generated.resources.filter_reset
import io.github.dumbgreenfish.dialogueforge.generated.resources.filter_section_exclude
import io.github.dumbgreenfish.dialogueforge.generated.resources.filter_section_include
import io.github.dumbgreenfish.dialogueforge.generated.resources.filter_section_show
import io.github.dumbgreenfish.dialogueforge.generated.resources.filter_title
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersIntent
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharacterFilter
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharacterQuickFilter
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Tag
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private val HeaderPaddingH      = 14.dp
private val HeaderPaddingTop    = 14.dp
private val HeaderPaddingBottom = 10.dp
private val TitleFontSize       = 15.sp
private val ResetFontSize       = 13.sp
private val ResetPadding        = 4.dp
private val BodyPadding         = 14.dp
private val SectionGap          = 18.dp
private val SectionLabelFontSize      = 11.sp
private val SectionLabelLetterSpacing = 0.8.sp
private val SectionLabelGap     = 10.dp
private val PillGap             = 7.dp
private val PillsBottomGap      = 10.dp
private val QuickPillHeight       = 34.dp
private val QuickPillPaddingStart = 10.dp
private val QuickPillPaddingEnd   = 13.dp
private val QuickPillGap          = 6.dp
private val QuickPillIconSize     = 15.dp
private val QuickPillFontSize     = 13.5.sp
private val BorderWidth         = 1.dp
private val BodyMaxHeight        = 460.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FilterPanel(
    filter: CharacterFilter,
    availableTags: List<Tag>,
    onIntent: (CharactersIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cs.surfaceVariant)
                .padding(start = HeaderPaddingH, end = HeaderPaddingH, top = HeaderPaddingTop, bottom = HeaderPaddingBottom),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(stringResource(Res.string.filter_title), color = cs.onSurface, fontSize = TitleFontSize, fontWeight = FontWeight.W700, modifier = Modifier.weight(1f))
            Text(
                stringResource(Res.string.filter_reset),
                color      = ForgeColors.copperSoft,
                fontSize   = ResetFontSize,
                fontWeight = FontWeight.W600,
                modifier   = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onIntent(CharactersIntent.FiltersReset) }
                    .padding(ResetPadding),
            )
        }
        HorizontalDivider(color = cs.outline)
        Column(
            modifier = Modifier
                .heightIn(max = BodyMaxHeight)
                .verticalScroll(rememberScrollState())
                .padding(BodyPadding),
            verticalArrangement = Arrangement.spacedBy(SectionGap),
        ) {
            FilterSection(stringResource(Res.string.filter_section_show)) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(PillGap), verticalArrangement = Arrangement.spacedBy(PillGap)) {
                    QuickFilterPill(CharacterQuickFilter.All,      filter.quick, enabled = true,  onIntent = onIntent)
                    QuickFilterPill(CharacterQuickFilter.Pinned,   filter.quick, enabled = true,  onIntent = onIntent)
                    // TODO(origin): enable when character origin will be added
                    QuickFilterPill(CharacterQuickFilter.Made,     filter.quick, enabled = false, onIntent = onIntent)
                    QuickFilterPill(CharacterQuickFilter.Imported, filter.quick, enabled = false, onIntent = onIntent)
                }
            }
            FilterSection(stringResource(Res.string.filter_section_include)) {
                if (filter.includeTags.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.padding(bottom = PillsBottomGap),
                        horizontalArrangement = Arrangement.spacedBy(PillGap),
                        verticalArrangement   = Arrangement.spacedBy(PillGap),
                    ) {
                        filter.includeTags.forEach { tag ->
                            FilterTagPill(tag.value, ForgeColors.spark, onRemove = { onIntent(CharactersIntent.IncludeTagRemoved(tag)) })                        }
                    }
                }
                FilterTagInput(
                    placeholder = stringResource(Res.string.filter_include_placeholder),
                    leadingIcon = Icons.Filled.Add,
                    accent      = ForgeColors.spark,
                    chosen      = filter.includeTags,
                    otherChosen = filter.excludeTags,
                    allTags     = availableTags,
                    onAdd       = { onIntent(CharactersIntent.IncludeTagAdded(Tag(it))) },
                )
            }
            FilterSection(stringResource(Res.string.filter_section_exclude)) {
                if (filter.excludeTags.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.padding(bottom = PillsBottomGap),
                        horizontalArrangement = Arrangement.spacedBy(PillGap),
                        verticalArrangement   = Arrangement.spacedBy(PillGap),
                    ) {
                        filter.excludeTags.forEach { tag ->
                            FilterTagPill(tag.value, cs.error, onRemove = { onIntent(CharactersIntent.ExcludeTagRemoved(tag)) })                        }
                    }
                }
                FilterTagInput(
                    placeholder = stringResource(Res.string.filter_exclude_placeholder),
                    leadingIcon = Icons.Filled.Block,
                    accent      = cs.error,
                    chosen      = filter.excludeTags,
                    otherChosen = filter.includeTags,
                    allTags     = availableTags,
                    onAdd       = { onIntent(CharactersIntent.ExcludeTagAdded(Tag(it))) },
                )
            }
        }
    }
}

@Composable
private fun FilterSection(label: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            label.uppercase(),
            color         = ForgeColors.onSurfaceFaint,
            fontSize      = SectionLabelFontSize,
            fontWeight    = FontWeight.W700,
            letterSpacing = SectionLabelLetterSpacing,
            modifier      = Modifier.padding(bottom = SectionLabelGap),
        )
        content()
    }
}

@Composable
private fun QuickFilterPill(
    quick: CharacterQuickFilter,
    current: CharacterQuickFilter,
    enabled: Boolean,
    onIntent: (CharactersIntent) -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val on = current == quick
    val contentColor = when {
        !enabled -> ForgeColors.onSurfaceMute
        on       -> cs.onPrimaryContainer
        else     -> cs.onSurface
    }
    val iconTint = when {
        !enabled -> ForgeColors.onSurfaceMute
        on       -> ForgeColors.spark
        else     -> cs.onSurfaceVariant
    }
    val borderColor = when {
        !enabled -> cs.outline
        on       -> cs.primary
        else     -> cs.outlineVariant
    }
    val bg = if (on) cs.primaryContainer else Color.Transparent
    Row(
        modifier = Modifier
            .height(QuickPillHeight)
            .clip(ForgeShape.pill)
            .background(bg)
            .border(BorderWidth, borderColor, ForgeShape.pill)
            .then(if (enabled) Modifier.clickable { onIntent(CharactersIntent.QuickFilterChanged(quick)) } else Modifier)
            .padding(start = QuickPillPaddingStart, end = QuickPillPaddingEnd),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(QuickPillGap),
    ) {
        Icon(quickIcon(quick, on), contentDescription = null, tint = iconTint, modifier = Modifier.size(QuickPillIconSize))
        Text(stringResource(quickLabel(quick)), color = contentColor, fontSize = QuickPillFontSize, fontWeight = if (on) FontWeight.W600 else FontWeight.W500)
    }
}

private fun quickLabel(quick: CharacterQuickFilter): StringResource = when (quick) {
    CharacterQuickFilter.All      -> Res.string.filter_all
    CharacterQuickFilter.Pinned   -> Res.string.filter_pinned
    CharacterQuickFilter.Made     -> Res.string.filter_made
    CharacterQuickFilter.Imported -> Res.string.filter_imported
}

private fun quickIcon(quick: CharacterQuickFilter, active: Boolean): ImageVector = when (quick) {
    CharacterQuickFilter.All      -> if (active) Icons.Filled.Apps else Icons.Outlined.Apps
    CharacterQuickFilter.Pinned   -> if (active) Icons.Filled.PushPin else Icons.Outlined.PushPin
    CharacterQuickFilter.Made     -> Icons.Outlined.Edit
    CharacterQuickFilter.Imported -> Icons.Outlined.FileDownload
}