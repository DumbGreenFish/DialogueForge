package io.github.dumbgreenfish.dialogueforge.data.repository.dialogue

import io.github.dumbgreenfish.dialogueforge.data.config.DatabaseConfig
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole
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
        val now = Clock.System.now().toEpochMilliseconds()
        val conversation = ConversationEntity(
            id = Uuid.random().toString(),
            characterId = characterId,
            title = "",
            createdAt = now,
            updatedAt = now,
        )
        val greetingMessage = greeting.takeIf { it.isNotBlank() }?.let {
            MessageEntity(
                id = Uuid.random().toString(),
                conversationId = conversation.id,
                role = MessageRole.Assistant.wire,
                text = it,
                timestamp = now,
                orderInConversation = 0,
            )
        }
        return db.conversationDao().getOrCreate(conversation, greetingMessage)
    }

    override suspend fun addMessage(conversationId: String, role: String, text: String): MessageEntity {
        val now = Clock.System.now().toEpochMilliseconds()
        val message = MessageEntity(
            id = Uuid.random().toString(),
            conversationId = conversationId,
            role = role,
            text = text,
            timestamp = now,
            orderInConversation = 0,
        )
        return db.messageDao().insertWithOrder(message)
    }

    override suspend fun deleteMessage(id: String) {
        db.messageDao().deleteById(id)
    }

    override suspend fun updateMessage(id: String, text: String) {
        db.messageDao().updateText(id, text)
    }
}
