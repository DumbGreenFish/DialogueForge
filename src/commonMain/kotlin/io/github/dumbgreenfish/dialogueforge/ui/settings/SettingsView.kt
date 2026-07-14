package io.github.dumbgreenfish.dialogueforge.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShapes
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_animation_fast
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_animation_normal
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_animation_off
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_animation_slow
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_animation_speed
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_chat_background
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_chat_background_dim
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_chat_background_opacity
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_chat_background_pick
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_chat_background_remove
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_chat_panel_opacity
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_composer_max_height
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_default_view_mode
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_density_scale
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_font_scale
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_message_compact
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_message_normal
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_message_wide
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_message_width
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_reset
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_section_appearance
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_section_chat
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_section_navigation
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_sidebar_width
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_view_grid
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_view_list
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharactersViewMode
import io.github.dumbgreenfish.dialogueforge.ui.common.rememberFilePicker
import io.github.dumbgreenfish.dialogueforge.ui.common.toImageBitmapOrNull
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.AnimationSpeed
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.MessageWidth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.math.roundToInt

private val ContentPadH             = 16.dp
private val ContentPadTop           = 16.dp
private val ContentPadBottom        = 16.dp
private val SectionGap              = 24.dp
private val SectionHeaderToCardGap  = 8.dp
private val CardBorderWidth         = 1.dp
private val CardItemDividerInset    = 16.dp
private val ExpandedPadH            = 16.dp
private val ExpandedPadTop          = 4.dp
private val ExpandedPadBottom       = 14.dp
private val ExpandedSliderGap       = 8.dp
private val ExpandedSegmentedGap    = 12.dp
private val ChevronSize             = 20.dp
private val ChevronGap              = 4.dp
private val FooterButtonGap         = 32.dp
private val ResetButtonHeight       = 48.dp

private const val DensityMin        = 0.7f
private const val DensityMax        = 3.0f
private const val FontScaleMin      = 0.75f
private const val FontScaleMax      = 2.0f
private const val ComposerHeightMin = 80f
private const val ComposerHeightMax = 300f
private const val ComposerStep      = 20f
private const val SidebarMin        = 200f
private const val SidebarMax        = 320f
private const val SidebarStep       = 10f
private const val PanelOpacityMin   = 0.3f
private const val PanelOpacityMax   = 1.0f
private const val BgDimMin          = 0f
private const val BgDimMax          = 0.8f

@Composable
@OptIn(KoinExperimentalAPI::class)
fun SettingsView(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<SettingsViewModel>()
    val state by viewModel.state.collectAsState()
    var expandedId by remember { mutableStateOf<String?>(null) }

    fun toggle(id: String) {
        expandedId = if (expandedId == id) null else id
    }

    LaunchedEffect(Unit) {
        viewModel.handle(SettingsIntent.Load)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ContentPadH),
    ) {
        SectionHeader(
            title = stringResource(Res.string.settings_section_appearance),
            modifier = Modifier.padding(top = ContentPadTop),
        )
        Spacer(Modifier.height(SectionHeaderToCardGap))

        SettingsCard {
            ExpandableSliderSetting(
                headline = stringResource(Res.string.settings_density_scale),
                value = state.densityScale,
                onValueChange = { viewModel.handle(SettingsIntent.UpdateDensityScale(it)) },
                valueRange = DensityMin..DensityMax,
                valueSuffix = "x",
                isExpanded = expandedId == "density",
                onToggle = { toggle("density") },
            )
            SettingsDivider()
            ExpandableSliderSetting(
                headline = stringResource(Res.string.settings_font_scale),
                value = state.fontScale,
                onValueChange = { viewModel.handle(SettingsIntent.UpdateFontScale(it)) },
                valueRange = FontScaleMin..FontScaleMax,
                valueSuffix = "x",
                isExpanded = expandedId == "font_scale",
                onToggle = { toggle("font_scale") },
            )
            SettingsDivider()
            ExpandableEnumSetting(
                headline = stringResource(Res.string.settings_animation_speed),
                options = AnimationSpeed.entries,
                selected = state.animationSpeed,
                onSelect = {
                    viewModel.handle(SettingsIntent.UpdateAnimationSpeed(it))
                    toggle("animation")
                },
                label = { animationSpeedLabel(it) },
                isExpanded = expandedId == "animation",
                onToggle = { toggle("animation") },
            )
            SettingsDivider()
            ExpandableEnumSetting(
                headline = stringResource(Res.string.settings_default_view_mode),
                options = CharactersViewMode.entries,
                selected = state.defaultViewMode,
                onSelect = {
                    viewModel.handle(SettingsIntent.UpdateDefaultViewMode(it))
                    toggle("view_mode")
                },
                label = { viewModeLabel(it) },
                isExpanded = expandedId == "view_mode",
                onToggle = { toggle("view_mode") },
            )
        }

        SectionHeader(
            title = stringResource(Res.string.settings_section_chat),
            modifier = Modifier.padding(top = SectionGap),
        )
        Spacer(Modifier.height(SectionHeaderToCardGap))

        SettingsCard {
            ExpandableEnumSetting(
                headline = stringResource(Res.string.settings_message_width),
                options = MessageWidth.entries,
                selected = state.messageWidth,
                onSelect = {
                    viewModel.handle(SettingsIntent.UpdateMessageWidth(it))
                    toggle("msg_width")
                },
                label = { messageWidthLabel(it) },
                isExpanded = expandedId == "msg_width",
                onToggle = { toggle("msg_width") },
            )
            SettingsDivider()
            ExpandableSliderSetting(
                headline = stringResource(Res.string.settings_composer_max_height),
                value = state.composerMaxHeightDp.toFloat(),
                onValueChange = {
                    val stepped = roundToStep(it, ComposerStep)
                    viewModel.handle(SettingsIntent.UpdateComposerMaxHeight(stepped.toInt()))
                },
                valueRange = ComposerHeightMin..ComposerHeightMax,
                valueSuffix = " dp",
                isExpanded = expandedId == "composer",
                onToggle = { toggle("composer") },
            )
            SettingsDivider()
            ExpandableSliderSetting(
                headline = stringResource(Res.string.settings_chat_panel_opacity),
                value = state.chatPanelOpacity,
                onValueChange = { viewModel.handle(SettingsIntent.UpdateChatPanelOpacity(it)) },
                valueRange = PanelOpacityMin..PanelOpacityMax,
                valueSuffix = "",
                isExpanded = expandedId == "panel_opacity",
                onToggle = { toggle("panel_opacity") },
            )
            SettingsDivider()
            ExpandableSliderSetting(
                headline = stringResource(Res.string.settings_chat_background_dim),
                value = state.chatBackgroundDim,
                onValueChange = { viewModel.handle(SettingsIntent.UpdateChatBackgroundDim(it)) },
                valueRange = BgDimMin..BgDimMax,
                valueSuffix = "",
                isExpanded = expandedId == "bg_dim",
                onToggle = { toggle("bg_dim") },
            )
        }

        Spacer(Modifier.height(SectionHeaderToCardGap))

        ChatBackgroundSetting(
            bytes = state.chatBackgroundBytes,
            opacity = state.chatBackgroundOpacity,
            onPick = { bytes -> viewModel.handle(SettingsIntent.SetChatBackground(bytes)) },
            onRemove = { viewModel.handle(SettingsIntent.RemoveChatBackground) },
            onOpacityChange = { viewModel.handle(SettingsIntent.UpdateChatBackgroundOpacity(it)) },
        )

        SectionHeader(
            title = stringResource(Res.string.settings_section_navigation),
            modifier = Modifier.padding(top = SectionGap),
        )
        Spacer(Modifier.height(SectionHeaderToCardGap))

        SettingsCard {
            ExpandableSliderSetting(
                headline = stringResource(Res.string.settings_sidebar_width),
                value = state.sidebarWidthDp.toFloat(),
                onValueChange = {
                    val stepped = roundToStep(it, SidebarStep)
                    viewModel.handle(SettingsIntent.UpdateSidebarWidth(stepped.toInt()))
                },
                valueRange = SidebarMin..SidebarMax,
                valueSuffix = " dp",
                isExpanded = expandedId == "sidebar",
                onToggle = { toggle("sidebar") },
            )
        }

        Spacer(Modifier.height(FooterButtonGap))

        OutlinedButton(
            onClick = { viewModel.handle(SettingsIntent.Reset) },
            modifier = Modifier.fillMaxWidth().height(ResetButtonHeight),
        ) {
            Text(stringResource(Res.string.settings_reset))
        }

        Spacer(Modifier.height(ContentPadBottom))
    }
}

@Composable
private fun ChatBackgroundSetting(
    bytes: ByteArray?,
    opacity: Float,
    onPick: (ByteArray) -> Unit,
    onRemove: () -> Unit,
    onOpacityChange: (Float) -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    var previewBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val picker = rememberFilePicker { data, _ -> onPick(data) }

    LaunchedEffect(bytes) {
        previewBitmap = if (bytes != null) {
            withContext(Dispatchers.Default) { bytes.toImageBitmapOrNull() }
        } else null
    }

    val hasBackground = bytes != null

    SettingsCard {
        SettingRow(
            headline = stringResource(Res.string.settings_chat_background),
            trailing = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (hasBackground) {
                        previewBitmap?.let { bmp ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(cs.surfaceVariant, CircleShape),
                            ) {
                                Image(
                                    bitmap = bmp,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        }
                        TextButton(onClick = onRemove) {
                            Text(
                                stringResource(Res.string.settings_chat_background_remove),
                                color = cs.error,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                    OutlinedButton(onClick = picker) {
                        Text(
                            if (hasBackground) "..." else stringResource(Res.string.settings_chat_background_pick),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            },
            onClick = {},
        )
    }

    if (hasBackground) {
        SettingsDivider()
        ExpandableSliderSetting(
            headline = stringResource(Res.string.settings_chat_background_opacity),
            value = opacity,
            onValueChange = onOpacityChange,
            valueRange = 0.05f..0.40f,
            valueSuffix = "",
            isExpanded = false,
            onToggle = {},
        )
    }
}

@Composable
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = ForgeColors.spark,
        modifier = modifier.padding(horizontal = 4.dp),
    )
}

@Composable
private fun SettingsCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = ForgeShapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        border = BorderStroke(CardBorderWidth, MaterialTheme.colorScheme.outline),
    ) {
        Column(content = content)
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = CardItemDividerInset),
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}

@Composable
private fun ChevronIcon(isExpanded: Boolean) {
    Icon(
        imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
        contentDescription = null,
        modifier = Modifier.size(ChevronSize),
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun ExpandableSliderSetting(
    headline: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    valueSuffix: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
) {
    val cs = MaterialTheme.colorScheme

    Column {
        SettingRow(
            headline = headline,
            trailing = { Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatSliderValue(value, valueSuffix),
                    style = MaterialTheme.typography.bodyMedium,
                    color = cs.onSurfaceVariant,
                    textAlign = TextAlign.End,
                )
                Spacer(Modifier.size(ChevronGap))
                ChevronIcon(isExpanded)
            }},
            onClick = onToggle,
        )
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Column(
                modifier = Modifier.padding(
                    start = ExpandedPadH,
                    end = ExpandedPadH,
                    top = ExpandedPadTop,
                    bottom = ExpandedPadBottom,
                ),
            ) {
                Slider(
                    value = value,
                    onValueChange = onValueChange,
                    valueRange = valueRange,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = ForgeColors.spark,
                        activeTrackColor = ForgeColors.spark,
                        inactiveTrackColor = ForgeColors.copperDim,
                    ),
                )
                Spacer(Modifier.height(ExpandedSliderGap))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = formatSliderValue(valueRange.start, valueSuffix),
                        style = MaterialTheme.typography.labelSmall,
                        color = cs.onSurfaceVariant,
                    )
                    Text(
                        text = formatSliderValue(valueRange.endInclusive, valueSuffix),
                        style = MaterialTheme.typography.labelSmall,
                        color = cs.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> ExpandableEnumSetting(
    headline: String,
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: @Composable (T) -> Unit,
    isExpanded: Boolean,
    onToggle: () -> Unit,
) {
    val cs = MaterialTheme.colorScheme

    Column {
        SettingRow(
            headline = headline,
            trailing = { Row(verticalAlignment = Alignment.CenterVertically) {
                ProvideTextStyle(MaterialTheme.typography.bodyMedium.copy(color = cs.onSurfaceVariant)) {
                    label(selected)
                }
                Spacer(Modifier.size(ChevronGap))
                ChevronIcon(isExpanded)
            }},
            onClick = onToggle,
        )
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Column(
                modifier = Modifier.padding(
                    start = ExpandedPadH,
                    end = ExpandedPadH,
                    top = ExpandedSegmentedGap,
                    bottom = ExpandedPadBottom,
                ),
            ) {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    options.forEachIndexed { index, option ->
                        SegmentedButton(
                            selected = option == selected,
                            onClick = { onSelect(option) },
                            shape = SegmentedButtonDefaults.itemShape(index, options.size),
                        ) {
                            ProvideTextStyle(MaterialTheme.typography.labelLarge) {
                                label(option)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingRow(
    headline: String,
    trailing: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    val cs = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = ExpandedPadH, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = headline,
            style = MaterialTheme.typography.bodyLarge,
            color = cs.onSurface,
            modifier = Modifier.weight(1f),
        )
        trailing()
    }
}

private fun formatSliderValue(value: Float, suffix: String): String {
    val display = (value * 100).roundToInt() / 100f
    val intPart = display.toInt()
    val fracPart = ((display - intPart) * 100).roundToInt()
    return if (fracPart == 0) "$intPart$suffix"
    else "$intPart.${fracPart.toString().padStart(2, '0')}$suffix"
}

private fun roundToStep(value: Float, step: Float): Float =
    (value / step).roundToInt() * step

@Composable
private fun animationSpeedLabel(speed: AnimationSpeed): String = when (speed) {
    AnimationSpeed.Fast -> stringResource(Res.string.settings_animation_fast)
    AnimationSpeed.Normal -> stringResource(Res.string.settings_animation_normal)
    AnimationSpeed.Slow -> stringResource(Res.string.settings_animation_slow)
    AnimationSpeed.Off -> stringResource(Res.string.settings_animation_off)
}

@Composable
private fun viewModeLabel(mode: CharactersViewMode): String = when (mode) {
    CharactersViewMode.List -> stringResource(Res.string.settings_view_list)
    CharactersViewMode.Grid -> stringResource(Res.string.settings_view_grid)
}

@Composable
private fun messageWidthLabel(width: MessageWidth): String = when (width) {
    MessageWidth.Compact -> stringResource(Res.string.settings_message_compact)
    MessageWidth.Normal -> stringResource(Res.string.settings_message_normal)
    MessageWidth.Wide -> stringResource(Res.string.settings_message_wide)
}
