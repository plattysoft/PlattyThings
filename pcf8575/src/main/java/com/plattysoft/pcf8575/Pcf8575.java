/*
 * Copyright 2018 Raul Portales
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.plattysoft.pcf8575;

import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Android Things driver for the Pcf8575 GPIO extension board
 * by Raul Portales
 */
public class Pcf8575 implements AutoCloseable {

    /**
     * Device base address
     */
    private static final int BASE_ADDRESS = 0x20;

    /**
     * Names of the GPIO pins provided by the component
     */
    private final String [] mPinNames = {
            "P00", "P01", "P02", "P03", "P04", "P05", "P06", "P07",
            "P10", "P11", "P12", "P13", "P14", "P15", "P16", "P17"
    };

    /**
     * Gpio of the Android Things board that is connected to the interrupt signal
     */
    private Gpio mInterruptGpio;
    /**
     * Reference to the I2C device
     */
    private final I2cDevice mI2cDevice;
    /**
     * Map of GPIO objects based on their name, to be initialized on construction
     */
    private final Map<String, Pcf8575Gpio> mGpios = new HashMap<>();
    /**
     * Set of Gpio pins that are registered for Gpio callbacks.
     * They will be notified whenever the interrupt GPIO changes
     */
    private final Set<Pcf8575Gpio> mRegisteredPins = new HashSet<>();

    /**
     * Latest value of the GPIO writes. It is used to write the same value on all the pins that are
     * not the one we want to modify
     */
    protected byte[] mCurrentGpioValues = new byte[] {0x00, 0x00};

    /**
     * GPIO callback for the interrupt that iterates over the registered GPIO objects
     */
    private final GpioCallback mCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            try {
                for (Pcf8575Gpio registeredPin : mRegisteredPins) {
                    registeredPin.validateInterrupt();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void onGpioError(Gpio gpio, int error) {
            Log.w("PCF8575", gpio + ": Error event " + error);
        }
    };

    protected Pcf8575(I2cDevice i2cDevice) {
        mI2cDevice = i2cDevice;

        for (int i = 0; i< mPinNames.length; i++) {
            mGpios.put(mPinNames[i], new Pcf8575Gpio(mPinNames[i], i, this));
        }
    }

    /**
     * Configures interrupt pin. Calling this method is mandatory to receive GPIO callbacks
     * @param interruptPin The name of the Android Things connected board
     * @throws IOException
     */
    public void setInterrupt(String interruptPin) throws IOException {
        PeripheralManager peripheralManager = PeripheralManager.getInstance();
        mInterruptGpio = peripheralManager.openGpio(interruptPin);
        mInterruptGpio.setDirection(Gpio.DIRECTION_IN);
        mInterruptGpio.setActiveType(Gpio.ACTIVE_LOW);
        mInterruptGpio.setEdgeTriggerType(Gpio.EDGE_BOTH);
        mInterruptGpio.registerGpioCallback(mCallback);
    }

    /**
     * Create a Pcf8575 with the default address on the
     * default I2C bus.
     *
     * @return new Pcf8575
     */
    public static Pcf8575 open() throws IOException {
        return open(0, getBus());
    }

    /**
     * Create a Pcf8575 with the given bus on the
     * default address.
     *
     * @param bus     the I2C bus the mI2cDevice is on
     * @return new Pcf8575
     */
    public static Pcf8575 open(String bus) throws IOException {
        return open(0, bus);
    }

    /**
     * Create a Pcf8575 with the given address on the
     * default I2C bus.
     *
     * @param address value of A0-A2 for your Pcf8575
     * @return new Pcf8575
     */
    public static Pcf8575 open(int address) throws IOException {
        return open(address, getBus());
    }

    /**
     * Create a Pcf8575 with the given address on the
     * given bus.
     *
     * @param address value of A0-A2 for your Pcf8575
     * @param bus     the I2C bus the I2cDevice is on
     * @return new Pcf8575
     */
    public static Pcf8575 open(int address, String bus) throws IOException {
        int fullAddress = BASE_ADDRESS + address;

        PeripheralManager peripheralManager = PeripheralManager.getInstance();
        I2cDevice i2cDevice = peripheralManager.openI2cDevice(bus, fullAddress);

        return new Pcf8575(i2cDevice);
    }

    protected static String getBus() {
        PeripheralManager peripheralManager = PeripheralManager.getInstance();
        List<String> deviceList = peripheralManager.getI2cBusList();
        if (deviceList.isEmpty()) {
            return "I2C1";
        } else {
            return deviceList.get(0);
        }
    }

    public void close() throws IOException {
        if (mI2cDevice != null) {
            mI2cDevice.close();
        }
        if (mInterruptGpio != null) {
            mInterruptGpio.close();
            mInterruptGpio.unregisterGpioCallback(mCallback);
        }
    }

    /**
     * Opens one of the GPIO ports by name
     * @param pinName The name of the GPIO pin (P00-P07, P10-P17)
     * @return A GPIO object that can be used to handle that pin
     */
    public Gpio openGpio(String pinName) {
        // Make a GPIO and handle it
        return mGpios.get(pinName);
    }

    /**
     * Reads the value of a GPIO by position, provided for completion
     * @param gpio the index of the GPIO to be read (0-15)
     * @return if the value is HIGH (true) or LOW (false)
     * @throws IOException
     */
    public boolean getValue(int gpio) throws IOException {
        // Ask for a read, return the requested value
        byte[] buffer = new byte[2];
        mI2cDevice.read(buffer, 2);
        int targetGpioBlock = 0;
        int shiftAmount = gpio;
        if (gpio < 8) {
            targetGpioBlock = 0;
        }
        else {
            shiftAmount = gpio - 8;
            targetGpioBlock = 1;
        }
        byte command = (byte) (0x01<<shiftAmount);
        return (buffer[targetGpioBlock] & command) == command;
    }

    /**
     *  Writes a value to one pin by position. The other pins stay the same
     * @param gpio the index of the GPIO to be written
     * @param value if it is to be set to HIGH (true) or LOW (false)
     * @throws IOException
     */
    public void setValue(int gpio, boolean value) throws IOException {
        int shiftAmount = gpio;
        int targetGpioBlock = 0;
        if (gpio < 8) {
            targetGpioBlock = 0;
        }
        else {
            shiftAmount = gpio - 8;
            targetGpioBlock = 1;
        }
        byte command = (byte) (0x01<<shiftAmount);
        if (value) {
            mCurrentGpioValues[targetGpioBlock] = (byte) (mCurrentGpioValues[targetGpioBlock] | command);
        }
        else {
            mCurrentGpioValues[targetGpioBlock] = (byte) (mCurrentGpioValues[targetGpioBlock] & ~command);
        }
        mI2cDevice.write(mCurrentGpioValues, 2);
    }

    void unregisterGpioCallback(Pcf8575Gpio pcf8575Gpio) {
        mRegisteredPins.remove(pcf8575Gpio);
    }

    void registerGpioCallback(Pcf8575Gpio pcf8575Gpio) {
        mRegisteredPins.add(pcf8575Gpio);
    }
}