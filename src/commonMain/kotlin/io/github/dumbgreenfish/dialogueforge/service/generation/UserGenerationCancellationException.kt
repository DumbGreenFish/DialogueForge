package io.github.dumbgreenfish.dialogueforge.service.generation

import kotlinx.coroutines.CancellationException

internal class UserGenerationCancellationException : CancellationException("Generation cancelled by user")
