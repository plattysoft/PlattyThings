This is a driver for the PCF8575 GPIO Extension board

It is compatible with Android Things DP 8

# Gradle configuration

Pending to update to Jcenter

# Usage

You can open the driver providing bus and address or use the defaults (if you have not touched the A0-A2 pins
Once the driver is created, you can open GPIO pins by name on the same fashion as normal ones
If you want to receive GPIO callbacks, you need to configure the interrupt signal
