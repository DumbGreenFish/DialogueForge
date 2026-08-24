package io.github.dumbgreenfish.dialogueforge.service.generation.execution

import io.github.dumbgreenfish.dialogueforge.data.model.CharacterEntity
import kotlin.test.Test
import kotlin.test.assertEquals

class CharacterSystemPromptBuilderTest {
    @Test
    fun build_includes_every_non_blank_character_section_in_the_existing_format() {
        val prompt = CharacterSystemPromptBuilder().build(
            character(
                description = "Description",
                personality = "Curious",
                scenario = "Workshop",
            ),
        )

        assertEquals(
            """
            You are Airi.

            Description

            Personality: Curious

            Scenario: Workshop

            Respond in character as Airi. Stay consistent with the description and personality above.
            """.trimIndent(),
            prompt,
        )
    }

    @Test
    fun build_omits_blank_optional_sections_without_extra_section_spacing() {
        val prompt = CharacterSystemPromptBuilder().build(
            character(
                description = "",
                personality = "   ",
                scenario = "",
            ),
        )

        assertEquals(
            """
            You are Airi.

            Respond in character as Airi. Stay consistent with the description and personality above.
            """.trimIndent(),
            prompt,
        )
    }

    private fun character(
        description: String,
        personality: String,
        scenario: String,
    ) = CharacterEntity(
        id = "character-id",
        name = "Airi",
        description = description,
        creator = "",
        avatarData = byteArrayOf(),
        mainImageThumbnailData = byteArrayOf(),
        thumbnailSmall = byteArrayOf(),
        thumbnailMedium = byteArrayOf(),
        thumbnailLarge = byteArrayOf(),
        tags = emptyList(),
        specVersion = "3.0",
        pinned = false,
        chatCount = 0,
        importedAt = 1L,
        updatedAt = 1L,
        lastUsedAt = null,
        personality = personality,
        scenario = scenario,
    )
}
