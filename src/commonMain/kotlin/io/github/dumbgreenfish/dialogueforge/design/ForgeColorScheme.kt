package io.github.dumbgreenfish.dialogueforge.design

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

private val Background            = Color(0xFF13140F)
private val Surface               = Color(0xFF1A1C15)
private val SurfaceContainer      = Color(0xFF22241C)
private val SurfaceContainerHigh  = Color(0xFF2A2C23)
private val SurfaceContainerHighest = Color(0xFF32342A)
private val SurfaceContainerLow   = Color(0xFF181A13)
private val SurfaceContainerLowest = Color(0xFF13140F)
private val SurfaceBright         = Color(0xFF34362D)
private val SurfaceDim            = Color(0xFF13140F)

private val OnSurface             = Color(0xFFECEFE0)
private val OnSurfaceVariant      = Color(0x9EECEFE0)  // 62% opacity
private val Outline               = Color(0x1AE6F0D7)  // 10% opacity
private val OutlineVariant        = Color(0x33E6F0D7)  // 20% opacity

private val Primary               = Color(0xFFA8E3A7)
private val PrimarySoft           = Color(0xFF8FC98F)  // hover
private val PrimaryDim            = Color(0xFF639563)  // pressed
private val PrimaryContainer      = Color(0xFF00460A)
private val OnPrimary             = Color(0xFF011D03)
private val OnPrimaryContainer    = Color(0xFFD4F1D3)

private val Secondary             = Color(0xFF8FC98F)
private val OnSecondary           = Color(0xFF011D03)
private val SecondaryContainer    = Color(0xFF1C2E1C)
private val OnSecondaryContainer  = Color(0xFFD9EAD7)

private val Tertiary              = Color(0xFFF5C298)
private val OnTertiary            = Color(0xFF2C1400)
private val TertiaryContainer     = Color(0xFF4B2914)
private val OnTertiaryContainer   = Color(0xFFFFE2C4)

private val Error                 = Color(0xFFFF958C)
private val OnError               = Color(0xFF4B0000)
private val ErrorContainer        = Color(0xFF6B1C16)
private val OnErrorContainer      = Color(0xFFFFDAD6)

private val InverseSurface        = Color(0xFFECEFE0)
private val InverseOnSurface      = Color(0xFF2A2C23)
private val InversePrimary        = Color(0xFF00460A)

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
    primaryFixedDim        = PrimaryDim,
    onPrimaryFixed         = OnPrimaryContainer,
    onPrimaryFixedVariant  = OnSurfaceVariant,
    secondaryFixed         = SecondaryContainer,
    secondaryFixedDim      = Color(0xFF142414),
    onSecondaryFixed       = OnSecondaryContainer,
    onSecondaryFixedVariant = OnSurfaceVariant,
    tertiaryFixed          = TertiaryContainer,
    tertiaryFixedDim       = Color(0xFF3A1E0D),
    onTertiaryFixed        = OnTertiaryContainer,
    onTertiaryFixedVariant = OnSurfaceVariant,
)

val ForgeColors = object {
    val primarySoft          = PrimarySoft
    val primaryDim           = PrimaryDim
    val onSurfaceFaint       = Color(0x61ECEFE0)
    val success              = Color(0xFF9BD69B)
}
