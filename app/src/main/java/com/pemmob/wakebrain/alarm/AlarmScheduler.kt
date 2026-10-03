package com.pemmob.wakebrain.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.pemmob.wakebrain.data.model.Alarm
import com.pemmob.wakebrain.MainActivity
import java.time.Instant
import java.time.ZoneId

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

        val triggerAtMillis = AlarmScheduleCalculator.nextTriggerMillis(alarm)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_FIRE_ALARM
            putAlarm(alarm, triggerAtMillis)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val showIntent = PendingIntent.getActivity(
            context,
            alarm.id + SHOW_INTENT_REQUEST_OFFSET,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val clockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, showIntent)

        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAlarmClock(clockInfo, pendingIntent)
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent,
            )
        }
        val scheduledTime = Instant.ofEpochMilli(triggerAtMillis).atZone(ZoneId.systemDefault())
        Log.i(TAG, "Scheduled alarm id=${alarm.id} at $scheduledTime for days=${alarm.days}")
    }

    /**
     * Membatalkan jadwal alarm dari AlarmManager.
     */
    fun cancel(alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_FIRE_ALARM
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        alarmManager.cancel(pendingIntent)
    }

    companion object {
        const val ACTION_FIRE_ALARM = "com.pemmob.wakebrain.action.FIRE_ALARM"
        const val EXTRA_ALARM_ID = "EXTRA_ALARM_ID"
        const val EXTRA_HOUR = "EXTRA_HOUR"
        const val EXTRA_MINUTE = "EXTRA_MINUTE"
        const val EXTRA_PUZZLE_TYPE = "EXTRA_PUZZLE_TYPE"
        const val EXTRA_DIFFICULTY = "EXTRA_DIFFICULTY"
        const val EXTRA_LABEL = "EXTRA_LABEL"
        const val EXTRA_DAYS = "EXTRA_DAYS"
        const val EXTRA_TRIGGER_AT = "EXTRA_TRIGGER_AT"

        private const val SHOW_INTENT_REQUEST_OFFSET = 1_000_000
        private const val TAG = "WakeBrainAlarmScheduler"
    }
}
