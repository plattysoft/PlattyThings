package com.plattysoft.pcf8574_sample

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import com.plattysoft.pcf8575.Pcf8575
import java.util.*
import kotlin.concurrent.timerTask

private val TAG = MainActivity::class.java.simpleName

class MainActivity : Activity() {

    private val mGpioBoard = Pcf8575.open("BCM23")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var current = 12
        var value = true
        val button = mGpioBoard.openGpio("P00")
        button.setDirection(Gpio.DIRECTION_IN)
        button.registerGpioCallback(GpioCallback() {
            Log.d("PCF8575","Read value (INT): "+it.getValue()+" - "+System.currentTimeMillis())
            true
        })
        val t = Timer()
        t.schedule(timerTask {
            mGpioBoard.setValue(current, value)
            current++;
            if (current >= 16) {
                current = 12
                value = !value
            }
            Log.d("PCF8575","Read value: "+button.getValue()+" - "+System.currentTimeMillis())

        }
        , 200, 500)
    }

    override fun onDestroy() {
        super.onDestroy()
        mGpioBoard.close()
    }

}
