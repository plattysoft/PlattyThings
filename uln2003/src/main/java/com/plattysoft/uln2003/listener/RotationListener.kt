package com.plattysoft.uln2003.listener

interface RotationListener {
    fun onStarted() {
    }

    fun onFinishedSuccessfully() {
    }

    fun onFinishedWithError(degreesToRotate: Double, rotatedDegrees: Double, exception: Exception) {
    }
}