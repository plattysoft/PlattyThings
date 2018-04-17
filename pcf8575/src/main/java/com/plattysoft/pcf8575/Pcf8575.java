/*
 * Copyright 2017 Dave McKelvie
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
import java.util.HashSet;
import java.util.List;

/**
 * Android Things driver for the PCF8591 Analog to Digital Converter
 * http://www.nxp.com/documents/data_sheet/PCF8591.pdf
 */
public class Pcf8575 implements AutoCloseable {

    /**
     * Device base address
     */
    private static final int BASE_ADDRESS = 0x20;

    private final Gpio mInterruptGpio;
    private final I2cDevice mI2cDevice;
    private final HashSet<Pcf8575Gpio> mRegisteredPins = new HashSet<>();

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

    protected byte[] mCurrentGpio = new byte[] {0x00, 0x00};

    protected Pcf8575(Gpio interruptGpio, I2cDevice i2cDevice) throws IOException {
        mInterruptGpio = interruptGpio;
        mInterruptGpio.setDirection(Gpio.DIRECTION_IN);
        mInterruptGpio.setActiveType(Gpio.ACTIVE_LOW);
        mInterruptGpio.setEdgeTriggerType(Gpio.EDGE_BOTH);
        mInterruptGpio.registerGpioCallback(mCallback);
        mI2cDevice = i2cDevice;
    }

    /**
     * Create a Pcf8591 with the default address on the
     * default I2C bus.
     *
     * @return new Pcf8591
     */
    public static Pcf8575 open(String interruptPin) throws IOException {
        return open(interruptPin, 0, getBus());
    }

    /**
     * Create a Pcf8591 with the given bus on the
     * default address.
     *
     * @param bus     the I2C bus the mI2cDevice is on
     * @return new Pcf8591
     */
    public static Pcf8575 open(String interruptPin, String bus) throws IOException {
        return open(interruptPin, 0, bus);
    }

    /**
     * Create a Pcf8591 with the given address on the
     * default I2C bus.
     *
     * @param address value of A0-A2 for your Pcf8591
     * @return new Pcf8591
     */
    public static Pcf8575 open(String interruptPin, int address) throws IOException {
        return open(interruptPin, address, getBus());
    }

    /**
     * Create a Pcf8591 with the given address on the
     * given bus.
     *
     * @param address value of A0-A2 for your Pcf8591
     * @param bus     the I2C bus the mI2cDevice is on
     * @return new Pcf8591
     */
    public static Pcf8575 open(String interruptPin, int address, String bus) throws IOException {
        int fullAddress = BASE_ADDRESS + address;

        PeripheralManager peripheralManager = PeripheralManager.getInstance();
        I2cDevice i2cDevice = peripheralManager.openI2cDevice(bus, fullAddress);
        Gpio interruptGpio = peripheralManager.openGpio(interruptPin);
        return new Pcf8575(interruptGpio, i2cDevice);
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

    public Gpio openGpio(String gpio) {
        // Make a GPIO and handle it
        return new Pcf8575Gpio(this, gpio);
    }

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
            mCurrentGpio[targetGpioBlock] = (byte) (mCurrentGpio[targetGpioBlock] | command);
        }
        else {
            mCurrentGpio[targetGpioBlock] = (byte) (mCurrentGpio[targetGpioBlock] & ~command);
        }
        mI2cDevice.write(mCurrentGpio, 2);
    }

    public void unregisterGpioCallback(Pcf8575Gpio pcf8575Gpio) {
        mRegisteredPins.remove(pcf8575Gpio);
    }

    public void registerGpioCallback(Pcf8575Gpio pcf8575Gpio) {
        mRegisteredPins.add(pcf8575Gpio);
    }
}