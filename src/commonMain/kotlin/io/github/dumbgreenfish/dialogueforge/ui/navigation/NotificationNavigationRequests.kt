package io.github.dumbgreenfish.dialogueforge.ui.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

@Single
class NotificationNavigationRequests {
    private val _characterId = MutableStateFlow<String?>(null)
    val characterId: StateFlow<String?> = _characterId.asStateFlow()

    fun openCharacter(characterId: String) {
        _characterId.value = characterId
    }

    fun consume(characterId: String) {
        if (_characterId.value == characterId) _characterId.value = null
    }
}
