package com.plattysoft.pca9685;

import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by Raul Portales on 28/10/2018.
 */
public class ServoUnderPcs9685Test {

    @Test
    public void testProperDutyCycle() throws IOException {
        PwmUnderPca9685 mockPwm = mock(PwmUnderPca9685.class);
        ServoUnderPca9685 servoInterface = new ServoUnderPca9685(mockPwm);
        servoInterface.setAngleRange(0, 180);
        servoInterface.setPulseDurationRange(1, 2);
        servoInterface.setAngle(90);
        verify(mockPwm).setPwmDutyCycle(7.5);
    }
}
