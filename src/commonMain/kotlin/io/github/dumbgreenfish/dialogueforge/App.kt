package io.github.dumbgreenfish.dialogueforge

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import io.github.dumbgreenfish.dialogueforge.design.DialogueForgeTheme
import io.github.dumbgreenfish.dialogueforge.design.WithReferenceDensity
import io.github.dumbgreenfish.dialogueforge.koin.KoinConfigModule
import io.github.dumbgreenfish.dialogueforge.ui.common.mouseNav
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavBar
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavController
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavScreen
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab
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
            WithReferenceDensity {
                val controller = koinInject<NavController>()
                val activeTab by controller.activeTab.collectAsState()

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .mouseNav(
                            onBack = {
                                @Suppress("UNCHECKED_CAST")
                                val bar = controller.getBar(activeTab) as? NavBar<NavScreen>
                                bar?.popBack()
                            },
                            onForward = {
                                @Suppress("UNCHECKED_CAST")
                                val bar = controller.getBar(activeTab) as? NavBar<NavScreen>
                                bar?.popForward()
                            },
                        ),
                    color = MaterialTheme.colorScheme.background
                ) {
                    key(activeTab) {
                        val bar = controller.getBar(activeTab)
                        @Suppress("UNCHECKED_CAST")
                        val stack = (bar as NavBar<NavScreen>).stack
                        val topScreen = stack.lastOrNull()

                        Box(modifier = Modifier.fillMaxSize()) {
                            AnimatedContent(
                                targetState = topScreen,
                                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                                label = "screen-transition",
                            ) { screen ->
                                screen?.Render(onBack = { bar.popBack() })
                            }

                            Box(modifier = Modifier.size(0.dp)) {
                                @Suppress("UNCHECKED_CAST")
                                NavDisplay(
                                    backStack = stack as List<NavScreen>,
                                    onBack = { bar.popBack() },
                                ) { screen ->
                                    NavEntry(screen) {}
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
