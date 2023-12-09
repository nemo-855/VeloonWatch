package com.nemo.veloon.ui.home

sealed class HomeState {
    object InPreparation : HomeState()

    class InProgress(
        val pace: Double,
    ) : HomeState()
}
