package com.pemmob.wakebrain.alarm

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import com.pemmob.wakebrain.R
import com.pemmob.wakebrain.data.local.SettingsManager

/**
 * Pemutar media audio nada dering alarm berbasis MediaPlayer.
 */
object AlarmSoundPlayer {
    private var mediaPlayer: MediaPlayer? = null

    /**
     * Memulai pemutaran nada dering MP3 / ringtone sistem.
     */
    fun start(context: Context) {
        val settingsManager = SettingsManager(context)
        if (settingsManager.isVibrateOnly) return

        if (mediaPlayer?.isPlaying == true) return

        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound)?.apply {
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

    /**
     * Menghentikan pemutaran nada dering dan melepas resource MediaPlayer.
     */
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
