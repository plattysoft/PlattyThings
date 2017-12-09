package com.plattysoft.iotdisplaysample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import java.io.IOException;

import android.util.Log;

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
        for (int i=0; i<mScreen.getLcdWidth(); i++) {
            for (int j=0; j<mScreen.getLcdHeight(); j++) {
                mScreen.setPixel(i, j, ((i <8 && j<8)||(i%8==0 ||j%8==0)) ? Ssd1306.ColorCode.BLACK : Ssd1306.ColorCode.WHITE);
            }
        }
//        mScreen.drawString(0,0,"Hello SDD1306", null);
        Bitmap plattyBitmap = ((BitmapDrawable)getDrawable(R.drawable.platty)).getBitmap();
        setBmpData(mScreen, plattyBitmap);
        mScreen.show();
//        mScreen.startScrolling();
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
                        if (pixel != -1) { // Only draw white pixels
                            mScreen.setPixel(x, y + k, Ssd1306.ColorCode.WHITE);
                        }
                        else {
                            mScreen.setPixel(x, y + k, Ssd1306.ColorCode.BLACK);
                        }
                    }
                }
            }
        }
    }
}
