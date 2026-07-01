package io.github.dumbgreenfish.dialogueforge.ui.presets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.preset_api_key_label
import io.github.dumbgreenfish.dialogueforge.generated.resources.preset_api_key_placeholder
import io.github.dumbgreenfish.dialogueforge.generated.resources.preset_endpoint_label
import io.github.dumbgreenfish.dialogueforge.generated.resources.preset_endpoint_placeholder
import io.github.dumbgreenfish.dialogueforge.generated.resources.preset_max_tokens_label
import io.github.dumbgreenfish.dialogueforge.generated.resources.preset_model_label
import io.github.dumbgreenfish.dialogueforge.generated.resources.preset_model_placeholder
import io.github.dumbgreenfish.dialogueforge.generated.resources.preset_save
import io.github.dumbgreenfish.dialogueforge.generated.resources.preset_saved
import io.github.dumbgreenfish.dialogueforge.generated.resources.preset_temperature_label
import io.github.dumbgreenfish.dialogueforge.generated.resources.presets_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.math.roundToInt

private val FormPaddingH = 20.dp
private val FormPaddingV = 16.dp
private val FieldGap = 16.dp
private val SliderFieldGap = 4.dp
private val MaxTokensMax = 32768f

@Composable
@OptIn(KoinExperimentalAPI::class)
fun PresetsView(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<PresetsViewModel>()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handle(PresetsIntent.Load)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = FormPaddingH, vertical = FormPaddingV),
        verticalArrangement = Arrangement.spacedBy(FieldGap),
    ) {
        Text(
            text = stringResource(Res.string.presets_title),
            style = MaterialTheme.typography.headlineSmall,
        )

        OutlinedTextField(
            value = state.endpoint,
            onValueChange = { viewModel.handle(PresetsIntent.UpdateEndpoint(it)) },
            label = { Text(stringResource(Res.string.preset_endpoint_label)) },
            placeholder = { Text(stringResource(Res.string.preset_endpoint_placeholder)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = state.apiKey,
            onValueChange = { viewModel.handle(PresetsIntent.UpdateApiKey(it)) },
            label = { Text(stringResource(Res.string.preset_api_key_label)) },
            placeholder = { Text(stringResource(Res.string.preset_api_key_placeholder)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = state.model,
            onValueChange = { viewModel.handle(PresetsIntent.UpdateModel(it)) },
            label = { Text(stringResource(Res.string.preset_model_label)) },
            placeholder = { Text(stringResource(Res.string.preset_model_placeholder)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.preset_temperature_label),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = formatTemperature(state.temperature),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(Modifier.height(SliderFieldGap))
            Slider(
                value = state.temperature,
                onValueChange = { viewModel.handle(PresetsIntent.UpdateTemperature(it)) },
                valueRange = 0f..2f,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.preset_max_tokens_label),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = state.maxTokens.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(Modifier.height(SliderFieldGap))
            Slider(
                value = state.maxTokens.toFloat(),
                onValueChange = { viewModel.handle(PresetsIntent.UpdateMaxTokens(it.roundToInt())) },
                valueRange = 256f..MaxTokensMax,
                steps = ((MaxTokensMax - 256f) / 256f).toInt() - 1,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Button(
            onClick = { viewModel.handle(PresetsIntent.Save) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (state.isSaved) {
                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(Res.string.preset_saved))
            } else {
                Text(stringResource(Res.string.preset_save))
            }
        }
    }
}

private fun formatTemperature(t: Float): String {
    val scaled = (t * 10).roundToInt()
    return "${scaled / 10}.${scaled % 10}"
}
