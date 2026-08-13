package io.github.dumbgreenfish.dialogueforge.data.generation

import org.koin.core.annotation.Single

@Single(binds = [GenerationLifetime::class])
class AndroidGenerationLifetime internal constructor(
    private val platform: AndroidGenerationLifetimePlatform,
) : GenerationLifetime {
    private var serviceStarted = false

    @Synchronized
    override fun activeGenerationsChanged(activeGenerations: List<GenerationRequest>) {
        val activeCount = activeGenerations.size
        if (activeCount == 0) {
            serviceStarted = false
            return
        }

        val characterName = activeGenerations.singleOrNull()?.characterName
        if (!serviceStarted) {
            platform.startForegroundService(characterName)
            serviceStarted = true
        } else {
            platform.updateNotification(activeCount, characterName)
        }
    }
}
