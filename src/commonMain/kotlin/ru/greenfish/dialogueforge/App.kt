package ru.greenfish.dialogueforge

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import ru.greenfish.dialogueforge.koin.KoinConfigModule
import ru.greenfish.dialogueforge.ui.home.HomeView
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
        MaterialTheme {
            HomeView()
        }
    }
}
