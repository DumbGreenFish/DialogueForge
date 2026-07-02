package io.github.dumbgreenfish.dialogueforge.data.repository.settings

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@Single
class ModelNameProvider(
    private val settings: SettingsRepository,
    notifier: PresetsSavedNotifier,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _modelName = MutableStateFlow("")
    val modelName: StateFlow<String> = _modelName.asStateFlow()

    init {
        scope.launch {
            _modelName.value = settings.getModel()
            notifier.version.collect {
                _modelName.value = settings.getModel()
            }
        }
    }
}
