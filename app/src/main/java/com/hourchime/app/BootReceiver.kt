package com.hourchime.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * 开机自启：系统启动后自动恢复闹钟调度
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {

            val prefs = context.getSharedPreferences("HourChime", Context.MODE_PRIVATE)
            val enabled = prefs.getBoolean("enabled", false)

            if (enabled) {
                // 重新注册闹钟（重启后闹钟会丢失）
                ChimeScheduler.schedule(context)
            }
        }
    }
}
