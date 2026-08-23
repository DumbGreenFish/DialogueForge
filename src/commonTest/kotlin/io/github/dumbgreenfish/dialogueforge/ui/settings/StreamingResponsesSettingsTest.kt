package io.github.dumbgreenfish.dialogueforge.ui.settings

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.github.dumbgreenfish.dialogueforge.service.ForgeSettings
import io.github.dumbgreenfish.dialogueforge.design.DialogueForgeTheme
import io.github.dumbgreenfish.dialogueforge.testing.FakeSettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class StreamingResponsesSettingsTest {
    @Test
    fun streaming_responses_are_disabled_by_default_loaded_persisted_and_reset() = runBlocking {
        val repository = FakeSettingsRepository()
        val settings = ForgeSettings(repository)
        withTimeout(TEST_TIMEOUT_MILLIS) { settings.state.first { it.isLoaded } }

        assertFalse(settings.state.value.streamResponses)

        settings.setStreamResponses(true)
        assertTrue(settings.state.value.streamResponses)
        withTimeout(TEST_TIMEOUT_MILLIS) {
            while (repository.get("stream_responses") != "true") kotlinx.coroutines.yield()
        }

        settings.reset()
        assertFalse(settings.state.value.streamResponses)
        withTimeout(TEST_TIMEOUT_MILLIS) {
            while (repository.get("stream_responses") != "false") kotlinx.coroutines.yield()
        }
    }

    @Test
    fun settings_view_model_forwards_streaming_toggle() = runBlocking {
        val settings = ForgeSettings(FakeSettingsRepository())
        val viewModel = SettingsViewModel(settings)
        withTimeout(TEST_TIMEOUT_MILLIS) { viewModel.state.first { it.isLoaded } }

        viewModel.handle(SettingsIntent.UpdateStreamResponses(true))

        assertTrue(viewModel.state.value.streamResponses)
    }

    @Test
    fun streaming_setting_displays_switch_and_reports_new_value() = runComposeUiTest {
        var requestedValue: Boolean? = null

        setContent {
            DialogueForgeTheme {
                StreamingResponsesSetting(
                    headline = "Streaming test setting",
                    enabled = false,
                    onEnabledChange = { requestedValue = it },
                )
            }
        }

        onNodeWithText("Streaming test setting").assertExists()
        onNodeWithTag(StreamingResponsesSwitchTag).assertIsOff().performClick()
        assertEquals(true, requestedValue)
    }

    private companion object {
        const val TEST_TIMEOUT_MILLIS = 5_000L
    }
}
