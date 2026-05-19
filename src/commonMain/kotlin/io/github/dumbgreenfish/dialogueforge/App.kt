package io.github.dumbgreenfish.dialogueforge

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.window.core.layout.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.dumbgreenfish.dialogueforge.design.DialogueForgeTheme
import io.github.dumbgreenfish.dialogueforge.koin.KoinConfigModule
import io.github.dumbgreenfish.dialogueforge.ui.home.HomeView
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ForgeBottomNav
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavTab
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavigationSidebar
import org.koin.compose.KoinApplication
import org.koin.core.annotation.KoinApplication
import org.koin.dsl.koinConfiguration
import org.koin.plugin.module.dsl.startKoin

@KoinApplication(modules = [KoinConfigModule::class])
class ForgeApp

fun initKoin() = startKoin<ForgeApp> {}

@Composable
fun App() {
    KoinApplication(configuration = koinConfiguration {}) {
        DialogueForgeTheme {
            var selectedTab by remember { mutableStateOf(NavTab.Characters) }

            // currentWindowAdaptiveInfo is deprecated but there is no alternative for CMP project so
            // it's okay to use this version until normal one is presented
            @Suppress("DEPRECATION")
            val isCompact = !currentWindowAdaptiveInfo()
                .windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

            if (isCompact) {
                Scaffold(
                    bottomBar = {
                        ForgeBottomNav(
                            selected = selectedTab,
                            onSelect = { selectedTab = it },
                        )
                    },
                ) { innerPadding ->
                    HomeView(Modifier.fillMaxSize().padding(innerPadding))
                }
                return@DialogueForgeTheme
            }

            Row(Modifier.fillMaxSize()) {
                NavigationSidebar(
                    selected = selectedTab,
                    onSelect = { selectedTab = it },
                )
                VerticalDivider()
                HomeView(Modifier.fillMaxSize().weight(1f))
            }
        }
    }
}
