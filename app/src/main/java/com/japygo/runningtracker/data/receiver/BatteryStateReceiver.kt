package com.japygo.runningtracker.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import com.japygo.runningtracker.data.manager.BatteryStateManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BatteryStateReceiver : BroadcastReceiver() {

    @Inject
    lateinit var batteryStateManager: BatteryStateManager

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BATTERY_CHANGED -> {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                batteryStateManager.updateBatteryState(level * 100 / scale.toFloat())
            }
        }
    }
}