package io.github.dumbgreenfish.dialogueforge

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
        }
    }
}
