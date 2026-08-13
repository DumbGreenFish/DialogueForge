package io.github.dumbgreenfish.dialogueforge.data.generation

import kotlin.coroutines.cancellation.CancellationException

internal class UserGenerationCancellationException : CancellationException("Generation cancelled by user")
