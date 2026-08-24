package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole

internal const val STREAMING_MESSAGE_ID_PREFIX = "streaming-"
private const val STREAMING_MESSAGE_TIMESTAMP = 0L

internal fun messagesForDisplay(state: DialogueState): List<Message> {
    val streamingMessage = state.streamingMessage
    if (streamingMessage != null) {
        val persistedMessages = state.messages.dropCurrentStreamingCopy(streamingMessage)
        return listOf(streamingMessage) + persistedMessages
    }

    val newestMessage = state.messages.firstOrNull()
    if (
        state.isGenerating &&
        newestMessage?.role == MessageRole.Assistant
    ) {
        return listOf(newestMessage.copy(timestamp = STREAMING_MESSAGE_TIMESTAMP)) + state.messages.drop(1)
    }

    return state.messages
}

private fun List<Message>.dropCurrentStreamingCopy(streamingMessage: Message): List<Message> {
    val newestMessage = firstOrNull()
    return if (
        newestMessage?.role == MessageRole.Assistant &&
        newestMessage.text == streamingMessage.text
    ) {
        drop(1)
    } else {
        this
    }
}

internal fun streamingMessageId(conversationId: String): String =
    "$STREAMING_MESSAGE_ID_PREFIX$conversationId"

internal fun Message.isStreamingMessage(): Boolean = id.startsWith(STREAMING_MESSAGE_ID_PREFIX)
