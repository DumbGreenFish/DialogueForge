package io.github.dumbgreenfish.dialogueforge.data.repository.settings

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.annotation.Single

@Single
class PresetsSavedNotifier {
    private val _version = MutableStateFlow(0)
    val version = _version

    fun notifySaved() {
        _version.value++
    }
}
