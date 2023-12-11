package com.nemo.veloon.domain

sealed class InAppException : Exception() {
    sealed class ActivityMeasurementException : InAppException() {
        object AnotherExerciseIsInProgress : ActivityMeasurementException()
        object BikingMeasurementIsNotSupported : ActivityMeasurementException()
    }
}