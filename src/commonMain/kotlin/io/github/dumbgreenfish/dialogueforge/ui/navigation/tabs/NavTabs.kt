package io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Settings
import com.composables.icons.lucide.SlidersHorizontal
import com.composables.icons.lucide.User
import com.composables.icons.lucide.Users
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.nav_characters
import io.github.dumbgreenfish.dialogueforge.generated.resources.nav_persona
import io.github.dumbgreenfish.dialogueforge.generated.resources.nav_presets
import io.github.dumbgreenfish.dialogueforge.generated.resources.nav_settings
import org.jetbrains.compose.resources.StringResource

enum class NavTabs(
    val tabObject: NavTab<out NavScreen>,
    val labelRes: StringResource,
    val icon: ImageVector
) {
    Characters(CharactersTab.instance, Res.string.nav_characters, Lucide.Users),
    Persona(PersonaTab.instance, Res.string.nav_persona, Lucide.User),
    Presets(PresetsTab.instance, Res.string.nav_presets, Lucide.SlidersHorizontal),
    Settings(SettingsTab.instance, Res.string.nav_settings, Lucide.Settings),
}