package ru.greenfish.dialogueforge.strings

import cafe.adriel.lyricist.LyricistStrings

interface Strings {
    val homeStrings: HomeStrings
}

interface HomeStrings {
    val app_name: String
    val greetings: String
    val text_click: String
    val button_text_click: String
    val button_text_clicked: String
}