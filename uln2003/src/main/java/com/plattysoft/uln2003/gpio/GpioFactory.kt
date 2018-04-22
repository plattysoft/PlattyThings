package com.plattysoft.uln2003.gpio

import com.google.android.things.pio.PeripheralManager

open class GpioFactory {
    open fun openGpio(name: String)
            = PeripheralManager.getInstance().openGpio(name)
}