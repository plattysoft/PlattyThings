package com.plattysoft.ledmatrix_test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.PeripheralManager;
import com.nilhcem.androidthings.driver.max72xx.LedControl;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int NUM_DISPLAYS = 2;
    private static final long DELAY = 300;

    private LedControl ledControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<String> list = PeripheralManager.getInstance().getSpiBusList();
        for (String bus : list) {
            Log.d("Bus:", bus);
        }
        setupOledDisplay();
        startRandomDisplay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyOledDisplay();
    }

    private void startRandomDisplay() {
        final Random random = new Random();
        final int[] randomValues = new int[8*NUM_DISPLAYS];
        for (int i=0; i<randomValues.length; i++) {
            randomValues[i] = 0;
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Make a new random number, push the values of the array
                for (int i=randomValues.length-1; i>0; i--) {
                    randomValues[i] = randomValues[i-1];
                }
                randomValues[0] = random.nextInt(9);
                int currentValue = 0;
                // Display each new column
                for (int device = NUM_DISPLAYS-1; device >= 0; device--) {
                    for (int row = 0; row <8; row++) {
                        for (int column = 0; column < 8; column++) {
                            try {
                                ledControl.setLed(device, row, column, randomValues[currentValue] > column);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        currentValue++;
                    }
                }
            }
        }, DELAY, DELAY);
    }

    private void setupOledDisplay() {
        try {
            ledControl = new LedControl("SPI3.0", NUM_DISPLAYS); // second parameter is the number of chained matrices. Here, we only use 1 LED matrix module (8x8).
            for (int i = 0; i < ledControl.getDeviceCount(); i++) {
                ledControl.setIntensity(i, 1);
                ledControl.shutdown(i, false);
                ledControl.clearDisplay(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "OLED screen activity created");
    }

    private void destroyOledDisplay() {
        if (ledControl != null) {
            try {
                ledControl.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing SSD1306", e);
            } finally {
                ledControl = null;
                ledControl = null;
            }
        }
    }

}
