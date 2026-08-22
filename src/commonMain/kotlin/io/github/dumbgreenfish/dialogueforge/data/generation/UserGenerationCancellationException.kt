package io.github.dumbgreenfish.dialogueforge.data.generation

import kotlinx.coroutines.CancellationException

internal class UserGenerationCancellationException : CancellationException("Generation cancelled by user")
