package com.plattysoft.iotdisplaysample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import java.io.IOException;

import android.util.Log;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.plattysoft.androidthings.ssd1306.Ssd1306;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String BUS = "SPI0.0";
    private Ssd1306 mScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupOledDisplay();
        try {
            useDisplay();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void useDisplay() throws IOException {
        Fuel.get("https://chart.googleapis.com/chart?chs=128x64&cht=qr&chl=Hello%20world&choe=UTF-8&chld=M|1").responseString(new Handler<String>() {
            @Override
            public void failure(Request request, Response response, FuelError error) {
                //do something when it is failure
            }

            @Override
            public void success(Request request, Response response, String data) {
                //do something when it is successful
                byte[] dataBytes = response.getData();
                Bitmap qrcode = BitmapFactory.decodeByteArray(dataBytes, 0, dataBytes.length);
                setBmpData(mScreen, qrcode);
                mScreen.show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyOledDisplay();
    }

    private void setupOledDisplay() {
        try {
            mScreen = Ssd1306.openSpi(BUS, "BCM14", "BCM15", 128, 64);
        } catch (IOException e) {
            Log.e(TAG, "Error while opening screen", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "OLED screen activity created");
    }

    private void destroyOledDisplay() {
        if (mScreen != null) {
            try {
                mScreen.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing SSD1306", e);
            } finally {
                mScreen = null;
            }
        }
    }

    private static final int GRADIENT_CUTOFF = 170; // Tune for gradient picker on grayscale images.

    public static void setBmpData(Ssd1306 mScreen, Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int bmpByteSize = (int) Math.ceil((double) (width * ((height / 8) > 1 ? (height / 8) : 1)));

        // Each byte stored in memory represents 8 vertical pixels.  As such, you must fill the
        // memory with pixel data moving vertically top-down through the image and scrolling
        // across, while appending the vertical pixel data by series of 8.

        for (int y = 0; y < height; y += 8) {
            for (int x = 0; x < width; x++) {
                int bytePos = x + ((y / 8) * width);

                for (int k = 0; k < 8; k++) {
                    if ((k + y < height) && (bytePos < bmpByteSize)) {
                        int pixel = bmp.getPixel(x, y + k);
                        mScreen.setPixel(x, y + k, pixel == -1);
                    }
                }
            }
        }
    }
}
