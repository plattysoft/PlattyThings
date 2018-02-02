/*
 * Based on: https://github.com/hongcheng79/androidthings/tree/master/sparkfun
 *
 * Copyright 2017 Choong Hong Cheng, Lockswitch Sdn Bhd
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

package com.plattysoft.androidthings.ssd1306;

import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.SpiDevice;

import java.io.Closeable;
import java.io.IOException;

/**
 * SSD1306 for Sparkfun OLED Block
 * Port mostly taken from
 * https://github.com/sparkfun/SparkFun_Micro_OLED_Arduino_Library/blob/V_1.0.0/src/SFE_MicroOLED.cpp
 *
 */
class Ssd1306OverSPI extends Ssd1306 implements Closeable {
    private static final String TAG = "SSD1306";

    private static final String DEFAULT_DC_PIN = "BCM14";
    private static final String DEFAULT_RST_PIN = "BCM15";

    private static final int DEFAULT_LCD_WIDTH = 128;
    private static final int DEFAULT_LCD_HEIGHT = 64;

    private final int mLedWidth;
    private final int mLedHeight;
    private final byte[] mBuffer;

    private SpiDevice mSpiDevice;
    private Gpio mDc;
    private Gpio mRst;

    // Protocol constants
    //private static final int DATA_OFFSET = 1;
    private static final int DATA_OFFSET = 0;

    private static final int COMMAND_START_LINE = 0x40;
    private static final int INIT_CHARGE_PUMP = 0x8D;
    private static final int INIT_COMSCAN_DEC = 0xC8;
    private static final int INIT_DISPLAY_NO_OFFSET = 0x0;
    private static final int COMMAND_SET_DISPLAY_OFFSET = 0xD3;
    private static final int INIT_DUTY_CYCLE_1_64 = 0x3F;

    private static final int INIT_MEMORY_ADDRESSING_HORIZ = 0x0;

    private static final int INIT_RESISTER_RATIO = 0x80;
    private static final int INIT_SEGREMAP = 0xA1;
    private static final int INIT_SET_MEMORY_ADDRESSING_MODE = 0x20;

    private static final byte SSD1306_DISPLAY_WRITE = (byte) 0xA4;

    private static final int SETCONTRAST 		= 0x81;

    private static final int DISPLAYALLONRESUME = 0xA4;
    private static final int DISPLAYALLON 		= 0xA5;
    private static final int NORMALDISPLAY 		= 0xA6;
    private static final int INVERTDISPLAY 		= 0xA7;
    private static final int COMMAND_SET_DISPLAY_OFF = 0xAE;
    private static final int COMMAND_SET_DISPLAY_ON = 0xAF;
    private static final int SETDISPLAYOFFSET 	= 0xD3;
    private static final int SETCOMPINS 		= 0xDA;
    private static final int SETVCOMDESELECT	= 0xDB;
    private static final int SETDISPLAYCLOCKDIV = 0xD5;
    private static final int SETPRECHARGE 		= 0xD9;
    private static final int SETMULTIPLEX 		= 0xA8;
    private static final int SETLOWCOLUMN 		= 0x00;
    private static final int SETHIGHCOLUMN 		= 0x10;
    private static final int SETSTARTLINE 		= 0x40;
    private static final int MEMORYMODE 		= 0x20;
    private static final int COMSCANINC 		= 0xC0;
    private static final int COMSCANDEC 		= 0xC8;
    private static final int SEGREMAP 			= 0xA0;
    private static final int CHARGEPUMP 		= 0x8D;
    private static final int EXTERNALVCC 		= 0x01;
    private static final int SWITCHCAPVCC 		= 0x02;

    // Scroll
    private static final int ACTIVATESCROLL 				= 0x2F;
    private static final int DEACTIVATESCROLL 				= 0x2E;
    private static final int SETVERTICALSCROLLAREA 			= 0xA3;
    private static final int RIGHTHORIZONTALSCROLL 			= 0x26;
    private static final int LEFT_HORIZONTALSCROLL 			= 0x27;
    private static final int VERTICALRIGHTHORIZONTALSCROLL	= 0x29;
    private static final int VERTICALLEFTHORIZONTALSCROLL	= 0x2A;

    public void startScrolling() throws IOException {
        command((byte) (0x29));
        command((byte) (0));
        command((byte) (0)); // start page 0
        command((byte) (0)); // 5 frames
        command((byte) (7)); // end page (7)
        command((byte) (1)); // voffset

        command((byte) (0x2F)); // start scroll
    }

    private static final byte[] INIT_PAYLOAD = new  byte[]{
        // Step 1: Start with the display off
        (byte) COMMAND_SET_DISPLAY_OFF,

        // Step 2: Set up the required communication / power settings
        (byte) INIT_SEGREMAP,
        (byte) INIT_COMSCAN_DEC,

        (byte) INIT_DUTY_CYCLE_1_64,

        (byte) SETDISPLAYCLOCKDIV,
        (byte) INIT_RESISTER_RATIO,

        // Step 3: Set display input configuration and start. This will start the display all
        // The addressing mode can only be conigured with the display off
        (byte) INIT_SET_MEMORY_ADDRESSING_MODE,
        (byte) INIT_MEMORY_ADDRESSING_HORIZ,

        (byte) COMMAND_SET_DISPLAY_OFFSET,
        (byte) INIT_DISPLAY_NO_OFFSET,

        (byte) COMMAND_SET_DISPLAY_ON,
        (byte) INIT_CHARGE_PUMP
    };

    Ssd1306OverSPI(String spiName, String dcPin, String rstPin) throws IOException, InterruptedException {
        this(spiName, dcPin, rstPin, DEFAULT_LCD_WIDTH, DEFAULT_LCD_HEIGHT);
    }

    Ssd1306OverSPI(String spiName) throws IOException, InterruptedException {
        this(spiName, DEFAULT_DC_PIN, DEFAULT_RST_PIN);
    }

    Ssd1306OverSPI(String spiName, String dcPin, String rstPin, int width, int height) throws IOException, InterruptedException {
        mDc = new PeripheralManagerService().openGpio(dcPin);
        mRst = new PeripheralManagerService().openGpio(rstPin);
        mLedWidth = width;
        mLedHeight = height;
        mBuffer = new byte[((mLedWidth * mLedHeight) / 8)];

        mSpiDevice = new PeripheralManagerService().openSpiDevice(spiName);

        mSpiDevice.setMode(SpiDevice.MODE0);
        mSpiDevice.setFrequency(10000000);
        mSpiDevice.setBitsPerWord(8);
        mSpiDevice.setBitJustification(false);

        mDc.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
        mDc.setActiveType(Gpio.ACTIVE_LOW);

        mRst.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
        mRst.setActiveType(Gpio.ACTIVE_HIGH);

        mRst.setValue(true);
        Thread.sleep(5000); // VDD (3.3V) goes high at start, lets just chill for 5 ms
        mRst.setValue(false);
        Thread.sleep(10000); // wait 10ms
        mRst.setValue(true);

        command(INIT_PAYLOAD);
    }

    /**
     * Draw pixel in SSD1306
     * @param x
     * @param y
     * @param color WHITE | BLACK | INVERSE
     */
    public void setPixel(int x, int y, ColorCode color)  {
        if (x < 0 || y < 0 || x >= mLedWidth || y >= mLedHeight) {
            // Ignore the out of bound at this point
            return;
        }
        switch (color) {
            case WHITE :
                mBuffer[DATA_OFFSET + x + ((y / 8) * mLedWidth)] |= (1 << y % 8);
                break;
            case BLACK:
                mBuffer[DATA_OFFSET + x + ((y / 8) * mLedWidth)] &= ~(1 << y % 8);
                break;
            case INVERSE:
                mBuffer[DATA_OFFSET + x + ((y / 8) * mLedWidth)] ^= (1 << y % 8);
                break;
        }
    }

    /**
     * LCD Width
     * @return int
     */
    public int getLcdWidth() {
        return mLedWidth;
    }

    /**
     * LCD Height
     * @return int
     */
    public int getLcdHeight() {
        return mLedHeight;
    }

    @Override
    public void close() throws IOException {
        if ( mSpiDevice != null ) {
            try {
                mSpiDevice.close();
                mSpiDevice = null;
            } catch (IOException e) {
                Log.w(TAG, "Unable to close SPI device", e);
            }
        }
        if ( mDc != null ) {
            try {
                mDc.close();
                mDc = null;
            } catch (IOException e) {
                Log.w(TAG, "Unable to close mDc", e);
            }
        }
        if ( mRst != null ) {
            try {
                mRst.close();
                mRst = null;
            } catch (IOException e) {
                Log.w(TAG, "Unable to close mRst", e);
            }
        }
    }

    public void show() {
        try {
            showUsingHorizAddrMode();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        showUsingPageAddrMode();
    }

    private void showUsingPageAddrMode() throws IOException {
        int numPages = mLedHeight / 8;
        for (int i=0;i<numPages; i++) {
            // Set the configuration for the current page
            command((byte) (0xb0|i));
            // Followed by the data in that page
            for ( int j = 0 ; j < mLedWidth; j++ ) {
                data(mBuffer[i*mLedWidth+j]);
            }
        }
    }

    private void showUsingHorizAddrMode() throws IOException {
        // Memory mode
        command((byte) DISPLAYALLONRESUME);
        data(mBuffer);
    }

    /**
     * SSD1306 send command
     * @param c
     * @throws IOException
     */
    private void command(byte c) throws IOException {
        mDc.setValue(true);
        mSpiDevice.write(new byte[]{c},1);
    }

    private void command(byte[] c) throws IOException {
        mDc.setValue(true);
        mSpiDevice.write(c,c.length);
    }

    /**
     * SSD1306 send data
     * @param c
     * @throws IOException
     */
    private void data(byte c) throws IOException {
        mDc.setValue(false);
        mSpiDevice.write(new byte[]{c},1);
    }

    private void data(byte[] c) throws IOException {
        mDc.setValue(false);
        mSpiDevice.write(c,c.length);
    }
}