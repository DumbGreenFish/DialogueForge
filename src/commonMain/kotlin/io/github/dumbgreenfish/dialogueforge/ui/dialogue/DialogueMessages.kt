package io.github.dumbgreenfish.dialogueforge.ui.dialogue

import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.Message

internal const val STREAMING_MESSAGE_ID_PREFIX = "streaming-"

internal fun messagesForDisplay(state: DialogueState): List<Message> =
    listOfNotNull(state.streamingMessage) + state.messages

internal fun streamingMessageId(conversationId: String): String =
    "$STREAMING_MESSAGE_ID_PREFIX$conversationId"

internal fun Message.isStreamingMessage(): Boolean = id.startsWith(STREAMING_MESSAGE_ID_PREFIX)
