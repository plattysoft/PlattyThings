#DRiver for accelerometer ADXL345

Forked from: https://github.com/cagdasc/AndroidThings-ADXL345

Simply added upload to bintray configuration

## Gradle configuration

Add PlattyThings to your repositories

```gradle
repositories {
    [...]
    maven {
        url  "https://dl.bintray.com/plattysoft/PlattyThings"
    }
}
```

And this library as a dependency

```gradle
dependencies {
    [...]
    implementation (group: 'com.plattysoft.things', name: 'adxl345', version: '1.0.0', ext: 'aar', classifier: '')
}
```

# Sample usage:

```kotlin
val adxl345 = ADXL345(I2cUtils.getBus())
Log.i(TAG,"Accel X"+adxl345.accelerationX)
Log.i(TAG,"Accel Y"+adxl345.accelerationY)
Log.i(TAG,"Accel Z"+adxl345.accelerationZ)
adxl345.close()
```
