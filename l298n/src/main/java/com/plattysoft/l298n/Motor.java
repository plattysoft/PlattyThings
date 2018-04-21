package com.plattysoft.l298n;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

/**
 * Created by Raul Portales on 16/12/17.
 */

class Motor implements AutoCloseable {
    private Gpio mGpioForward;
    private Gpio mGpioBackward;

    public Motor(String gpioForward, String gpioBackward) throws IOException {
        PeripheralManager service = PeripheralManager.getInstance();

        mGpioForward = service.openGpio(gpioForward);
        mGpioForward.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        mGpioBackward = service.openGpio(gpioBackward);
        mGpioBackward.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
    }

    @Override
    public void close() throws Exception {
        mGpioForward.close();
        mGpioBackward.close();
    }

    public void forward() throws IOException {
        mGpioForward.setValue(true);
        mGpioBackward.setValue(false);
    }

    public void backward() throws IOException {
        mGpioForward.setValue(false);
        mGpioBackward.setValue(true);
    }

    public void stop() throws IOException {
        mGpioForward.setValue(false);
        mGpioBackward.setValue(false);
    }
}
