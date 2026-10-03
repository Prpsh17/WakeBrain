package com.pemmob.wakebrain.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pemmob.wakebrain.MainActivity

/**
 * BroadcastReceiver untuk menangkap sinyal AlarmManager saat jam alarm berdering.
 */
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra(AlarmScheduler.EXTRA_ALARM_ID, -1)
        val hour = intent.getIntExtra(AlarmScheduler.EXTRA_HOUR, 7)
        val minute = intent.getIntExtra(AlarmScheduler.EXTRA_MINUTE, 0)
        val puzzleType = intent.getStringExtra(AlarmScheduler.EXTRA_PUZZLE_TYPE) ?: "Matematika"
        val difficulty = intent.getStringExtra(AlarmScheduler.EXTRA_DIFFICULTY) ?: "EASY"
        val label = intent.getStringExtra(AlarmScheduler.EXTRA_LABEL) ?: "Alarm"

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(AlarmScheduler.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmScheduler.EXTRA_HOUR, hour)
            putExtra(AlarmScheduler.EXTRA_MINUTE, minute)
            putExtra(AlarmScheduler.EXTRA_PUZZLE_TYPE, puzzleType)
            putExtra(AlarmScheduler.EXTRA_DIFFICULTY, difficulty)
            putExtra(AlarmScheduler.EXTRA_LABEL, label)
        }
        context.startActivity(launchIntent)
    }
}
