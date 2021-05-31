package com.example.marsrealestate.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.example.marsrealestate.R

object PreferenceDarkMode {

    fun setDarkModeFromPreferences(context: Context) {

        val resources = context.resources

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val key = resources.getString(R.string.preference_key_dark_theme)
        val default = resources.getString(R.string.preference_dark_mode_follow_system)

        val mode = when (prefs.getString(key, default)) {
            resources.getString(R.string.preference_dark_mode_always_light) -> AppCompatDelegate.MODE_NIGHT_NO
            resources.getString(R.string.preference_dark_mode_always_dark) -> AppCompatDelegate.MODE_NIGHT_YES
            resources.getString(R.string.preference_dark_mode_follow_system) -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            resources.getString(R.string.preference_dark_mode_follow_battery) -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        AppCompatDelegate.setDefaultNightMode(mode)
    }

}

