package com.example.marsrealestate

import android.app.Application
import com.example.marsrealestate.util.helpers.NotificationHelper
import com.example.marsrealestate.util.helpers.PreferencesHelper

class MainApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        PreferencesHelper.setDarkMode(this)
        NotificationHelper.createNotificationChannel(this)
    }






}