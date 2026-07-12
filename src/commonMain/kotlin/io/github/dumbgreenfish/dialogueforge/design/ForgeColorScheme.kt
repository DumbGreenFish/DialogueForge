package io.github.dumbgreenfish.dialogueforge.design

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

private val Background    = Color(0xFF14110E)
private val Surface       = Color(0xFF1C1815)
private val SurfaceVariant = Color(0xFF231E1A)

private val OnSurface         = Color(0xFFF2E4CF)
private val OnSurfaceVariant  = Color(0x9EF2E4CF) // 62%
private val Outline           = Color(0x1AF4C882) // 10% copper
private val OutlineVariant    = Color(0x33F4C882) // 20% copper

private val Primary           = Color(0xFFB87333)
private val PrimaryContainer  = Color(0xFF3A2614) // copperDim — user bubble, active nav pill, fallback avatars
private val OnPrimary         = Color(0xFF1A0F06)
private val OnPrimaryContainer = Color(0xFFF4D9B8)

private val Tertiary           = Color(0xFFF4A340)
private val TertiaryContainer  = Color(0xFF4A2E0F) // sparkContainer
private val OnTertiary         = Color(0xFF1A0F06)
private val OnTertiaryContainer = Color(0xFFFFE0B0)

private val Secondary            = Color(0xFFC9854A) // copperSoft
private val OnSecondary          = Color(0xFF1A0F06) // onCopper
private val SecondaryContainer   = Color(0xFF2C2620) // surface3, NOT copperDim
private val OnSecondaryContainer = Color(0x9EF2E4CF) // onSurfaceVariant (62%)

private val Error            = Color(0xFFE8623A)
private val ErrorContainer   = Color(0xFF6B1C16)
private val OnError          = Color(0xFF1A0F06)
private val OnErrorContainer = Color(0xFFFFDAD6)

val ForgeColorScheme: ColorScheme = darkColorScheme(
    primary               = Primary,
    onPrimary             = OnPrimary,
    primaryContainer      = PrimaryContainer,
    onPrimaryContainer    = OnPrimaryContainer,
    secondary             = Secondary,
    onSecondary           = OnSecondary,
    secondaryContainer    = SecondaryContainer,
    onSecondaryContainer  = OnSecondaryContainer,
    tertiary              = Tertiary,
    onTertiary            = OnTertiary,
    tertiaryContainer     = TertiaryContainer,
    onTertiaryContainer   = OnTertiaryContainer,
    background            = Background,
    onBackground          = OnSurface,
    surface               = Surface,
    onSurface             = OnSurface,
    surfaceVariant        = SurfaceVariant,
    onSurfaceVariant      = OnSurfaceVariant,
    outline               = Outline,
    outlineVariant        = OutlineVariant,
    error                 = Error,
    onError               = OnError,
    errorContainer        = ErrorContainer,
    onErrorContainer      = OnErrorContainer,
)

object ForgeColors {
    val copperSoft = Color(0xFFC9854A) // hover
    val copperDeep = Color(0xFF7A4A1F) // pressed
    val copperDim  = Color(0xFF3A2614) // = primaryContainer

    val spark          = Color(0xFFF4A340) // = tertiary
    val sparkHot       = Color(0xFFFFD27A) // spark inside the logo
    val sparkContainer = Color(0xFF4A2E0F) // = tertiaryContainer

    val surfaceContainerHigh    = Color(0xFF2C2620)
    val surfaceContainerHighest = Color(0xFF352D26)

    val onSurfaceFaint = Color(0x66F2E4CF) // 40% — dates, meta
    val onSurfaceMute  = Color(0x47F2E4CF) // 28% — disabled

    val forged = Color(0xFF7BB369)
}