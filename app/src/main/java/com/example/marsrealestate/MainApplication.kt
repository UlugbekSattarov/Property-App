package com.example.marsrealestate

import android.app.Application
import com.example.marsrealestate.util.NotificationHelper
import com.example.marsrealestate.util.PreferenceDarkMode

class MainApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        PreferenceDarkMode.setDarkModeFromPreferences(this)
        NotificationHelper.createNotificationChannel(this)
    }






}