package io.github.dumbgreenfish.dialogueforge.ui.characters

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.characters_empty
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.card.CharacterCardGrid
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.card.CharacterCardList
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.CharactersSpeedDial
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.header.CompactHeader
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharactersViewMode
import io.github.dumbgreenfish.dialogueforge.ui.common.rememberFilePicker
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.menu.DeleteCharacterDialog
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.navigation.CharactersTab
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavController
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab

private val GRID_CELLS_LIST         = GridCells.Adaptive(400.dp)
private val GRID_CELLS_GRID_COMPACT = GridCells.Fixed(2)
private val GRID_CELLS_GRID_WIDE    = GridCells.Adaptive(160.dp)

private val ContentPaddingWideH    = 28.dp
private val ContentPaddingWideT    = 24.dp
private val ContentPaddingWideB    = 32.dp
private val ContentPaddingCompactH = 16.dp
private val ContentPaddingCompactT = 4.dp
private val ContentPaddingCompactB = 100.dp
private val CardGapH               = 12.dp
private val CardGapVWide           = 12.dp
private val CardGapVCompact        = 10.dp
private val SpeedDialPaddingEnd    = 16.dp
private val SpeedDialPaddingBottom = 16.dp
private val EmptyStateVerticalPadding = 80.dp

private val ScrimColor        = Color(0x52000000)
private val ScrimAnimDuration = 180

@Composable
@OptIn(KoinExperimentalAPI::class)
fun CharactersView(modifier: Modifier = Modifier, isCompact: Boolean = false) {
    val viewModel = koinViewModel<CharactersViewModel>()
    val controller = koinInject<NavController>()
    val state by viewModel.state.collectAsState()
    var fabExpanded by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Character?>(null) }
    val scrimColor by animateColorAsState(
        targetValue   = if (fabExpanded) ScrimColor else Color.Transparent,
        animationSpec = tween(ScrimAnimDuration),
        label         = "scrim-color",
    )

    val launchFilePicker = rememberFilePicker { bytes, filename ->
        viewModel.handle(CharactersIntent.ImportFile(bytes, filename))
    }

    val gridColumns = when {
        state.viewMode == CharactersViewMode.List -> GRID_CELLS_LIST
        isCompact                                 -> GRID_CELLS_GRID_COMPACT
        else                                      -> GRID_CELLS_GRID_WIDE
    }

    Box(modifier = modifier) {
        LazyVerticalGrid(
            columns             = gridColumns,
            modifier            = Modifier.fillMaxSize(),
            contentPadding      = if (isCompact)
                PaddingValues(start = ContentPaddingCompactH, top = ContentPaddingCompactT, end = ContentPaddingCompactH, bottom = ContentPaddingCompactB)
            else
                PaddingValues(start = ContentPaddingWideH, top = ContentPaddingWideT, end = ContentPaddingWideH, bottom = ContentPaddingWideB),
            horizontalArrangement = Arrangement.spacedBy(CardGapH),
            verticalArrangement   = Arrangement.spacedBy(if (isCompact) CardGapVCompact else CardGapVWide),
        ) {
            if (isCompact) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    CompactHeader(state, viewModel::handle)
                }
            }

            if (state.displayed.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) { EmptyState() }
            } else {
                items(state.displayed, key = { it.id }) { char ->
                    val onClick: () -> Unit = {
                        val bar = controller.getBar(NavTab.Characters)
                        if (bar is CharactersTab) {
                            bar.navigateTo(CharactersTab.Screen.ChatScreen(char.id))
                        }
                    }
                    if (state.viewMode == CharactersViewMode.List) {
                        CharacterCardList(
                            char = char,
                            onClick = onClick,
                            onDeleteRequest = { deleteTarget = it },
                            isCompact = isCompact,
                        )
                    } else {
                        CharacterCardGrid(
                            char = char,
                            onClick = onClick,
                            onDeleteRequest = { deleteTarget = it },
                            isCompact = isCompact,
                        )
                    }
                }
            }
        }

        if (isCompact) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(scrimColor)
                    .then(
                        if (fabExpanded) Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null,
                        ) { fabExpanded = false }
                        else Modifier
                    ),
            )
            CharactersSpeedDial(
                expanded      = fabExpanded,
                onToggle      = { fabExpanded = !fabExpanded },
                onImport      = { fabExpanded = false; launchFilePicker() },
                onCreateClick = { fabExpanded = false },
                modifier      = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = SpeedDialPaddingEnd, bottom = SpeedDialPaddingBottom),
            )
        }
        deleteTarget?.let { target ->
            DeleteCharacterDialog(
                onConfirm = {
                    viewModel.handle(CharactersIntent.DeleteCharacter(target.id))
                    deleteTarget = null
                },
                onDismiss = { deleteTarget = null },
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier        = Modifier.fillMaxWidth().padding(vertical = EmptyStateVerticalPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text      = stringResource(Res.string.characters_empty),
            style     = MaterialTheme.typography.bodyLarge,
            color     = ForgeColors.onSurfaceFaint,
            textAlign = TextAlign.Center,
        )
    }
}
