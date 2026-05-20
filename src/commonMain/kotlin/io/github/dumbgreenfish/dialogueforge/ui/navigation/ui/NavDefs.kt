package io.github.dumbgreenfish.dialogueforge.ui.navigation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.nav_characters
import io.github.dumbgreenfish.dialogueforge.generated.resources.nav_persona
import io.github.dumbgreenfish.dialogueforge.generated.resources.nav_presets
import io.github.dumbgreenfish.dialogueforge.generated.resources.nav_settings
import org.jetbrains.compose.resources.StringResource

enum class NavTab { Characters, Persona, Presets, Settings }

data class NavItemDef(
    val tab: NavTab,
    val labelRes: StringResource,
    val iconOutlined: ImageVector,
    val iconFilled: ImageVector,
)

val navItems = listOf(
    NavItemDef(NavTab.Characters, Res.string.nav_characters, Icons.Outlined.Groups,   Icons.Filled.Groups),
    NavItemDef(NavTab.Persona,    Res.string.nav_persona,    Icons.Outlined.Person,   Icons.Filled.Person),
    NavItemDef(NavTab.Presets,    Res.string.nav_presets,    Icons.Outlined.Tune,     Icons.Filled.Tune),
    NavItemDef(NavTab.Settings,   Res.string.nav_settings,   Icons.Outlined.Settings, Icons.Filled.Settings),
)
