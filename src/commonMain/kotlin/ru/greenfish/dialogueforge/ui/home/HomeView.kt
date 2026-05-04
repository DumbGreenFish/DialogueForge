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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.strings

@Composable
fun HomeView() {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        Text(strings.homeStrings.app_name)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(strings.homeStrings.greetings)
        Text(strings.homeStrings.text_click)
        val clickedText = strings.homeStrings.button_text_clicked
        Button(onClick = { print(clickedText) }) {
            Text(strings.homeStrings.button_text_click)
        }
    }
}