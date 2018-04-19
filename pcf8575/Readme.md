This is a driver for the PCF8575 GPIO Extension board

![Image of PCB8575]
(https://github.com/plattysoft/PlattyThings/tree/master/pcf8575/PCF8575.jpg)

It is compatible with Android Things DP 8

# Gradle configuration

Pending to update to Jcenter, so for now you need to add a custom repository

```gradle
repositories {
    [...]
    maven {
        url  "https://dl.bintray.com/plattysoft/PlattyThings"
    }
}
```
And in your project specific build.gradle dependencies

```gradle
dependencies {
    [...]
    implementation (group: 'com.plattysoft.things', name: 'pcf8575', version: '0.8.1', ext: 'aar', classifier: '')
}
```

# Usage

You can open the driver providing bus and address or use the defaults (if you have not touched the A0-A2 pins)

```kotlin
private val mGpioBoard = Pcf8575.open()
```

Once the driver is created, you can open GPIO pins by name and use them like any other GPIO

```kotlin
// Opening a GPIO for an LED
val led = mGpioBoard.openGpio("P00")
led.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
led.setValue(true)
    
// Opening a GPIO for reading
val button = mGpioBoard.openGpio("P00")
button.setDirection(Gpio.DIRECTION_IN)
val value = button.getValue()
```

If you want to receive GPIO callbacks, you need to configure the interrupt pin, otherwise they will not work
Note that the interrupt pin is shared, so it is very noisy, you may want to implement some debouncing and filtering (or open it using the Button or an InputDriver)
```kotlin
// First configure the interrupt pin
mGpioBoard.setInterrupt("BCM23")

// Now we can register a callback
button.registerGpioCallback(GpioCallback() {
    Log.d("PCF8575","Read value (INT): "+it.getValue())
    true
})
```

