package com.pemmob.wakebrain.data.local

import android.content.Context

/**
 * Pengelola penyimpanan preferensi pengaturan aplikasi berbasis SharedPreferences.
 */
class SettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("wakebrain_settings", Context.MODE_PRIVATE)

    var isChallengeDisabled: Boolean
        get() = prefs.getBoolean(KEY_CHALLENGE_DISABLED, false)
        set(value) {
            prefs.edit().putBoolean(KEY_CHALLENGE_DISABLED, value).apply()
        }

    var isVibrateOnly: Boolean
        get() = prefs.getBoolean(KEY_VIBRATE_ONLY, false)
        set(value) {
            prefs.edit().putBoolean(KEY_VIBRATE_ONLY, value).apply()
        }

    companion object {
        private const val KEY_CHALLENGE_DISABLED = "key_challenge_disabled"
        private const val KEY_VIBRATE_ONLY = "key_vibrate_only"
    }
}
