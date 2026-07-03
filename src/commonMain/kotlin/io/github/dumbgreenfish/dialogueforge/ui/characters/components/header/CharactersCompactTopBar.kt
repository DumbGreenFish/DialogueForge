package io.github.dumbgreenfish.dialogueforge.ui.characters.components.header

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersIntent
import io.github.dumbgreenfish.dialogueforge.ui.characters.CharactersViewModel
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.filter.FilterPanel
import io.github.dumbgreenfish.dialogueforge.ui.common.ForgeMark
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.navItems
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private val TopBarHeight           = 64.dp
private val TopBarPaddingH         = 4.dp
private val ForgeMarkContainerSize = 32.dp
private val ForgeMarkSize          = 16.dp
private val ForgeMarkTitlePad      = 4.dp
private val GapItems               = 4.dp
private val FilterBadgeSize        = 16.dp
private val FilterBadgeOffset      = 2.dp
private val FilterBadgeFontSize    = 8.sp
private val ShadowElevation        = 4.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CharactersCompactTopBar() {
    val viewModel = koinViewModel<CharactersViewModel>()
    val state by viewModel.state.collectAsState()
    var searchExpanded by remember { mutableStateOf(false) }
    var filterOpen by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val item = navItems.first { it.tab == NavTab.Characters }
    val cs = MaterialTheme.colorScheme
    val filterActiveCount = state.filter.activeCount
    val filterActive = filterActiveCount > 0

    Surface(
        shadowElevation = ShadowElevation,
        color = cs.background,
    ) {
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
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .height(TopBarHeight)
                    .padding(horizontal = TopBarPaddingH),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(GapItems),
            ) {
                if (expanded) {
                    SearchField(
                        value = state.query,
                        onChange = { viewModel.handle(CharactersIntent.SearchChanged(it)) },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                    )

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
                    Box(
                        modifier = Modifier.size(ForgeMarkContainerSize),
                        contentAlignment = Alignment.Center,
                    ) {
                        ForgeMark(Modifier.size(ForgeMarkSize))
                    }

                    Text(
                        text = stringResource(item.labelRes),
                        style = MaterialTheme.typography.headlineSmall,
                        color = cs.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = ForgeMarkTitlePad),
                    )

                    Box {
                        IconButton(onClick = { filterOpen = true }) {
                            Icon(
                                imageVector = if (filterActive) Icons.Filled.Tune else Icons.Outlined.Tune,
                                contentDescription = null,
                                tint = if (filterActive) ForgeColors.spark else cs.onSurfaceVariant,
                            )
                        }
                        if (filterActive) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = FilterBadgeOffset, y = -FilterBadgeOffset)
                                    .size(FilterBadgeSize)
                                    .clip(CircleShape)
                                    .background(cs.primary),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = filterActiveCount.toString(),
                                    color = cs.onPrimary,
                                    fontSize = FilterBadgeFontSize,
                                    fontWeight = FontWeight.W700,
                                )
                            }
                        }
                    }

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
    }

    if (searchExpanded) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

    if (filterOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { filterOpen = false },
            sheetState = sheetState,
            containerColor = cs.surfaceVariant,
        ) {
            FilterPanel(filter = state.filter, availableTags = state.availableTags, onIntent = viewModel::handle)
        }
    }
}
