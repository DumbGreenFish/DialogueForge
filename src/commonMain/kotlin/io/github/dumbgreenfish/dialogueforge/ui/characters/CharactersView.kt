package io.github.dumbgreenfish.dialogueforge.ui.characters

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.characters_empty
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.CharactersViewMode
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.CharacterCardGrid
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.CharacterCardList
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.CompactHeader
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.WideHeader
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

private val GRID_CELLS_LIST         = GridCells.Adaptive(400.dp)
private val GRID_CELLS_GRID_COMPACT = GridCells.Fixed(2)
private val GRID_CELLS_GRID_WIDE    = GridCells.Adaptive(220.dp)

@Composable
@OptIn(KoinExperimentalAPI::class)
fun CharactersView(modifier: Modifier = Modifier, isCompact: Boolean = false) {
    val viewModel = koinViewModel<CharactersViewModel>()
    val state by viewModel.state.collectAsState()

    val gridColumns = when {
        state.viewMode == CharactersViewMode.List -> GRID_CELLS_LIST
        isCompact                                 -> GRID_CELLS_GRID_COMPACT
        else                                      -> GRID_CELLS_GRID_WIDE
    }

    LazyVerticalGrid(
        columns = gridColumns,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            if (isCompact) CompactHeader(state, viewModel::handle)
            else           WideHeader(state, viewModel::handle)
        }

        if (state.displayed.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) { EmptyState() }
        } else {
            items(state.displayed, key = { it.id }) { char ->
                if (state.viewMode == CharactersViewMode.List) {
                    CharacterCardList(char = char, onClick = {})
                } else {
                    CharacterCardGrid(char = char, onClick = {})
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 80.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(Res.string.characters_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = ForgeColors.onSurfaceFaint,
            textAlign = TextAlign.Center,
        )
    }
}
