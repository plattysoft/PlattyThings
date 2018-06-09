package com.plattysoft.mpu6050_sample

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.plattysoft.mpu6050.Mpu6050

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

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gyroscope = Mpu6050.open()
        // Values are on accelXYZ and gyroXYZ
        while(true) {
            Log.d(TAG, "Accel: x:" + gyroscope.accelX + " y;" + gyroscope.accelY + " z:" + gyroscope.accelZ)
            Log.d(TAG, "Gyro: x:" + gyroscope.gyroX + " y;" + gyroscope.gyroY + " z:" + gyroscope.gyroZ)
        }
        gyroscope.close()
    }
}
