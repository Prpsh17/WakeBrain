package com.pemmob.wakebrain.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pemmob.wakebrain.MainActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarm = intent.alarmOrNull() ?: return
        val triggerAt = intent.getLongExtra(
            AlarmScheduler.EXTRA_TRIGGER_AT,
            System.currentTimeMillis(),
        )
        AlarmRingingService.start(context, alarm, triggerAt)

        val launchIntent = Intent(context, MainActivity::class.java).putAlarm(alarm, triggerAt).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(launchIntent)
    }
}
