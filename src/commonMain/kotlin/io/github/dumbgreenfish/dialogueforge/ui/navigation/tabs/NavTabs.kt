package io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs

enum class NavTabs(val tabObject: NavTab<out NavScreen>) {
    Characters(CharactersTab.instance),
    Persona(PersonaTab.instance),
    Presets(PresetsTab.instance),
    Settings(SettingsTab.instance)
}