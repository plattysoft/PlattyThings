# PCF8591 ADC Converter

This is an Android Things driver for PCF8591 Analog to Digital Converter (4 inputs). It is compatible with Android Things DP 8

![Image of PCF8591](https://github.com/plattysoft/PlattyThings/blob/master/pcf8591/pcf8591.jpg)

## Gradle configuration

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
    implementation (group: 'com.plattysoft.things', name: 'pcf8591', version: '0.8.1', ext: 'aar', classifier: '')
}
```

## Usage

You can open the driver providing bus and address or use the defaults (if you have not touched the A0-A2 pins)

```kotlin
private val mPcf8591 = Pcf8591.open()
```

Once the driver is created, you can read the value from any analog input. Values range from 0 to 255 (8 bits of resolution)

```kotlin
// Reading the channel from AIN2
val value = mPcf8591.readValue(2)
// Reading the value from all channels
val allValues = readAllValues()
```

You can also write an analog value to the Analog output, but it does not seem to work properly, it is likely to be a fault on the board. 
If you want to have analog outputs I suggest you use PWM for it instead
```kotlin
mPcf8591.setAnalogOutput(128)
```

