package ru.greenfish.dialogueforge

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.lyricist.ProvideStrings
import ru.greenfish.dialogueforge.ui.home.HomeView
import ru.greenfish.dialogueforge.ui.home.HomeViewModel

@Composable
fun App() {
    ProvideStrings {
        MaterialTheme {
            HomeView(HomeViewModel())
        }
    }
}
