package io.github.dumbgreenfish.dialogueforge.service.generation.coordination

import kotlinx.coroutines.CancellationException

class UserGenerationCancellationException :
    CancellationException("Generation cancelled by user")
