package com.example.practiceflow

sealed class NavigationState {
    data class EmptyNavigationState(val destination: Int) : NavigationState()
    data class ViewNavigationState(val destination: Int) : NavigationState()
}
