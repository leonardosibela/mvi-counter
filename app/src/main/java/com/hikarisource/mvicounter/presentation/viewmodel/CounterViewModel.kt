package com.hikarisource.mvicounter.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

class CounterViewModel : ViewModel() {

    companion object {
        const val INITIAL_VALUE = 0
        const val SMALLEST_VALUE = 0
        const val LARGEST_VALUE = 10
        const val DECREMENT_AMOUNT = 1
        const val INCREMENT_AMOUNT = 1
    }

    private var currentJob: Job? = null

    var state by mutableStateOf(CounterState())
        private set

    private val _uiEvent = Channel<CounterUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: CounterEvent) {
        viewModelScope.launch { _uiEvent.send(CounterUiEvent.Nothing) }
        currentJob?.cancel()
        when (event) {
            CounterEvent.Decrease -> onDecreaseClicked()
            CounterEvent.Increase -> onIncreaseClicked()
            CounterEvent.DecreaseAutomatically -> onDecreaseAutomaticallyClicked()
            CounterEvent.IncreaseAutomatically -> onIncreaseAutomaticallyClicked()
        }
    }

    private fun onDecreaseClicked() {
        stopCurrentJob()
        var currentNumber = state.number
        if (currentNumber == SMALLEST_VALUE) return
        currentNumber -= DECREMENT_AMOUNT
        state = CounterState(currentNumber)
        if (currentNumber == SMALLEST_VALUE) {
            viewModelScope.launch { _uiEvent.send(CounterUiEvent.BeginReached) }
        }
    }

    private fun onIncreaseClicked() {
        stopCurrentJob()
        var currentNumber = state.number
        if (currentNumber == LARGEST_VALUE) return
        currentNumber += INCREMENT_AMOUNT
        state = CounterState(currentNumber)
        if (currentNumber == LARGEST_VALUE) {
            viewModelScope.launch { _uiEvent.send(CounterUiEvent.EndReached) }
        }
    }

    private fun onIncreaseAutomaticallyClicked() = viewModelScope.launch {
        stopCurrentJob()
        currentJob = this.coroutineContext.job
        var currentNumber = state.number
        while (currentNumber != LARGEST_VALUE) {
            currentNumber++
            state = CounterState(
                number = currentNumber,
                isIncreaseAutomaticallyEnable = false,
                isDecreaseAutomaticallyEnable = true,
                isIncreaseEnable = false,
                isDecreaseEnable = true
            )
            if (currentNumber == LARGEST_VALUE) {
                _uiEvent.send(CounterUiEvent.EndReached)
            }
            delay(1_000)
        }
        currentJob = null
    }

    private fun onDecreaseAutomaticallyClicked() = viewModelScope.launch {
        stopCurrentJob()
        currentJob = this.coroutineContext.job
        var currentNumber = state.number
        while (currentNumber != SMALLEST_VALUE) {
            currentNumber--
            state = CounterState(
                number = currentNumber,
                isDecreaseAutomaticallyEnable = false,
                isIncreaseAutomaticallyEnable = true,
                isDecreaseEnable = false,
                isIncreaseEnable = true
            )
            if (currentNumber == SMALLEST_VALUE) {
                _uiEvent.send(CounterUiEvent.BeginReached)
            }
            delay(1_000)
        }
        currentJob = null
    }

    private fun stopCurrentJob() {
        currentJob?.cancel()
        currentJob = null
    }

    sealed interface CounterEvent {
        object Increase : CounterEvent
        object Decrease : CounterEvent
        object IncreaseAutomatically : CounterEvent
        object DecreaseAutomatically : CounterEvent
    }

    sealed interface CounterUiEvent {
        object Nothing : CounterUiEvent
        object EndReached : CounterUiEvent
        object BeginReached : CounterUiEvent
    }

    data class CounterState(
        val number: Int = INITIAL_VALUE,
        val isDecreaseEnable: Boolean = false,
        val isIncreaseEnable: Boolean = true,
        val isDecreaseAutomaticallyEnable: Boolean = false,
        val isIncreaseAutomaticallyEnable: Boolean = true,
    ) {
        constructor(number: Int) : this(
            number = number,
            isDecreaseEnable = number > SMALLEST_VALUE,
            isIncreaseEnable = number < LARGEST_VALUE,
            isDecreaseAutomaticallyEnable = number > SMALLEST_VALUE,
            isIncreaseAutomaticallyEnable = number < LARGEST_VALUE,
        )
    }
}