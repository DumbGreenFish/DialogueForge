package io.github.dumbgreenfish.dialogueforge.service.generation.execution

import io.github.dumbgreenfish.dialogueforge.data.model.CharacterEntity
import org.koin.core.annotation.Single

@Single
class CharacterSystemPromptBuilder {
    fun build(character: CharacterEntity): String = buildString {
        appendLine("You are ${character.name}.")
        appendSection(character.description)
        appendSection(character.personality, prefix = "Personality: ")
        appendSection(character.scenario, prefix = "Scenario: ")
        appendLine()
        append(
            "Respond in character as ${character.name}. " +
                "Stay consistent with the description and personality above.",
        )
    }

    private fun StringBuilder.appendSection(
        value: String,
        prefix: String = "",
    ) {
        if (value.isBlank()) return
        appendLine()
        appendLine(prefix + value)
    }
}
