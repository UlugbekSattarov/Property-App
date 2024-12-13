package com.example.propertyappg11

import android.app.Application
import com.example.propertyappg11.util.helpers.NotificationHelper
import com.example.propertyappg11.util.helpers.PreferencesHelper

class MainApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        PreferencesHelper.setDarkMode(this)
        NotificationHelper.createNotificationChannel(this)
    }






}
