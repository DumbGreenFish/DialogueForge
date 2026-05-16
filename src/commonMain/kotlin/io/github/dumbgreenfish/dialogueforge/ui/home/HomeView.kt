package io.github.dumbgreenfish.dialogueforge.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
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
            ForgeMark(Modifier.size(96.dp))
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