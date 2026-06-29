package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_input_hint
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_placeholder
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.CharacterAvatar
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

private val TopBarHeight        = 64.dp
private val TopBarPaddingH      = 8.dp
private val TopBarGap           = 8.dp
private val AvatarSize          = 40.dp
private val BodyPadding         = 32.dp
private val ComposerPadding     = 12.dp
private val ComposerGap         = 8.dp
private val ComposerFieldHeight = 48.dp
private val ComposerFieldPaddingH = 16.dp

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
                    Row(
                        modifier = Modifier.fillMaxWidth().height(TopBarHeight).padding(horizontal = TopBarPaddingH),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(TopBarGap),
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = cs.onSurface)
                        }
                        state.character?.let { char ->
                            CharacterAvatar(letter = char.letter, modifier = Modifier.size(AvatarSize), shape = ForgeShape.avatar, fontSize = 18.sp, avatarBytes = char.avatarBytes)
                            Text(char.name, style = MaterialTheme.typography.titleMedium, color = cs.onSurface, modifier = Modifier.weight(1f))
                        }
                    }
                    HorizontalDivider(color = cs.outline)
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth().padding(BodyPadding),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.dialogue_placeholder),
                            style = MaterialTheme.typography.bodyLarge,
                            color = ForgeColors.onSurfaceFaint,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(ComposerPadding),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(ComposerGap),
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(ComposerFieldHeight)
                                .clip(ForgeShape.pill)
                                .background(cs.surface)
                                .border(1.dp, cs.outline, ForgeShape.pill)
                                .padding(horizontal = ComposerFieldPaddingH),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(stringResource(Res.string.dialogue_input_hint), style = MaterialTheme.typography.bodyLarge, color = ForgeColors.onSurfaceFaint)
                        }
                        FilledIconButton(onClick = {}, enabled = false) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}
