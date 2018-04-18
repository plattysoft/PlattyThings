# PlattyThings
Several Android Things drivers with samples.

* SSD1306: Driver for the OLED screen when wired as SPI (current contrib_driver only supports I2C)
  * Future work: integrate with the contrib_driver to support both
* PCF8591: Driver for the Analog to Digital converter (original driver did not support analog writing or independent channel reading)
* PCF8575: Driver for the GPIO extension board (16 GPIO pins)
* L298N: Driver for the dual motor controller L298N
* ADXL345: Test project with a driver for the 3-axis accelerometer (from https://github.com/cagdasc/AndroidThings-ADXL345)
* HMC5883L: Test project with a driver for the 3-axis magnetometer (from https://github.com/m-thu/android-things/tree/master/hmc5883l)
* Led Matrix test: A sample test project for a 8x8 LED matrix over SPI
 
