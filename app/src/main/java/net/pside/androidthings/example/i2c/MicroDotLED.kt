package net.pside.androidthings.example.i2c

import android.support.annotation.IntDef
import com.google.android.things.pio.I2cDevice

class MicroDotLED(device: I2cDevice, @MatrixType matrixType: Int) {

    companion object {
        const val WIDTH = 5
        const val HEIGHT = 7

        const val CMD_UPDATE = 0x0C

        const val CMD_MATRIX_1 = 0x01
        const val CMD_MATRIX_2 = 0x0E

        const val MATRIX_1_DECIMAL_POINT = 6
        const val MATRIX_2_DECIMAL_POINT = 7

        @IntDef(CMD_MATRIX_1.toLong(), CMD_MATRIX_2.toLong())
        @Retention(AnnotationRetention.SOURCE)
        annotation class MatrixType
    }

    private val device: I2cDevice

    private var matrixType: Int

    private var matrix: ByteArray

    init {
        this.device = device
        this.matrixType = matrixType
        this.matrix = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)

    }

    fun setDecimalPoint(bit: Boolean) {
        when (matrixType) {
            CMD_MATRIX_1 -> {
                if (bit) {
                    matrix[MATRIX_1_DECIMAL_POINT].toInt().or(0b10000000)
                } else {
                    matrix[MATRIX_1_DECIMAL_POINT].toInt().and(0b01111111)
                }
            }
            CMD_MATRIX_2 -> {
                if (bit) {
                    matrix[MATRIX_2_DECIMAL_POINT].toInt().or(0b01000000)
                } else {
                    matrix[MATRIX_2_DECIMAL_POINT].toInt().and(0b10111111)
                }
            }
        }
    }

    fun set(data: IntArray) {
        for (y in 0..HEIGHT - 1) {
            setRow(y, data[y])
        }
    }

    fun setRow(row: Int, data: Int) {
        for (x in 0..WIDTH - 1) {
            setPixel(x, row, data.and(1.shl(WIDTH - 1 - x)) > 0)
        }
    }

    fun setColumn(column: Int, data: Int) {
        for (y in 0..HEIGHT - 1) {
            setPixel(column, y, data.and(1.shl(y)) > 0)
        }
    }

    fun setPixel(x: Int, y: Int, bit: Boolean) {
        when (matrixType) {
            CMD_MATRIX_1 -> {
                val b = (0b1 shl x)
                if (bit) {
                    matrix[y] = matrix[y].toInt().or(b).toByte()
                } else {
                    matrix[y] = matrix[y].toInt().and(b.inv()).toByte()
                }
            }
            CMD_MATRIX_2 -> {
                val b = (0b1 shl y)
                if (bit) {
                    matrix[x] = matrix[x].toInt().or(b).toByte()
                } else {
                    matrix[x] = matrix[x].toInt().and(b.inv()).toByte()
                }
            }
        }
    }

    fun clear() {
        fill(false)
    }

    fun fill(bit: Boolean) {
        matrix.fill(if (bit) 1 else 0)
        update()
    }

    fun update() {
        device.writeRegBuffer(matrixType, matrix, matrix.size)
        device.writeRegByte(CMD_UPDATE, 0x01)
    }

}
