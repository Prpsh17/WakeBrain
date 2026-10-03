package com.pemmob.wakebrain.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.pemmob.wakebrain.data.model.Alarm
import java.util.Calendar

/**
 * Pengelola penjadwalan alarm menggunakan AlarmManager sistem Android.
 */
class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Menjadwalkan alarm presisi di sistem Android.
     */
    fun schedule(alarm: Alarm) {
        if (!alarm.isActive) return

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarm.id)
            putExtra(EXTRA_HOUR, alarm.hour)
            putExtra(EXTRA_MINUTE, alarm.minute)
            putExtra(EXTRA_PUZZLE_TYPE, alarm.puzzleType)
            putExtra(EXTRA_DIFFICULTY, alarm.difficulty)
            putExtra(EXTRA_LABEL, alarm.label)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val clockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent)
        try {
            alarmManager.setAlarmClock(clockInfo, pendingIntent)
        } catch (_: Exception) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent,
            )
        }
    }

    /**
     * Membatalkan jadwal alarm dari AlarmManager.
     */
    fun cancel(alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        alarmManager.cancel(pendingIntent)
    }

    companion object {
        const val EXTRA_ALARM_ID = "EXTRA_ALARM_ID"
        const val EXTRA_HOUR = "EXTRA_HOUR"
        const val EXTRA_MINUTE = "EXTRA_MINUTE"
        const val EXTRA_PUZZLE_TYPE = "EXTRA_PUZZLE_TYPE"
        const val EXTRA_DIFFICULTY = "EXTRA_DIFFICULTY"
        const val EXTRA_LABEL = "EXTRA_LABEL"
    }
}
