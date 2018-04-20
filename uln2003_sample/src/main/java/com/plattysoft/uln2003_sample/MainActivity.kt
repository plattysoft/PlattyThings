package com.plattysoft.uln2003_sample

import android.app.Activity
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import com.plattysoft.uln2003.driver.ULN2003Resolution
import com.plattysoft.uln2003.motor.ULN2003StepperMotor
import com.polidea.androidthings.driver.steppermotor.Direction
import com.polidea.androidthings.driver.steppermotor.listener.RotationListener

/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val in1Pin = "BCM4"
        val in2Pin = "BCM17"
        val in3Pin = "BCM27"
        val in4Pin = "BCM22"

        val stepper = ULN2003StepperMotor(in1GpioId = in1Pin,
                in2GpioId = in2Pin,
                in3GpioId = in3Pin,
                in4GpioId = in4Pin)

        //Perform a rotation and add rotation listener
        stepper.rotate(degrees = 180.0,
                direction = Direction.CLOCKWISE,
                resolutionId = ULN2003Resolution.HALF.id,
                rpm = 2.5,
                rotationListener = object : RotationListener {
                    override fun onStarted() {
                        Log.i(TAG, "rotation started")
                    }
                    override fun onFinishedSuccessfully() {
                        Log.i(TAG, "rotation finished")
                    }
                    override fun onFinishedWithError(degreesToRotate: Double, rotatedDegrees: Double, exception: Exception) {
                        Log.e(TAG, "error, degrees to rotate: {$degreesToRotate}  rotated degrees: {$rotatedDegrees}")
                    }
                })

        // Close the ULN2003StepperMotor when all moves are finished. Otherwise close() will terminate current and pending rotations.
        stepper.close()
    }
}
