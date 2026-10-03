package com.pemmob.wakebrain.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.pemmob.wakebrain.MainActivity
import com.pemmob.wakebrain.R
import com.pemmob.wakebrain.data.model.Alarm

class AlarmRingingService : Service() {
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarm = intent?.alarmOrNull()
        if (alarm == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        val triggerAt = intent.getLongExtra(
            AlarmScheduler.EXTRA_TRIGGER_AT,
            System.currentTimeMillis(),
        )
        startForeground(
            NOTIFICATION_ID,
            buildNotification(alarm, triggerAt),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK,
        )
        Log.i(TAG, "Foreground alarm started: id=${alarm.id}, label=${alarm.label}")
        acquireWakeLock()
        AlarmSoundPlayer.start(applicationContext)
        AlarmVibrationPlayer.start(applicationContext)
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        AlarmSoundPlayer.stop()
        AlarmVibrationPlayer.stop()
        wakeLock?.let { if (it.isHeld) it.release() }
        wakeLock = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(alarm: Alarm, triggerAt: Long): Notification {
        val openAlarmIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
            putAlarm(alarm, triggerAt)
        }
        val openAlarmPendingIntent = PendingIntent.getActivity(
            this,
            alarm.id,
            openAlarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm_notification)
            .setContentTitle(alarm.label.ifBlank { getString(R.string.alarm_notification_title) })
            .setContentText(getString(R.string.alarm_notification_text))
            .setCategory(Notification.CATEGORY_ALARM)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(openAlarmPendingIntent)
            .setFullScreenIntent(openAlarmPendingIntent, true)
            .setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.alarm_channel_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = getString(R.string.alarm_channel_description)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(null, null)
            enableVibration(false)
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun acquireWakeLock() {
        if (wakeLock?.isHeld == true) return
        wakeLock = getSystemService(PowerManager::class.java)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "$packageName:alarm")
            .apply { acquire(MAX_RING_DURATION_MS) }
    }

    companion object {
        private const val CHANNEL_ID = "wakebrain_alarm_v2"
        private const val TAG = "WakeBrainAlarmService"
        private const val NOTIFICATION_ID = 7_001
        private const val MAX_RING_DURATION_MS = 30 * 60 * 1_000L

        fun start(context: Context, alarm: Alarm, triggerAt: Long = System.currentTimeMillis()) {
            val intent = Intent(context, AlarmRingingService::class.java).putAlarm(alarm, triggerAt)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, AlarmRingingService::class.java))
        }
    }
}
