package com.pemmob.wakebrain.alarm

import android.content.Context
import android.os.VibrationEffect
import android.os.VibrationAttributes
import android.os.Vibrator
import android.os.VibratorManager

object AlarmVibrationPlayer {
    private var vibrator: Vibrator? = null

    @Synchronized
    fun start(context: Context) {
        val currentVibrator = context
            .getSystemService(VibratorManager::class.java)
            .defaultVibrator
        if (!currentVibrator.hasVibrator()) return

        vibrator = currentVibrator
        val effect = VibrationEffect.createWaveform(
            longArrayOf(0, 700, 300),
            0,
        )
        val attributes = VibrationAttributes.Builder()
            .setUsage(VibrationAttributes.USAGE_ALARM)
            .build()
        currentVibrator.vibrate(effect, attributes)
    }

    @Synchronized
    fun stop() {
        vibrator?.cancel()
        vibrator = null
    }
}
