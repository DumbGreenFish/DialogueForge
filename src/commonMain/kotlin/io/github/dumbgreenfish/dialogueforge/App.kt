package io.github.dumbgreenfish.dialogueforge

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.PredictiveBackHandler
import androidx.compose.ui.graphics.graphicsLayer
import io.github.dumbgreenfish.dialogueforge.data.config.DatabaseConfig
import io.github.dumbgreenfish.dialogueforge.data.model.DefaultCharacterData
import io.github.dumbgreenfish.dialogueforge.data.repository.character.CharacterRepository
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.ForgeSettings
import io.github.dumbgreenfish.dialogueforge.design.DialogueForgeTheme
import io.github.dumbgreenfish.dialogueforge.design.ForgeAnimation
import io.github.dumbgreenfish.dialogueforge.design.WithReferenceDensity
import io.github.dumbgreenfish.dialogueforge.koin.KoinConfigModule
import io.github.dumbgreenfish.dialogueforge.ui.common.ImportProgressOverlay
import io.github.dumbgreenfish.dialogueforge.ui.common.mouseNav
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavBar
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavController
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavScreen
import io.github.dumbgreenfish.dialogueforge.ui.navigation.ui.NavTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.annotation.KoinApplication
import org.koin.dsl.koinConfiguration
import org.koin.plugin.module.dsl.startKoin
import kotlin.coroutines.cancellation.CancellationException

@KoinApplication(modules = [KoinConfigModule::class])
object ForgeApp {
    fun initKoin() = startKoin<ForgeApp> {}
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun App() {
    KoinApplication(configuration = koinConfiguration {}) {
        DialogueForgeTheme {
            val forgeSettings = koinInject<ForgeSettings>()
            val characterRepo = koinInject<CharacterRepository>()
            val dbConfig = koinInject<DatabaseConfig>()
            val densityScale by forgeSettings.densityScale.collectAsState()
            val fontScale by forgeSettings.fontScale.collectAsState()
            val hasCompletedFirstLaunch by forgeSettings.hasCompletedFirstLaunch.collectAsState()

            data class ImportProgress(val characterName: String)
            var importingState by remember { mutableStateOf<ImportProgress?>(null) }

            LaunchedEffect(Unit) {
                withContext(Dispatchers.Default) {
                    dbConfig.mainDatabase()
                }

                if (!hasCompletedFirstLaunch) {
                    importingState = ImportProgress("Airi")
                    val defaultData = DefaultCharacterData.create()
                    if (defaultData != null) {
                        withContext(Dispatchers.Default) {
                            characterRepo.import(defaultData)
                        }
                        forgeSettings.setHasCompletedFirstLaunch()
                        forgeSettings.setAiriVersion(DefaultCharacterData.AIRI_VERSION)
                    }
                    importingState = null
                } else {
                    val storedVersion = forgeSettings.airiVersion.value
                    val resolvedVersion = if (storedVersion == 0) {
                        if (withContext(Dispatchers.Default) { characterRepo.existsByName("Airi") }) {
                            forgeSettings.setAiriVersion(DefaultCharacterData.AIRI_VERSION)
                            DefaultCharacterData.AIRI_VERSION
                        } else {
                            0
                        }
                    } else {
                        storedVersion
                    }
                    if (resolvedVersion > 0 && resolvedVersion < DefaultCharacterData.AIRI_VERSION) {
                        forgeSettings.setAiriUpdateAvailable(true)
                    }
                }

                if (BuildConfig.DEBUG) {
                    val exists = withContext(Dispatchers.Default) {
                        characterRepo.existsByName("Sasha")
                    }
                    if (!exists) {
                        importingState = ImportProgress("Sasha")
                        val debugData = DefaultCharacterData.createDebug()
                        if (debugData != null) {
                            withContext(Dispatchers.Default) {
                                characterRepo.import(debugData)
                            }
                        }
                        importingState = null
                    }
                }
            }

            WithReferenceDensity(densityScale, fontScale) {
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
                Box(modifier = Modifier.fillMaxSize()) {
                key(activeTab) {
                    val bar = controller.getBar(activeTab)
                    @Suppress("UNCHECKED_CAST")
                    val stack = (bar as NavBar<NavScreen>).stack
                    val topScreen = stack.lastOrNull()

                    val backProgress = remember { Animatable(0f) }
                    val backScope = rememberCoroutineScope()

                    PredictiveBackHandler(enabled = stack.size > 1) { progress ->
                        try {
                            progress.collect { event -> backProgress.snapTo(event.progress) }
                            bar.popBack()
                            backProgress.snapTo(0f)
                        } catch (e: CancellationException) {
                            backScope.launch { backProgress.animateTo(0f) }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                val p = backProgress.value
                                val scale = 1f - (1f - ForgeAnimation.PredictiveBackMinScale) * p
                                scaleX = scale
                                scaleY = scale
                                alpha = 1f - (1f - ForgeAnimation.PredictiveBackMinAlpha) * p
                            },
                    ) {
                        AnimatedContent(
                            targetState = topScreen,
                            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                            label = "screen-transition",
                        ) { screen ->
                            screen?.Render(onBack = { bar.popBack() })
                        }
                    }
                }

                importingState?.let { state ->
                    ImportProgressOverlay(state.characterName)
                }
                }
            }
            }
        }
    }
}
