package com.example.practiceflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.Flow

class MyViewModel() : ViewModel() {

    val regularFlow = flow<String> {
        (1..5).forEach {
            delay(1000)
            emit("emit $it")
        }
    }

    val navigationState =
        MutableStateFlow<NavigationState>(
            NavigationState.EmptyNavigationState(0)
        )

    val navigationEvent =
        MutableSharedFlow<Int>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    fun emitNavigationState(destination: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            navigationState
                .value = NavigationState.ViewNavigationState(destination)

        }
    }

    fun emitNavigationEvent(destination: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            navigationEvent.emit(destination)
        }
    }

}