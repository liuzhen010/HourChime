package com.hourchime.app

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var switchChime: Switch
    private lateinit var tvStatus: TextView
    private lateinit var btnTimeRange: Button
    private lateinit var tvTimeRange: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("HourChime", Context.MODE_PRIVATE)

        switchChime = findViewById(R.id.switch_chime)
        tvStatus = findViewById(R.id.tv_status)
        btnTimeRange = findViewById(R.id.btn_time_range)
        tvTimeRange = findViewById(R.id.tv_time_range)

        createNotificationChannel()
        updateUI()

        switchChime.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("enabled", isChecked).apply()
            if (isChecked) {
                ChimeScheduler.schedule(this)
                tvStatus.text = "✅ 报时已开启，整点滴一声"
            } else {
                ChimeScheduler.cancel(this)
                tvStatus.text = "⏸ 报时已关闭"
            }
        }

        btnTimeRange.setOnClickListener {
            showTimeRangeDialog()
        }
    }

    private fun updateUI() {
        val enabled = prefs.getBoolean("enabled", false)
        val startHour = prefs.getInt("start_hour", 8)
        val endHour = prefs.getInt("end_hour", 22)

        switchChime.isChecked = enabled
        tvStatus.text = if (enabled) "✅ 报时已开启，整点滴一声" else "⏸ 报时已关闭"
        tvTimeRange.text = "报时时段：${startHour}:00 — ${endHour}:00"
    }

    private fun showTimeRangeDialog() {
        val startHour = prefs.getInt("start_hour", 8)
        val endHour = prefs.getInt("end_hour", 22)

        val hours = (0..23).map { "${it}:00" }.toTypedArray()

        // 选开始时间
        AlertDialog.Builder(this)
            .setTitle("选择开始时间")
            .setItems(hours) { _, startIdx ->
                AlertDialog.Builder(this)
                    .setTitle("选择结束时间")
                    .setItems(hours) { _, endIdx ->
                        if (endIdx > startIdx) {
                            prefs.edit()
                                .putInt("start_hour", startIdx)
                                .putInt("end_hour", endIdx)
                                .apply()
                            tvTimeRange.text = "报时时段：${startIdx}:00 — ${endIdx}:00"
                            Toast.makeText(this, "时段已更新", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "结束时间须晚于开始时间", Toast.LENGTH_SHORT).show()
                        }
                    }.show()
            }.show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "chime_channel",
                "整点报时",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "整点报时后台服务"
                setSound(null, null)
            }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }
}
