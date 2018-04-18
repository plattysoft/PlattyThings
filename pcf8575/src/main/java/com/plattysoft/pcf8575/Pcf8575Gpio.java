package com.plattysoft.pcf8575;

import android.os.Handler;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raul Portales on 16/04/18.
 */
public class Pcf8575Gpio implements Gpio {

    private final Pcf8575 mController;
    private final List<GpioCallback> mCallbacks = new ArrayList<>();
    private final int mGpioIndex;
    private final String mPinName;
    private boolean mActiveHigh = true;

    public Pcf8575Gpio(String name, int index, Pcf8575 controller) {
        mController = controller;
        mGpioIndex = index;
        mPinName = name;
    }

    @Override
    public String getName() {
        return mPinName;
    }

    @Override
    public void close() throws IOException {
        // Nothing to close, the controller is the one that needs closing
    }

    @Override
    public void setDirection(int direction) throws IOException {
        switch (direction) {
            case Gpio.DIRECTION_IN:
            case Gpio.DIRECTION_OUT_INITIALLY_HIGH :
                // Set this pin as HIGH
                mController.setValue(mGpioIndex, true);
                break;
            case Gpio.DIRECTION_OUT_INITIALLY_LOW:
                // Set this pin as LOW
                mController.setValue(mGpioIndex, false);
                break;
            default:
                throw new InvalidParameterException("Not a valid direction");
        }
    }

    @Override
    public void setEdgeTriggerType(int i) throws IOException {
        // Not used at the moment
    }

    @Override
    public void setActiveType(int i) throws IOException {
        mActiveHigh = (i == ACTIVE_HIGH);
    }

    @Override
    public void setValue(boolean b) throws IOException {
        if (mActiveHigh) {
            // Set it to high or low depending of the active type
            mController.setValue(mGpioIndex, b);
        }
        else {
            mController.setValue(mGpioIndex, !b);
        }
    }

    @Override
    public boolean getValue() throws IOException {
        boolean state = mController.getValue(mGpioIndex);
        if (mActiveHigh) {
            return state;
        }
        else {
            return !state;
        }
    }

    @Override
    public void registerGpioCallback(GpioCallback gpioCallback) throws IOException {
        mCallbacks.add(gpioCallback);
        // To be implemented via the Interrupt
        mController.registerGpioCallback(this);
    }


    @Override
    public void registerGpioCallback(Handler handler, GpioCallback gpioCallback) throws IOException {
        registerGpioCallback(gpioCallback);
    }

    @Override
    public void unregisterGpioCallback(GpioCallback gpioCallback) {
        mCallbacks.remove(gpioCallback);
        if (mCallbacks.isEmpty()) {
            mController.unregisterGpioCallback(this);
        }
    }

    public void validateInterrupt() throws IOException {
        // TODO: Maybe implement some debouncing or filtering
        for (GpioCallback callback : mCallbacks) {
            callback.onGpioEdge(this);
        }
    }
}
