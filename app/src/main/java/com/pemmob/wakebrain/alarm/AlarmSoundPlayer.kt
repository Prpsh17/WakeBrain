package com.pemmob.wakebrain.alarm

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import com.pemmob.wakebrain.R
import com.pemmob.wakebrain.data.local.SettingsManager

object AlarmSoundPlayer {
    private var mediaPlayer: MediaPlayer? = null

    private fun getSoundResource(ringtoneName: String): Int {
        return when (ringtoneName.lowercase()) {
            "nada 2", "nada pasha", "alarm pasha", "pasha" -> R.raw.alarm_pasha
            "nada 3", "nada pikri", "alarm pikri", "pikri" -> R.raw.alarm_pikri
            else -> R.raw.alarm_adit
        }
    }

    fun start(context: Context, ringtoneName: String = "Nada 1") {
        val settingsManager = SettingsManager(context)
        if (settingsManager.isVibrateOnly) return

        if (mediaPlayer?.isPlaying == true) return

        val soundRes = getSoundResource(ringtoneName)

        try {
            mediaPlayer = MediaPlayer.create(context, soundRes)?.apply {
                isLooping = true
                start()
            }
        } catch (_: Exception) {
            mediaPlayer = null
        }

        if (mediaPlayer == null) {
            try {
                val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(context, alarmUri)
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build(),
                    )
                    isLooping = true
                    prepare()
                    start()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playSample(context: Context, ringtoneName: String) {
        stop()
        val settingsManager = SettingsManager(context)
        if (settingsManager.isVibrateOnly) return

        val soundRes = getSoundResource(ringtoneName)

        try {
            mediaPlayer = MediaPlayer.create(context, soundRes)?.apply {
                isLooping = false
                start()
            }
        } catch (_: Exception) {
            mediaPlayer = null
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
        }
    }
}
