package io.github.dumbgreenfish.dialogueforge.ui.settings.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.github.dumbgreenfish.dialogueforge.config.BackgroundGenerationSettings
import io.github.dumbgreenfish.dialogueforge.ui.settings.SettingsViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@Composable
@OptIn(KoinExperimentalAPI::class)
fun UiSettingsView() {
    val viewModel = koinViewModel<SettingsViewModel>()
    val backgroundGenerationSettings = koinInject<BackgroundGenerationSettings>()
    val state by viewModel.state.collectAsState()



}