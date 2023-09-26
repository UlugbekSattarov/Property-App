package com.example.marsrealestate.util.helpers

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.marsrealestate.R
import com.example.marsrealestate.login.Credentials


object PreferencesHelper {

    private fun getPrefValue(context: Context, @StringRes keyId : Int, @StringRes defaultId : Int) : String? {
        val resources = context.resources

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val key = resources.getString(keyId)
        val default = resources.getString(defaultId)

        return prefs.getString(key, default)
    }


    fun getDarkMode(context: Context) : Int {
        val resources = context.resources

        return when (getPrefValue(context, R.string.preference_key_dark_mode, R.string.preference_dark_mode_follow_system))
        {
            resources.getString(R.string.preference_dark_mode_always_light) -> AppCompatDelegate.MODE_NIGHT_NO
            resources.getString(R.string.preference_dark_mode_always_dark) -> AppCompatDelegate.MODE_NIGHT_YES
            resources.getString(R.string.preference_dark_mode_follow_system) -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            resources.getString(R.string.preference_dark_mode_follow_battery) -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }

    fun setDarkMode(context: Context) =
        AppCompatDelegate.setDefaultNightMode(getDarkMode(context))



    fun getFontSize(context: Context) : Int {
        val resources = context.resources

        return when (getPrefValue(context, R.string.preference_key_font_size, R.string.pref_font_size_normal))
        {
            resources.getString(R.string.pref_font_size_normal) -> R.style.FontSizeNormalTheme
            resources.getString(R.string.pref_font_size_smaller) -> R.style.FontSizeSmallerTheme
            resources.getString(R.string.pref_font_size_bigger) -> R.style.FontSizeBiggerTheme
            else -> R.style.FontSizeNormalTheme
        }
    }


    fun setFontSize(context: Context) =
        context.theme.applyStyle(getFontSize(context),true)



    fun getEncryptedPreferences(context: Context) : SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()


        return EncryptedSharedPreferences.create(
            context,
            "encrypted_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun getCredentials(context: Context) : Credentials? {

        val sharedPreferences = getEncryptedPreferences(context)

        val keyLogin = context.resources.getString(R.string.preference_key_login)
        val keyPassword = context.resources.getString(R.string.preference_key_password)

        val login = sharedPreferences.getString(keyLogin,null) ?: return null
        val password = sharedPreferences.getString(keyPassword,null) ?: return null

        return Credentials(login,password)
    }

    fun setCredentials(context: Context, credentials: Credentials)  {

        val sharedPreferences = getEncryptedPreferences(context)

        val keyLogin = context.resources.getString(R.string.preference_key_login)
        val keyPassword = context.resources.getString(R.string.preference_key_password)


        sharedPreferences.edit()
            .putString(keyLogin,credentials.login)
            .putString(keyPassword,credentials.password)
            .apply()
    }

    fun deleteCredentials(context: Context)  {

        val sharedPreferences = getEncryptedPreferences(context)

        val keyLogin = context.resources.getString(R.string.preference_key_login)
        val keyPassword = context.resources.getString(R.string.preference_key_password)


        sharedPreferences.edit()
            .remove(keyLogin)
            .remove(keyPassword)
            .apply()
    }


    object Tuto {


        @JvmStatic
        fun getShowFavoritesSwipe(context: Context): Boolean {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val key = context.resources.getString(R.string.preference_key_tuto_show_favorites_swipe)
            val default = context.resources.getBoolean(R.bool.preference_tuto_show_favorites_swipe_default)

            return prefs.getBoolean(key, default)
        }

        @JvmStatic
        fun setShowFavoritesSwipe(context: Context,value : Boolean) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val key = context.resources.getString(R.string.preference_key_tuto_show_favorites_swipe)

            prefs.edit().putBoolean(key, value).apply()
        }

        fun resetShowFavoritesSwipe(context: Context) {
            val default = context.resources.getBoolean(R.bool.preference_tuto_show_favorites_swipe_default)
            setShowFavoritesSwipe(context,default)
        }


    }

}

