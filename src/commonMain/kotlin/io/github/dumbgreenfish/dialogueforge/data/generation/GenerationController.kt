package io.github.dumbgreenfish.dialogueforge.data.generation

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface GenerationController {
    val activeConversationIds: StateFlow<Set<String>>
    val changedConversationIds: SharedFlow<String>
    fun start(request: GenerationRequest): Boolean
    fun cancel(conversationId: String)
    fun cancelAll()
    fun interruptAll()
}
