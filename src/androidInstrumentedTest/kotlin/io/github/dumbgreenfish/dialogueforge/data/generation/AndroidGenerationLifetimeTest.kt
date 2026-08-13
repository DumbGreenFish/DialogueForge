package io.github.dumbgreenfish.dialogueforge.data.generation

import kotlin.test.Test
import kotlin.test.assertEquals

class AndroidGenerationLifetimeTest {
    @Test
    fun immediate_completion_does_not_stop_a_service_before_foreground_promotion() {
        val platform = RecordingAndroidGenerationLifetimePlatform()
        val lifetime = AndroidGenerationLifetime(platform)

        lifetime.activeGenerationsChanged(listOf(request("conversation-a", "Airi")))
        lifetime.activeGenerationsChanged(emptyList())

        assertEquals(listOf<Action>(Action.Start("Airi")), platform.actions)
    }

    @Test
    fun generation_after_immediate_completion_requests_a_fresh_service_start() {
        val platform = RecordingAndroidGenerationLifetimePlatform()
        val lifetime = AndroidGenerationLifetime(platform)

        lifetime.activeGenerationsChanged(listOf(request("conversation-a", "Airi")))
        lifetime.activeGenerationsChanged(emptyList())
        lifetime.activeGenerationsChanged(listOf(request("conversation-b", "Sasha")))

        assertEquals(
            listOf<Action>(Action.Start("Airi"), Action.Start("Sasha")),
            platform.actions,
        )
    }

    @Test
    fun concurrent_generation_change_updates_the_promoted_service() {
        val platform = RecordingAndroidGenerationLifetimePlatform()
        val lifetime = AndroidGenerationLifetime(platform)

        lifetime.activeGenerationsChanged(listOf(request("conversation-a", "Airi")))
        lifetime.activeGenerationsChanged(
            listOf(request("conversation-a", "Airi"), request("conversation-b", "Sasha")),
        )

        assertEquals(listOf(Action.Start("Airi"), Action.Update(2, null)), platform.actions)
    }

    private fun request(conversationId: String, characterName: String) = GenerationRequest(
        conversationId = conversationId,
        characterId = "character-$conversationId",
        characterName = characterName,
        userText = "Hello",
    )

    private sealed interface Action {
        data class Start(val characterName: String?) : Action
        data class Update(val activeCount: Int, val characterName: String?) : Action
    }

    private class RecordingAndroidGenerationLifetimePlatform : AndroidGenerationLifetimePlatform {
        val actions = mutableListOf<Action>()

        override fun startForegroundService(characterName: String?) {
            actions += Action.Start(characterName)
        }

        override fun updateNotification(activeCount: Int, characterName: String?) {
            actions += Action.Update(activeCount, characterName)
        }
    }
}
