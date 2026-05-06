package io.github.dumbgreenfish.dialogueforge.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.KoinViewModel
import kotlin.random.Random

@KoinViewModel
class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    fun handle(intent: HomeIntent) = when (intent) {
        is HomeIntent.ShowRandomNumber -> {
            _state.value = _state.value.copy(alert = "Random number: ${Random.nextInt(100)}")
        }
    }
}