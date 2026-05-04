package ru.greenfish.dialogueforge.strings

import cafe.adriel.lyricist.LyricistStrings

@LyricistStrings(languageTag = Locales.RU)
val StringsRu: Strings = object : Strings {
    override val homeStrings = object : HomeStrings {
        override val app_name = "Dialogue Forge"
        override val greetings = "Привет!"
        override val text_click = "Нажми на кнопку!!"
        override val button_text_click = "Кликни меня!"
        override val button_text_clicked = "Кнопка нажата!"
    }
}