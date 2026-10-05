package com.pemmob.wakebrain.data.local

import android.content.Context
import androidx.core.content.edit

class SettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("wakebrain_settings", Context.MODE_PRIVATE)

    var isVibrateOnly: Boolean
        get() = prefs.getBoolean(KEY_VIBRATE_ONLY, false)
        set(value) {
            prefs.edit { putBoolean(KEY_VIBRATE_ONLY, value) }
        }

    companion object {
        private const val KEY_VIBRATE_ONLY = "key_vibrate_only"
    }
}
