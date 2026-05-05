package ru.greenfish.dialogueforge.ui.home

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class HomeViewModel {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    fun handle(intent: HomeIntent) = when (intent) {
        is HomeIntent.SHOW_RANDOM_NUMBER -> {
            _state.value = _state.value.copy(alert = "Random number: ${Random.nextInt(100)}")
        }
    }
}