package ru.greenfish.dialogueforge.strings

import cafe.adriel.lyricist.LyricistStrings

@LyricistStrings(languageTag = Locales.EN, default = true)
val StringsEn: Strings = object : Strings {
    override val homeStrings = object : HomeStrings {
        override val app_name = "Dialogue Forge"
        override val greetings = "Hello!"
        override val text_click = "Click the button!"
        override val button_text_click = "Click the button!"
        override val button_text_clicked = "Button clicked!"
    }
}