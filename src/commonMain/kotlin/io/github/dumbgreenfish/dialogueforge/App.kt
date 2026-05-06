package io.github.dumbgreenfish.dialogueforge

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import io.github.dumbgreenfish.dialogueforge.koin.KoinConfigModule
import io.github.dumbgreenfish.dialogueforge.ui.home.HomeView
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
