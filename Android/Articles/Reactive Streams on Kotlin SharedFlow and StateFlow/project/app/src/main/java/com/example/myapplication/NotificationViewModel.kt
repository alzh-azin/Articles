package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel() : ViewModel() {

    private var _sharedNotification = MutableSharedFlow<Int>()
    val sharedNotification get() = _sharedNotification

    private var _notificationState = MutableStateFlow<Int>(0)
    val notificationState get() = _notificationState

    fun startSharedNotification() {
        viewModelScope.launch {
            for (i in 0..100) {
                delay(3000)
                _sharedNotification.emit(i)
            }
        }
    }

    fun startNotificationState() {
        viewModelScope.launch {
            for (i in 0..1000) {
                delay(1000)
                _notificationState.value = i
            }
        }
    }
}