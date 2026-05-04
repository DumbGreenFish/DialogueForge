package ru.greenfish.dialogueforge

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.lyricist.ProvideStrings
import cafe.adriel.lyricist.rememberStrings
import ru.greenfish.dialogueforge.strings.Locales
import ru.greenfish.dialogueforge.strings.Strings
import ru.greenfish.dialogueforge.strings.StringsEn
import ru.greenfish.dialogueforge.strings.StringsRu
import ru.greenfish.dialogueforge.ui.home.HomeView

@Composable
fun App() {
    ProvideStrings {
        MaterialTheme {
            HomeView()
        }
    }
}
