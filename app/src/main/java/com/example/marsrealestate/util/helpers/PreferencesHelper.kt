package com.example.marsrealestate.util.helpers

import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.example.marsrealestate.R

object PreferencesHelper {

    private fun getPrefValue(context: Context, @StringRes keyId : Int, @StringRes defaultId : Int) : String? {
        val resources = context.resources

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val key = resources.getString(keyId)
        val default = resources.getString(defaultId)

        return prefs.getString(key, default)
    }


    fun setDarkMode(context: Context) {
        val resources = context.resources

        val mode = when (getPrefValue(context, R.string.preference_key_dark_mode, R.string.preference_dark_mode_follow_system))
        {
            resources.getString(R.string.preference_dark_mode_always_light) -> AppCompatDelegate.MODE_NIGHT_NO
            resources.getString(R.string.preference_dark_mode_always_dark) -> AppCompatDelegate.MODE_NIGHT_YES
            resources.getString(R.string.preference_dark_mode_follow_system) -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            resources.getString(R.string.preference_dark_mode_follow_battery) -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        AppCompatDelegate.setDefaultNightMode(mode)
    }


    fun setFontSize(context: Context) {
        val resources = context.resources

        val fontSizeTheme = when (getPrefValue(context, R.string.preference_key_font_size, R.string.pref_font_size_normal))
        {
            resources.getString(R.string.pref_font_size_normal) -> R.style.FontSizeNormalTheme
            resources.getString(R.string.pref_font_size_smaller) -> R.style.FontSizeSmallerTheme
            resources.getString(R.string.pref_font_size_bigger) -> R.style.FontSizeBiggerTheme
            else -> R.style.FontSizeNormalTheme
        }

        context.theme.applyStyle(fontSizeTheme,true)
    }




}

