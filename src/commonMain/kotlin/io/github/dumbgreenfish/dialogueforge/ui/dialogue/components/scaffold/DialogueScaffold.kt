package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val PanelBorderWidth = 1.dp
private const val PanelBorderAlpha = 0.06f

@Composable
internal fun DialogueScaffold(
    header: @Composable () -> Unit,
    composer: @Composable () -> Unit,
    messages: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    headerBackground: Color = MaterialTheme.colorScheme.background,
    composerBackground: Color = MaterialTheme.colorScheme.background,
) {
    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = PanelBorderAlpha)

    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerBackground),
            ) {
                header()
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PanelBorderWidth)
                    .background(borderColor)
                    .align(Alignment.BottomCenter),
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            messages()
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PanelBorderWidth)
                    .background(borderColor)
                    .align(Alignment.TopCenter),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(composerBackground),
            ) {
                composer()
            }
        }
    }
}
