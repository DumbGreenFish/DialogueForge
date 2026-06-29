package io.github.dumbgreenfish.dialogueforge

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.github.dumbgreenfish.dialogueforge.design.DialogueForgeTheme
import io.github.dumbgreenfish.dialogueforge.koin.KoinConfigModule
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavBar
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavController
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavScreen
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.annotation.KoinApplication
import org.koin.dsl.koinConfiguration
import org.koin.plugin.module.dsl.startKoin

@KoinApplication(modules = [KoinConfigModule::class])
object ForgeApp {
    fun initKoin() = startKoin<ForgeApp> {}
}

@Composable
fun App() {
    KoinApplication(configuration = koinConfiguration {}) {
        DialogueForgeTheme {
            val controller = koinInject<NavController>()
            val bar by controller.currentBar.collectAsState()
            key(bar) {
                @Suppress("UNCHECKED_CAST")
                NavDisplay(
                    backStack = (bar as NavBar<NavScreen>).stack as List<NavScreen>,
                    entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
                    onBack = { bar.popBack() },
                ) { screen ->
                    NavEntry(screen) { s -> s.Render(onBack = { bar.popBack() }) }
                }
            }
        }
    }
}
