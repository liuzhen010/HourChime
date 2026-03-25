package com.hourchime.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.PowerManager
import java.util.Calendar

class ChimeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = context.getSharedPreferences("HourChime", Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean("enabled", false)

        if (!enabled) return

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val startHour = prefs.getInt("start_hour", 8)
        val endHour = prefs.getInt("end_hour", 22)

        // 在设定时间段内才报时
        if (currentHour in startHour until endHour) {
            playChime(context)
        }

        // 安排下一次整点
        ChimeScheduler.schedule(context)
    }

    private fun playChime(context: Context) {
        // 使用 WakeLock 确保短暂唤醒播放
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "HourChime:ChimeWakeLock"
        )
        wakeLock.acquire(3000L) // 最多持有3秒

        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val volume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)

            if (volume > 0) {
                // 使用系统ToneGenerator播放短促滴声（DTMF音调）
                val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150) // 150ms短音
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    toneGen.release()
                }, 300)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // 延迟释放，确保音效播放完毕
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                if (wakeLock.isHeld) wakeLock.release()
            }, 500)
        }
    }
}
