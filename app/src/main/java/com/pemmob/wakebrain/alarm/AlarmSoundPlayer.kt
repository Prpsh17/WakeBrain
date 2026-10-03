package com.pemmob.wakebrain.alarm

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioDeviceInfo
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import com.pemmob.wakebrain.data.local.SettingsManager

/**
 * Pemutar media audio nada dering alarm berbasis MediaPlayer.
 */
object AlarmSoundPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var toneGenerator: ToneGenerator? = null
    private var audioManager: AudioManager? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    private val handler = Handler(Looper.getMainLooper())
    private val repeatTone = object : Runnable {
        override fun run() {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, TONE_DURATION_MS)
            if (toneGenerator != null) handler.postDelayed(this, TONE_REPEAT_MS)
        }
    }

    /**
     * Memulai pemutaran nada dering MP3 / ringtone sistem.
     */
    @Synchronized
    fun start(context: Context) {
        val settingsManager = SettingsManager(context)
        if (settingsManager.isVibrateOnly) return

        if (mediaPlayer?.isPlaying == true || toneGenerator != null) return

        try {
            val manager = context.getSystemService(AudioManager::class.java)
            val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)
                .setAudioAttributes(ALARM_AUDIO_ATTRIBUTES)
                .build()
            audioManager = manager
            audioFocusRequest = focusRequest
            manager.requestAudioFocus(focusRequest)

            val alarmUri = RingtoneManager.getActualDefaultRingtoneUri(
                context,
                RingtoneManager.TYPE_ALARM,
            ) ?: RingtoneManager.getActualDefaultRingtoneUri(
                context,
                RingtoneManager.TYPE_RINGTONE,
            ) ?: error("Tidak ada nada alarm sistem")

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(ALARM_AUDIO_ATTRIBUTES)
                setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
                setOnErrorListener { failedPlayer, _, _ ->
                    failedPlayer.release()
                    mediaPlayer = null
                    startFallbackTone()
                    true
                }
                setDataSource(context, alarmUri)
                val phoneSpeaker = manager
                    .getDevices(AudioManager.GET_DEVICES_OUTPUTS)
                    .firstOrNull { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
                val routedToPhoneSpeaker = phoneSpeaker != null && setPreferredDevice(phoneSpeaker)
                Log.i(TAG, "Alarm audio route requested: phoneSpeaker=$routedToPhoneSpeaker")
                isLooping = true
                prepare()
                start()
                Log.i(TAG, "Alarm sound started with system URI")
            }
        } catch (error: Exception) {
            Log.e(TAG, "System alarm sound failed; using fallback tone", error)
            mediaPlayer?.release()
            mediaPlayer = null
            startFallbackTone()
        }
    }

    /**
     * Menghentikan pemutaran nada dering dan melepas resource MediaPlayer.
     */
    @Synchronized
    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {
            // The player may already have been released by the platform.
        } finally {
            mediaPlayer = null
            handler.removeCallbacks(repeatTone)
            toneGenerator?.release()
            toneGenerator = null
            audioFocusRequest?.let { request -> audioManager?.abandonAudioFocusRequest(request) }
            audioFocusRequest = null
            audioManager = null
        }
    }

    private fun startFallbackTone() {
        toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME)
        handler.post(repeatTone)
    }

    private const val TONE_DURATION_MS = 1_000
    private const val TONE_REPEAT_MS = 1_300L
    private const val TAG = "WakeBrainAlarmSound"

    private val ALARM_AUDIO_ATTRIBUTES = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ALARM)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
}
