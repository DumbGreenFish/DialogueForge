package io.github.dumbgreenfish.dialogueforge.service

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DefaultCharacterDataTest {
    @Test
    fun current_airi_knows_about_standard_streamed_background_and_unbounded_generation() = runBlocking {
        assertEquals(6, DefaultCharacterData.AIRI_VERSION)

        val airi = assertNotNull(DefaultCharacterData.create())

        assertEquals("6", airi.characterVersion)
        assertContains(airi.creatorNotes, "continues generating after the Android app is minimized")
        assertContains(airi.creatorNotes, "Standard buffered generation is enabled by default")
        assertContains(airi.creatorNotes, "Streaming can be enabled in Chat settings")
        assertContains(airi.creatorNotes, "does not impose a client-side time limit")
        assertContains(airi.creatorNotes, "partial response is saved as a normal assistant message")
        assertContains(airi.creatorNotes, "stays detached while older messages are being read")
    }
}
