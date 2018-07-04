package com.plattysoft.rc522_test

import android.app.Activity
import android.os.Bundle
import com.galarzaa.androidthings.Rc522
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.SpiDevice



private val TAG = MainActivity::class.java.simpleName

class MainActivity : Activity() {

    lateinit var mRc522: Rc522

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pioService = PeripheralManager.getInstance()
        val spiDevice = pioService.openSpiDevice("SPI0.0")
        val resetPin = pioService.openGpio("BCM25")
        mRc522 = Rc522(spiDevice, resetPin)

        readRFid()
    }

    private fun readRFid() {
        while (true) {
            var success = mRc522.request()
            if (!success) {
                continue
            }
            success = mRc522.antiCollisionDetect()
            if (!success) {
                continue
            }
            val uid = mRc522.getUid()
            mRc522.selectTag(uid)
            break
        }
        // Factory Key A:
        val key = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte())
        // Data that will be written
        val newData = byteArrayOf(0x0F, 0x0E, 0x0D, 0x0C, 0x0B, 0x0A, 0x09, 0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00)
        // Get the address of the desired block
        val block = Rc522.getBlockAddress(3, 2)
        //We need to authenticate the card, each sector can have a different key
        var result = mRc522.authenticateCard(Rc522.AUTH_A, block, key)
        if (!result) {
            //Authentication failed
            return
        }
        result = mRc522.writeBlock(block, newData)
        if (!result) {
            //Could not write, key might have permission to read but not write
            return
        }
        //Buffer to hold read data
        val buffer = ByteArray(16)
        //Since we're still using the same block, we don't need to authenticate again
        result = mRc522.readBlock(block, buffer)
        if (!result) {
            //Could not read card
            return
        }
        //Stop crypto to allow subsequent readings
        mRc522.stopCrypto()


    }
    override fun onDestroy() {
        super.onDestroy()
    }

}
