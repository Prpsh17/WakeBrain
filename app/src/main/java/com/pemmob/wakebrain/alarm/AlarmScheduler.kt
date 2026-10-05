package com.pemmob.wakebrain.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.pemmob.wakebrain.data.model.Alarm

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(alarm: Alarm) {
        if (!alarm.isActive) return

        val triggerAtMillis = AlarmScheduleCalculator.nextTriggerMillis(alarm)
        val intent = Intent(context, AlarmReceiver::class.java).putAlarm(alarm, triggerAtMillis)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val clockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, pendingIntent)
        try {
            alarmManager.setAlarmClock(clockInfo, pendingIntent)
        } catch (_: Exception) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent,
            )
        }
    }

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
        const val EXTRA_DAYS = "EXTRA_DAYS"
        const val EXTRA_TRIGGER_AT = "EXTRA_TRIGGER_AT"
        const val EXTRA_RINGTONE = "EXTRA_RINGTONE"
    }
}
