package com.plattysoft.mypca9685_sample

import android.app.Activity
import android.os.Bundle
import com.plattysoft.pca9685.PCA9685
import com.plattysoft.pca9685.ServoUnderPca9685
import java.util.*

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

        // Open the PCa9685
        val pca9685 = PCA9685()
        // Open a servo by accessing the PWM entry
        val pwmChannel0 = pca9685.openPwm(0)
        val servo = ServoUnderPca9685(pwmChannel0)
        servo.setAngleRange(0.0, 180.0)
        servo.setPulseDurationRange(0.78, 2.6)
        // Check that it works
        val t = Timer()
        var value = 0.0
        val INCREMENT = 20
        t.schedule(object : TimerTask() {
            override fun run() {
                servo.angle = value
                value += INCREMENT
                if (value > 180) {
                    value = 0.0
                }
            }
        }, 0, 1000)
    }
}
