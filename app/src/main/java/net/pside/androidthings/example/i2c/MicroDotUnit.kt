package net.pside.androidthings.example.i2c

import android.support.annotation.FloatRange
import com.google.android.things.pio.I2cDevice
import java.util.*

class MicroDotUnit(device: I2cDevice) {

    companion object {
        const val MODE = 0b00011000
        /**
         * 1110 = 35mA, 0000 = 40mA
         */
        const val OPTS = 0b00001110

        const val CMD_MODE = 0x00
        const val CMD_OPTIONS = 0x0D
        const val CMD_BRIGHTNESS = 0x19
    }

    private var leds: ArrayList<MicroDotLED>

    private var device: I2cDevice

    init {
        this.device = device
        this.leds = ArrayList()

        leds.add(MicroDotLED(device, MicroDotLED.CMD_MATRIX_2))
        leds.add(MicroDotLED(device, MicroDotLED.CMD_MATRIX_1))

        initUnit()
    }

    private fun initUnit() {
        device.writeRegByte(CMD_MODE, MODE.toByte())
        device.writeRegByte(CMD_OPTIONS, OPTS.toByte())
        setBrightness(1.0F)
    }

    fun setBrightness(@FloatRange(from = 0.0, to = 1.0) brightness: Float) {
        val data = (brightness * 127).toInt();
        device.writeRegByte(CMD_BRIGHTNESS, data.toByte())
    }

    fun setDecimalPoint(ledIndex: Int, bit: Boolean) {
        leds[ledIndex].setDecimalPoint(bit)
    }

    fun set(ledIndex: Int, data: IntArray) {
        leds[ledIndex].set(data)
    }

    fun fill(ledIndex: Int, bit: Boolean) {
        leds[ledIndex].fill(bit)
    }

    fun fill(bit: Boolean) {
        leds.forEach {
            for (x in 0..MicroDotLED.WIDTH) {
                for (y in 0..MicroDotLED.HEIGHT) {
                    it.setPixel(x, y, bit)
                }
            }
        }
    }

    fun clear() {
        fill(false)
    }

    fun update() {
        leds.forEach { it.update() }
    }

    fun close() {
        device.close()
    }
}
