package com.japygo.runningtracker.data.receiver

import android.content.Intent
import android.os.BatteryManager
import com.japygo.runningtracker.data.manager.BatteryStateManager
import javax.inject.Inject

class BatteryStateReceiver @Inject constructor(
    private val batteryStateManager: BatteryStateManager
) {
    fun onReceive(intent: Intent?) {
        if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val percentage = level * 100 / scale.toFloat()
            batteryStateManager.updateBatteryState(percentage)
        }
    }
}