package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass

enum class WindowClass { Compact, Tablet, Wide }

@Suppress("DEPRECATION")
val windowClass: WindowClass @Composable get() {
    val sizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return when {
        !sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)   -> WindowClass.Compact
        !sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)  -> WindowClass.Tablet
        else                                                                                -> WindowClass.Wide
    }
}
