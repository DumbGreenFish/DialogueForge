package io.github.dumbgreenfish.dialogueforge.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.app_name
import io.github.dumbgreenfish.dialogueforge.generated.resources.button_text_click
import io.github.dumbgreenfish.dialogueforge.generated.resources.button_text_clicked
import io.github.dumbgreenfish.dialogueforge.generated.resources.greetings
import io.github.dumbgreenfish.dialogueforge.generated.resources.text_click

@Composable
@OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3Api::class)
fun HomeView() {
    val viewModel = koinViewModel<HomeViewModel>()
    val state by viewModel.state.collectAsState()
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(stringResource(Res.string.app_name))
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
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
}