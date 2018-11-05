package com.plattysoft.pca9685;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by Raul Portales on 28/10/2018.
 */
public class PCA9685Test {

    @Test
    public void testConversionDutyCycle() {
        assertEquals(306, convertPwmDutyCycle(7.5));
    }

    public int convertPwmDutyCycle(double dutyCycle) {
        return (int) (dutyCycle * 4092 / 100);
    }
}
