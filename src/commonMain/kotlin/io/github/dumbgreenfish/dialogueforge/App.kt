package io.github.dumbgreenfish.dialogueforge

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.window.core.layout.WindowSizeClass
import io.github.dumbgreenfish.dialogueforge.design.DialogueForgeTheme
import io.github.dumbgreenfish.dialogueforge.koin.KoinConfigModule
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab
import org.koin.compose.KoinApplication
import org.koin.core.annotation.KoinApplication
import org.koin.dsl.koinConfiguration
import org.koin.plugin.module.dsl.startKoin

@KoinApplication(modules = [KoinConfigModule::class])
object ForgeApp {
    fun initKoin() = startKoin<ForgeApp> {}
}

// currentWindowAdaptiveInfo is deprecated but there is no alternative for CMP project so
// it's okay to use this version until normal one is presented
@Suppress("DEPRECATION")
private val isCompact: Boolean @Composable get() = !currentWindowAdaptiveInfo()
    .windowSizeClass
    .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

@Composable
fun App() {
    KoinApplication(configuration = koinConfiguration {}) {
        DialogueForgeTheme {
            var selectedTab by remember { mutableStateOf(NavTab.Characters) }
            val onTabChange: (NavTab) -> Unit = { selectedTab = it }
            when {
                isCompact -> CompactScaffold(selectedTab, onTabChange)
                else -> WideScaffold(selectedTab, onTabChange)
            }
        }
    }
}
