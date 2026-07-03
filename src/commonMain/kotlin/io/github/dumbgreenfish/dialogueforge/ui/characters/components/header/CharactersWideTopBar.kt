package io.github.dumbgreenfish.dialogueforge.ui.characters.components.header

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.characters_total
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersIntent
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersViewModel
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.filter.FilterPanel
import io.github.dumbgreenfish.dialogueforge.ui.common.ForgeMark
import io.github.dumbgreenfish.dialogueforge.ui.common.rememberFilePicker
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.navItems
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private val TopBarHeight   = 64.dp
private val TopBarPaddingH = 4.dp
private val GapItems            = 2.dp
private val ViewTogglePad       = 12.dp
private val TitleSeparatorPad   = 4.dp
private val FilterPopupGap      = 8.dp
private val FilterPopupWidth    = 360.dp
private val FilterPopupMaxHeight = 460.dp
private val FilterPopupBorder   = 1.dp
private val FilterPopupShadow   = 12.dp

@Composable
internal fun CharactersWideTopBar() {
    val viewModel = koinViewModel<CharactersViewModel>()
    val state by viewModel.state.collectAsState()
    var searchExpanded by remember { mutableStateOf(false) }
    var filterOpen by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val cs = MaterialTheme.colorScheme
    val launchPicker = rememberFilePicker { bytes, filename ->
        viewModel.handle(CharactersIntent.ImportFile(bytes, filename))
    }
    val item = navItems.first { it.tab == NavTab.Characters }

    Column(Modifier.fillMaxWidth()) {
        AnimatedContent(
            targetState = searchExpanded,
            transitionSpec = {
                if (targetState) {
                    (fadeIn() + slideInHorizontally { it / 4 }).togetherWith(
                        fadeOut() + slideOutHorizontally { -it / 4 }
                    ).using(SizeTransform(clip = false))
                } else {
                    (fadeIn() + slideInHorizontally { -it / 4 }).togetherWith(
                        fadeOut() + slideOutHorizontally { it / 4 }
                    ).using(SizeTransform(clip = false))
                }
            },
        ) { expanded ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TopBarHeight)
                    .padding(horizontal = TopBarPaddingH),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (expanded) {
                    SearchField(
                        value = state.query,
                        onChange = { viewModel.handle(CharactersIntent.SearchChanged(it)) },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                    )
                    Spacer(Modifier.width(GapItems))
                    IconButton(onClick = {
                        searchExpanded = false
                        viewModel.handle(CharactersIntent.SearchChanged(""))
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = null,
                            tint = cs.onSurfaceVariant,
                        )
                    }
                } else {
                    Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                        ForgeMark(Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = stringResource(item.labelRes),
                        style = MaterialTheme.typography.titleMedium,
                        color = cs.onSurface,
                    )
                    Spacer(Modifier.width(TitleSeparatorPad))
                    Text(
                        text = "|",
                        style = MaterialTheme.typography.labelMedium,
                        color = cs.onSurfaceVariant,
                    )
                    Spacer(Modifier.width(TitleSeparatorPad))
                    Text(
                        text = stringResource(Res.string.characters_total, state.displayed.size),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.W500),
                        color = cs.onSurfaceVariant,
                    )
                    Spacer(Modifier.weight(1f))
                    ViewToggle(
                        mode = state.viewMode,
                        onToggle = { viewModel.handle(CharactersIntent.ViewModeChanged(it)) },
                    )
                    Spacer(Modifier.width(ViewTogglePad))
                    IconButton(onClick = launchPicker) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = null,
                            tint = cs.onSurfaceVariant,
                        )
                    }
                    Spacer(Modifier.width(GapItems))
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null,
                            tint = cs.onSurfaceVariant,
                        )
                    }
                    Spacer(Modifier.width(GapItems))
                    Box {
                        IconButton(onClick = { filterOpen = !filterOpen }) {
                            Icon(
                                imageVector = if (state.filter.activeCount > 0) Icons.Filled.Tune else Icons.Outlined.Tune,
                                contentDescription = null,
                                tint = if (state.filter.activeCount > 0) ForgeColors.spark else cs.onSurfaceVariant,
                            )
                        }
                        if (filterOpen) {
                            val density = LocalDensity.current
                            Popup(
                                alignment = Alignment.TopEnd,
                                offset = IntOffset(0, with(density) { (TopBarHeight + FilterPopupGap).roundToPx() }),
                                onDismissRequest = { filterOpen = false },
                                properties = PopupProperties(focusable = true),
                            ) {
                                Surface(
                                    shape = MaterialTheme.shapes.large,
                                    color = cs.surfaceVariant,
                                    border = BorderStroke(FilterPopupBorder, cs.outline),
                                    shadowElevation = FilterPopupShadow,
                                    modifier = Modifier
                                        .width(FilterPopupWidth)
                                        .heightIn(max = FilterPopupMaxHeight),
                                ) {
                                    FilterPanel(
                                        filter = state.filter,
                                        availableTags = state.availableTags,
                                        onIntent = viewModel::handle,
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.width(GapItems))
                    IconButton(onClick = { searchExpanded = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = cs.onSurfaceVariant,
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = cs.outline)
    }

    if (searchExpanded) {
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    }
}
