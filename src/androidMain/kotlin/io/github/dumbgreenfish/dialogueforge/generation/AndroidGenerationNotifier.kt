package io.github.dumbgreenfish.dialogueforge.generation

import android.app.Application
import android.graphics.BitmapFactory
import io.github.dumbgreenfish.dialogueforge.notification.GenerationNotificationFactory
import io.github.dumbgreenfish.dialogueforge.service.generation.ConversationVisibility
import io.github.dumbgreenfish.dialogueforge.service.generation.GenerationNotifier as LegacyGenerationNotifier
import io.github.dumbgreenfish.dialogueforge.service.generation.GenerationResult as LegacyGenerationResult
import io.github.dumbgreenfish.dialogueforge.service.generation.shouldPostCompletionNotification
import io.github.dumbgreenfish.dialogueforge.service.generation.api.GenerationResult
import io.github.dumbgreenfish.dialogueforge.service.generation.coordination.GenerationNotifier as CanonicalGenerationNotifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@Single(binds = [CanonicalGenerationNotifier::class])
class AndroidGenerationNotifier internal constructor(
    application: Application,
    private val visibility: ConversationVisibility,
    private val notificationGateway: CompletionNotificationGateway,
) : CanonicalGenerationNotifier, LegacyGenerationNotifier {
    private val notifications = GenerationNotificationFactory(application)
    private val preferences = application.getSharedPreferences(PREFERENCES_NAME, Application.MODE_PRIVATE)
    private val notificationLock = Any()

    init {
        CoroutineScope(SupervisorJob() + Dispatchers.Unconfined).launch {
            visibility.state.collectLatest { state ->
                if (state.appVisible) {
                    state.characterId?.let(::cancelForCharacter)
                }
            }
        }
    }

    override fun completed(conversationId: String, result: GenerationResult.Success) {
        synchronized(notificationLock) {
            val visibleState = visibility.state.value
            if (!shouldPostCompletionNotification(
                    appVisible = visibleState.appVisible,
                    visibleCharacterId = visibleState.characterId,
                    completedCharacterId = result.characterId,
                )
            ) return

            val notificationId = nextNotificationId()
            val avatar = result.avatar?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
            rememberNotificationId(result.characterId, notificationId)
            notificationGateway.notify(
                notificationId,
                notifications.completion(
                    notificationId = notificationId,
                    characterId = result.characterId,
                    characterName = result.characterName,
                    response = result.response,
                    avatar = avatar,
                ),
            )
        }
    }

    override fun completed(
        conversationId: String,
        result: LegacyGenerationResult.Success,
    ) {
        completed(
            conversationId = conversationId,
            result = GenerationResult.Success(
                characterId = result.characterId,
                characterName = result.characterName,
                avatar = result.avatar,
                response = result.response,
            ),
        )
    }

    private fun cancelForCharacter(characterId: String) {
        synchronized(notificationLock) {
            val key = notificationIdsKey(characterId)
            preferences.getStringSet(key, emptySet())
                .orEmpty()
                .mapNotNull(String::toIntOrNull)
                .forEach(notificationGateway::cancel)
            preferences.edit().remove(key).commit()
        }
    }

    private fun nextNotificationId(): Int {
        val current = preferences.getInt(KEY_NEXT_NOTIFICATION_ID, FIRST_COMPLETION_NOTIFICATION_ID)
        val next = if (current == Int.MAX_VALUE) FIRST_COMPLETION_NOTIFICATION_ID else current + 1
        preferences.edit().putInt(KEY_NEXT_NOTIFICATION_ID, next).commit()
        return current
    }

    private fun rememberNotificationId(characterId: String, notificationId: Int) {
        val key = notificationIdsKey(characterId)
        val ids = preferences.getStringSet(key, emptySet()).orEmpty().toMutableSet()
        ids += notificationId.toString()
        preferences.edit().putStringSet(key, ids).commit()
    }

    private fun notificationIdsKey(characterId: String) = "$KEY_NOTIFICATION_IDS_PREFIX$characterId"

    private companion object {
        const val FIRST_COMPLETION_NOTIFICATION_ID = 10_000
        const val PREFERENCES_NAME = "generation_notifications"
        const val KEY_NEXT_NOTIFICATION_ID = "next_notification_id"
        const val KEY_NOTIFICATION_IDS_PREFIX = "character_notification_ids:"
    }
}
