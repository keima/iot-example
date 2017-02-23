package net.pside.androidthings.example.i2c

import com.google.android.things.pio.PeripheralManagerService
import java.util.*

class MicroDotPhat(i2cName: String) {

    val deviceIds = intArrayOf(0x63, 0x62, 0x61)

    lateinit var units: ArrayList<MicroDotUnit>


    init {
        val service = PeripheralManagerService()
        this.units = ArrayList()
        for ((i, deviceId) in deviceIds.withIndex()) {
            val device = service.openI2cDevice(i2cName, deviceId)
            this.units.add(MicroDotUnit(device))
        }
    }

    fun destroy() {
        units.forEach {
            it.clear()
            it.update()

            it.close()
        }
    }

    fun update() {
        units.forEach {
            it.update()
        }
    }

    fun fill(bit: Boolean) {
        units.forEach {
            it.fill(bit)
        }
    }

    fun fill(index: Int, bit: Boolean) {
        val p = toIndexOfUnitAndLED(index)
        units[p.first].fill(p.second, bit)
    }

    fun setDecimalPoint(index: Int, bit: Boolean) {
        val p = toIndexOfUnitAndLED(index)
        units[p.first].setDecimalPoint(p.second, bit)
    }

    fun set(index: Int, data: IntArray) {
        val p = toIndexOfUnitAndLED(index)
        units[p.first].set(p.second, data)
    }

    private fun toIndexOfUnitAndLED(index: Int): Pair<Int, Int> {
        return Pair(index / 2, index % 2)
    }

}

