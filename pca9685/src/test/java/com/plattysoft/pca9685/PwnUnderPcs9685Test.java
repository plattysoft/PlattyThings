package com.plattysoft.pca9685;

import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by Raul Portales on 28/10/2018.
 */
public class PwnUnderPcs9685Test {

    @Test
    public void testProperDutyCycle() throws IOException {
        PCA9685 mockPca9685 = mock(PCA9685.class);
        PwmUnderPca9685 servoInterface = new PwmUnderPca9685(0, mockPca9685);
        for (int i=0; i<100; i++) {
            servoInterface.setPwmDutyCycle(i);
            verify(mockPca9685).setPwmDutyCycle(0,i);
        }
    }
}
