package com.plattysoft.pca9685;

import com.google.android.things.pio.Pwm;

import java.io.IOException;

/**
 * Created by Raul Portales on 06/06/18.
 */
class PwmUnderPca9685 implements Pwm {
    private final int mChannel;
    private final PCA9685 mController;

    public PwmUnderPca9685(int channel, PCA9685 pca9685) {
        mChannel = channel;
        mController = pca9685;
    }

    @Override
    public void close() throws IOException {
        // Nothing to do here, we need to close the master
    }

    @Override
    public void setPwmDutyCycle(double dutyCycle) throws IOException {
        mController.setPwmDutyCycle(mChannel, (int) dutyCycle);
    }

    @Override
    public void setPwmFrequencyHz(double freq) throws IOException {
        mController.setPwmFreq((int) freq);
    }

    @Override
    public void setEnabled(boolean b) throws IOException {
        // should set it to 0 or remember the last duty cycle settings
    }
}
