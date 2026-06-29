package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass

@Suppress("DEPRECATION")
val isCompact: Boolean @Composable get() = !currentWindowAdaptiveInfo()
    .windowSizeClass
    .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
