package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_placeholder
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.ChatHeader
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.Composer
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

private val BodyPaddingH = 12.dp

@Composable
@OptIn(KoinExperimentalAPI::class)
fun DialogueView(characterId: String, onBack: () -> Unit) {
    val viewModel = koinViewModel<DialogueViewModel>()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(characterId) {
        viewModel.handle(DialogueIntent.LoadCharacter(characterId))
    }

    val cs = MaterialTheme.colorScheme

    Scaffold { innerPadding ->
        Surface(modifier = Modifier.fillMaxSize().padding(innerPadding), color = cs.background) {
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ForgeColors.spark)
                }
            } else {
                Column(Modifier.fillMaxSize()) {
                    ChatHeader(
                        character = state.character,
                        presetName = state.presetName,
                        modelName = state.modelName,
                        onBack = onBack,
                    )
                    HorizontalDivider(color = cs.outline)
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = BodyPaddingH),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.dialogue_placeholder),
                            style = MaterialTheme.typography.bodyLarge,
                            color = ForgeColors.onSurfaceFaint,
                            textAlign = TextAlign.Center,
                        )
                    }
                    HorizontalDivider(color = cs.outline)
                    Composer(
                        inputText = state.inputText,
                        onInputChange = { viewModel.handle(DialogueIntent.UpdateInput(it)) },
                        onSend = { viewModel.handle(DialogueIntent.Send) },
                    )
                }
            }
        }
    }
}
