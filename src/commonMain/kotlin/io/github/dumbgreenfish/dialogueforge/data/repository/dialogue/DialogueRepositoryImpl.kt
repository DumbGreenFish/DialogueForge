package io.github.dumbgreenfish.dialogueforge.data.repository.dialogue

import io.github.dumbgreenfish.dialogueforge.data.config.DatabaseConfig
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import org.koin.core.annotation.Single

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
@Single(binds = [DialogueRepository::class])
class DialogueRepositoryImpl(dbConfig: DatabaseConfig) : DialogueRepository {

    private val db = dbConfig.mainDatabase()

    override fun getMessages(conversationId: String): Flow<List<MessageEntity>> =
        db.messageDao().getByConversation(conversationId)

    override suspend fun getOrCreateConversation(characterId: String, greeting: String): ConversationEntity {
        val existing = db.conversationDao().getByCharacterId(characterId).firstOrNull()
        if (existing != null) return existing

        val now = Clock.System.now().toEpochMilliseconds()
        val conversation = ConversationEntity(
            id = Uuid.random().toString(),
            characterId = characterId,
            title = "",
            createdAt = now,
            updatedAt = now,
        )
        db.conversationDao().insert(conversation)

        if (greeting.isNotBlank()) {
            addMessage(
                conversationId = conversation.id,
                role = "assistant",
                text = greeting,
            )
        }

        return conversation
    }

    override suspend fun addMessage(conversationId: String, role: String, text: String): MessageEntity {
        val now = Clock.System.now().toEpochMilliseconds()
        val order = db.messageDao().countByConversation(conversationId)
        val message = MessageEntity(
            id = Uuid.random().toString(),
            conversationId = conversationId,
            role = role,
            text = text,
            timestamp = now,
            orderInConversation = order,
        )
        db.messageDao().insert(message)
        db.conversationDao().touch(conversationId, now)
        return message
    }

    override suspend fun deleteMessage(id: String) {
        db.messageDao().deleteById(id)
    }

    override suspend fun updateMessage(id: String, text: String) {
        db.messageDao().updateText(id, text)
    }
}
