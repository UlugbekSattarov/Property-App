package com.example.marsrealestate

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.example.marsrealestate.util.NotificationHelper
import com.example.marsrealestate.util.PreferenceDarkMode

class MainApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        PreferenceDarkMode.setDarkModeFromPreferences(this)
        NotificationHelper.createNotificationChannel(this)
    }






}