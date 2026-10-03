package com.pemmob.wakebrain.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.pemmob.wakebrain.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver untuk menangkap sinyal AlarmManager saat jam alarm berdering.
 */
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != AlarmScheduler.ACTION_FIRE_ALARM) return
        val alarm = intent.alarmOrNull() ?: return
        val triggerAt = intent.getLongExtra(
            AlarmScheduler.EXTRA_TRIGGER_AT,
            System.currentTimeMillis(),
        )
        Log.i(TAG, "Alarm received: id=${alarm.id}, label=${alarm.label}, triggerAt=$triggerAt")

        AlarmRingingService.start(context, alarm, triggerAt)

        if (AlarmScheduleCalculator.isOneTime(alarm.days)) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    AppDatabase.getDatabase(context).alarmDao().deactivateAlarm(alarm.id)
                } finally {
                    pendingResult.finish()
                }
            }
        } else {
            AlarmScheduler(context.applicationContext).schedule(alarm)
        }
    }

    companion object {
        private const val TAG = "WakeBrainAlarmReceiver"
    }
}
