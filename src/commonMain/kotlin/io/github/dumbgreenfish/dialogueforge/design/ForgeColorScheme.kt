package io.github.dumbgreenfish.dialogueforge.design

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

private val Background            = Color(0xFF14110E)
private val Surface               = Color(0xFF1C1815)
private val SurfaceContainer      = Color(0xFF231E1A)
private val SurfaceContainerHigh  = Color(0xFF2C2620)
private val SurfaceContainerHighest = Color(0xFF352D26)
private val SurfaceContainerLow   = Color(0xFF1A1511)
private val SurfaceContainerLowest = Color(0xFF14110E)
private val SurfaceBright         = Color(0xFF3A3028)
private val SurfaceDim            = Color(0xFF14110E)

private val OnSurface             = Color(0xFFF2E4CF)
private val OnSurfaceVariant      = Color(0x9EF2E4CF)  // 62% opacity
private val Outline               = Color(0x1AF4C882)  // 10% opacity
private val OutlineVariant        = Color(0x33F4C882)  // 20% opacity

// copper (primary)
private val Primary               = Color(0xFFB87333)
private val PrimarySoft           = Color(0xFFC9854A)  // hover
private val PrimaryDeep           = Color(0xFF7A4A1F)  // pressed
private val PrimaryContainer      = Color(0xFF3A2614)  // copperDim
private val OnPrimary             = Color(0xFF1A0F06)
private val OnPrimaryContainer    = Color(0xFFF4D9B8)

// spark (accent / tertiary)
private val Tertiary              = Color(0xFFF4A340)
private val TertiaryHot          = Color(0xFFFFD27A)
private val TertiaryContainer     = Color(0xFF4A2E0F)
private val OnTertiary            = Color(0xFF1A0F06)
private val OnTertiaryContainer   = Color(0xFFFFE0B0)

private val Secondary             = Color(0xFFC9854A)
private val OnSecondary           = Color(0xFF1A0F06)
private val SecondaryContainer    = Color(0xFF3A2614)
private val OnSecondaryContainer  = Color(0xFFF4D9B8)

private val Error                 = Color(0xFFE8623A)
private val OnError               = Color(0xFF1A0F06)
private val ErrorContainer        = Color(0xFF6B1C16)
private val OnErrorContainer      = Color(0xFFFFDAD6)

private val InverseSurface        = Color(0xFFF2E4CF)
private val InverseOnSurface      = Color(0xFF2C2620)
private val InversePrimary        = Color(0xFF3A2614)

private val Scrim                 = Color(0xFF000000)
private val SurfaceTint           = Primary

// ── Scheme ───────────────────────────────────────────────────────────────────

val forgeColorScheme = ColorScheme(
    primary                = Primary,
    onPrimary              = OnPrimary,
    primaryContainer       = PrimaryContainer,
    onPrimaryContainer     = OnPrimaryContainer,
    inversePrimary         = InversePrimary,
    secondary              = Secondary,
    onSecondary            = OnSecondary,
    secondaryContainer     = SecondaryContainer,
    onSecondaryContainer   = OnSecondaryContainer,
    tertiary               = Tertiary,
    onTertiary             = OnTertiary,
    tertiaryContainer      = TertiaryContainer,
    onTertiaryContainer    = OnTertiaryContainer,
    background             = Background,
    onBackground           = OnSurface,
    surface                = Surface,
    onSurface              = OnSurface,
    surfaceVariant         = SurfaceContainerHigh,
    onSurfaceVariant       = OnSurfaceVariant,
    surfaceTint            = SurfaceTint,
    inverseSurface         = InverseSurface,
    inverseOnSurface       = InverseOnSurface,
    error                  = Error,
    onError                = OnError,
    errorContainer         = ErrorContainer,
    onErrorContainer       = OnErrorContainer,
    outline                = Outline,
    outlineVariant         = OutlineVariant,
    scrim                  = Scrim,
    surfaceBright          = SurfaceBright,
    surfaceDim             = SurfaceDim,
    surfaceContainer       = SurfaceContainer,
    surfaceContainerHigh   = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    surfaceContainerLow    = SurfaceContainerLow,
    surfaceContainerLowest = SurfaceContainerLowest,
    primaryFixed           = PrimaryContainer,
    primaryFixedDim        = PrimaryDeep,
    onPrimaryFixed         = OnPrimaryContainer,
    onPrimaryFixedVariant  = OnSurfaceVariant,
    secondaryFixed         = SecondaryContainer,
    secondaryFixedDim      = Color(0xFF2C1A0A),
    onSecondaryFixed       = OnSecondaryContainer,
    onSecondaryFixedVariant = OnSurfaceVariant,
    tertiaryFixed          = TertiaryContainer,
    tertiaryFixedDim       = Color(0xFF3A2008),
    onTertiaryFixed        = OnTertiaryContainer,
    onTertiaryFixedVariant = OnSurfaceVariant,
)

val ForgeColors = object {
    val copperSoft         = PrimarySoft
    val copperDeep         = PrimaryDeep
    val copperDim          = PrimaryContainer
    val spark              = Tertiary
    val sparkHot           = TertiaryHot
    val sparkContainer     = TertiaryContainer
    val onSurfaceFaint     = Color(0x66F2E4CF)  // 40% opacity
    val success            = Color(0xFF7BB369)
}
