package com.plattysoft.adxl345_test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.plattysoft.adxl345.ADXL345;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String bus = "I2C1";

    ADXL345 adxl345 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            adxl345 = new ADXL345(bus);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                getSensorData();
            }
        }, 200, 200);
    }

    private void getSensorData(){
        try {
            Log.i(TAG , " Accel X: "+String.valueOf(adxl345.getAccelerationX())
                    +", Accel Y: "+ String.valueOf(adxl345.getAccelerationY())
                    +", Accel Z: "+ String.valueOf(adxl345.getAccelerationZ()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (adxl345 != null) {
                adxl345.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
