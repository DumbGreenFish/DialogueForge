package ru.greenfish.dialogueforge.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import ru.greenfish.dialogueforge.generated.resources.Res
import ru.greenfish.dialogueforge.generated.resources.app_name
import ru.greenfish.dialogueforge.generated.resources.button_text_click
import ru.greenfish.dialogueforge.generated.resources.button_text_clicked
import ru.greenfish.dialogueforge.generated.resources.greetings
import ru.greenfish.dialogueforge.generated.resources.text_click

@Composable
@OptIn(KoinExperimentalAPI::class)
fun HomeView() {
    val viewModel = koinViewModel<HomeViewModel>()
    val state by viewModel.state.collectAsState()
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        Text(stringResource(Res.string.app_name))
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(stringResource(Res.string.greetings))
        Text(stringResource(Res.string.text_click))
        val clickedText = stringResource(Res.string.button_text_clicked)
        Button(onClick = {
            viewModel.handle(HomeIntent.ShowRandomNumber)
        }) {
            Text(stringResource(Res.string.button_text_click))
        }
        Text(state.alert)
    }
}