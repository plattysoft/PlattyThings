package com.plattysoft.androidthings.ssd1306;

import java.io.IOException;

/**
 * Created by Raul Portales on 09/12/17.
 *
 * Base class for SPI and I2C implementation of the module
 * Both classes should have the same interface and common methods
 */

public abstract class Ssd1306 {

    protected Ssd1306() {
        // This class is used as a factory, not intended to be constructed
    }

    public static Ssd1306 openSpi(String spiName, String dcPin, String rstPin) throws IOException, InterruptedException {
        return new Ssd1306OverSPI(spiName, dcPin, rstPin);
    }

    public static Ssd1306 openSpi(String spiName, String dcPin, String rstPin, int width, int height)
            throws IOException, InterruptedException {
        return new Ssd1306OverSPI(spiName, dcPin, rstPin, width, height);
    }

    public abstract int getLcdWidth();

    public abstract int getLcdHeight();

    public abstract void setPixel(int i, int j, boolean on);

    public abstract void show();

    public abstract void close() throws IOException;

}
