package com.plattysoft.uln2003.listener

interface StepsListener {
    fun onStarted() {
    }

    fun onFinishedSuccessfully() {
    }

    fun onFinishedWithError(stepsToPerform: Int, performedSteps: Int, exception: Exception) {
    }
}