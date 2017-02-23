/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.pside.androidthings.example.ui

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService
import kotlinx.android.synthetic.main.activity_main.*
import net.pside.androidthings.example.R
import net.pside.androidthings.example.event.CloudMessageEvent
import net.pside.androidthings.example.i2c.MicroDotPhat
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

/**
 * Skeleton of the main Android Things activity. Implement your device's logic
 * in this class.
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <pre>`PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);`</pre>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 */
class MainActivity : Activity() {

    lateinit var peripheralService: PeripheralManagerService
    lateinit var adapter: ArrayAdapter<String>
    lateinit var microDotPhat: MicroDotPhat

    val data = arrayOf(
            intArrayOf(
                    0b00100,
                    0b01100,
                    0b00100,
                    0b00100,
                    0b00100,
                    0b00100,
                    0b00100
            ),
            intArrayOf(
                    0b01110,
                    0b10001,
                    0b00001,
                    0b00010,
                    0b00100,
                    0b01000,
                    0b11111
            ),
            intArrayOf(
                    0b01110,
                    0b10001,
                    0b00001,
                    0b01110,
                    0b00001,
                    0b10001,
                    0b01110
            ),
            intArrayOf(
                    0b00010,
                    0b00110,
                    0b01010,
                    0b11111,
                    0b00010,
                    0b00010,
                    0b00010
            ),
            intArrayOf(
                    0b11111,
                    0b10000,
                    0b10000,
                    0b11110,
                    0b00001,
                    0b00001,
                    0b11110
            ),
            intArrayOf(
                    0b01110,
                    0b10000,
                    0b10000,
                    0b11110,
                    0b10001,
                    0b10001,
                    0b01110
            )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        peripheralService = PeripheralManagerService()

        adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        listView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)

        Timber.d("available: %s", peripheralService.i2cBusList)


        val microDotPhat = MicroDotPhat("I2C1")

        microDotPhat.set(0, data[0])
        microDotPhat.set(1, data[1])
        microDotPhat.set(2, data[2])
        microDotPhat.set(3, data[3])
        microDotPhat.set(4, data[4])
        microDotPhat.set(5, data[5])

        microDotPhat.update()
    }

    override fun onStop() {
        super.onStop()
        microDotPhat.destroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: CloudMessageEvent) {
        adapter.add(event.message)
        adapter.notifyDataSetChanged()

//        toggleGpio(adapter.count % 2 == 0)
        blinkPwm(adapter.count % 2 == 0)
    }

    private fun toggleGpio(isOn: Boolean) {
        val gpio = peripheralService.openGpio("BCM18")
        gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        gpio.setActiveType(Gpio.ACTIVE_HIGH)

        gpio.value = isOn

        gpio.close()
    }

    private fun blinkPwm(active: Boolean) {
        val pwm = peripheralService.openPwm("PWM0")
        pwm.setPwmFrequencyHz(1.0)
        pwm.setPwmDutyCycle(50.0)

        pwm.setEnabled(active)

        pwm.close()
    }

}
