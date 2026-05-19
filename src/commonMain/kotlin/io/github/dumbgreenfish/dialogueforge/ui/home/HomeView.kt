package io.github.dumbgreenfish.dialogueforge.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.generated.resources.*
import io.github.dumbgreenfish.dialogueforge.ui.ForgeMark
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@Composable
@OptIn(KoinExperimentalAPI::class)
fun HomeView(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<HomeViewModel>()
    val state by viewModel.state.collectAsState()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ForgeMark(Modifier.size(96.dp))
        Text(stringResource(Res.string.greetings))
        Text(stringResource(Res.string.text_click))
        Button(onClick = {
            viewModel.handle(HomeIntent.ShowRandomNumber)
        }) {
            Text(stringResource(Res.string.button_text_click))
        }
        Text(state.alert)
    }
}