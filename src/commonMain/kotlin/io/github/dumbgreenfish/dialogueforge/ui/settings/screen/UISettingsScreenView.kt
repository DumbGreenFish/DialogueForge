package io.github.dumbgreenfish.dialogueforge.ui.settings.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.alorma.compose.settings.ui.SettingsSlider
import io.github.dumbgreenfish.dialogueforge.config.BackgroundGenerationSettings
import io.github.dumbgreenfish.dialogueforge.design.ForgeShapes
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_density_save
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_density_scale
import io.github.dumbgreenfish.dialogueforge.ui.settings.SettingsIntent
import io.github.dumbgreenfish.dialogueforge.ui.settings.SettingsState
import io.github.dumbgreenfish.dialogueforge.ui.settings.SettingsViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.math.roundToInt

private val ContentPadH             = 16.dp
private const val DensityMin        = 0.7f
private const val DensityMax        = 3.0f

@Composable
@OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3Api::class)
fun UiSettingsView(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<SettingsViewModel>()
    val backgroundGenerationSettings = koinInject<BackgroundGenerationSettings>()
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ContentPadH, vertical = ContentPadH),
    ) {
        DensitySlider(state, viewModel)
    }
}

@Composable
private fun DensitySlider(
    state: SettingsState,
    viewModel: SettingsViewModel
) {
    var densitySliderState by remember { mutableFloatStateOf(state.densityScale) }
    SettingsSlider(
        value = densitySliderState,
        valueRange = DensityMin..DensityMax,
        title = {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(Res.string.settings_density_scale))
                Spacer(Modifier.weight(1f))
                DensityField(
                    value = densitySliderState,
                    onValueChange = { densitySliderState = it },
                    shape = ForgeShapes.medium
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        viewModel.handle(SettingsIntent.UpdateDensityScale(densitySliderState))
                    },
                    content = {
                        Text(stringResource(Res.string.settings_density_save))
                    },
                    shape = ForgeShapes.medium
                )
            }
        },
        onValueChange = { densitySliderState = it },
        onValueChangeFinished = {
            viewModel.handle(SettingsIntent.UpdateDensityScale(densitySliderState))
        },
    )
}

@Composable
private fun DensityField(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = OutlinedTextFieldDefaults.shape
) {
    var text by remember { mutableStateOf(value.formatDensity()) }

    LaunchedEffect(value) {
        if (text.toFloatOrNull()?.roundTo1() != value.roundTo1()) {
            text = value.formatDensity()
        }
    }

    BasicTextField(
        value = text,
        onValueChange = {  raw ->
            val cleaned = raw.replace(',', '.')
            if (!cleaned.matches(Regex("""\d*\.?\d?"""))) return@BasicTextField
            text = cleaned
            cleaned.toFloatOrNull()
                ?.coerceIn(DensityMin, DensityMax)
                ?.let { onValueChange(it.roundTo1()) }
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier
            .width(40.dp)
            .height(40.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .padding(horizontal = 8.dp, vertical = 10.dp),
    )
}

private fun Float.roundTo1(): Float = (this * 10f).roundToInt() / 10f

private fun Float.formatDensity(): String {
    val r = roundTo1()
    return if (r == r.toInt().toFloat()) r.toInt().toString() else r.toString()
}
