# L298N Dual DC Motor controller

This is an Android Things driver for L298N Dual motor controler. It is compatible with Android Things DP 8

![Image of L298N](https://github.com/plattysoft/PlattyThings/blob/master/l298n/l298n.jpg)

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
    compile(group: 'com.plattysoft.things', name: 'l298n', version: '0.8.1', ext: 'aar', classifier: '')
}
```

## Usage

You can open the driver providing the GPIO names of the 4 pins used to control the motors

```kotlin
private val mL298n = L298N.open(pin1,pin2,pin3,pin4)
```

Once the driver is created, you set the mode of the 2 motors combined (left and right)
* BACKWARD
* TURN_RIGHT: Turning from a fixed point
* TURN_LEFT: Turning from a fixed point
* SPIN_RIGHT: Spin in the same position
* SPIN_LEFT: Spin in the same position
* STOP
* FORWARD

As an example:
```kotlin
mL298n.setMode(MotorMode.SPIN_RIGHT)
```


