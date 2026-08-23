package io.github.dumbgreenfish.dialogueforge.service

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DefaultCharacterDataTest {
    @Test
    fun current_airi_knows_about_standard_streamed_and_background_generation() = runBlocking {
        assertEquals(5, DefaultCharacterData.AIRI_VERSION)

        val airi = assertNotNull(DefaultCharacterData.create())

        assertEquals("5", airi.characterVersion)
        assertContains(airi.creatorNotes, "continues generating after the Android app is minimized")
        assertContains(airi.creatorNotes, "Standard buffered generation is enabled by default")
        assertContains(airi.creatorNotes, "Streaming can be enabled in Chat settings")
        assertContains(airi.creatorNotes, "partial response is saved as a normal assistant message")
        assertContains(airi.creatorNotes, "stays detached while older messages are being read")
    }
}
