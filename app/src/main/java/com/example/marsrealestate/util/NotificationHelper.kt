package com.example.marsrealestate.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.marsrealestate.R
import com.example.marsrealestate.data.MarsProperty


object NotificationHelper {

    const val CHANNEL_ID_PROPERTY_BOUGHT = "CHANNEL_PROPERTY_BOUGHT"
    private var propertyBoughtNotifId = 0

    fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                CHANNEL_ID_PROPERTY_BOUGHT,
                "Property bought",
                importance
            ).apply {
                description = "Channel used when properties are bought"
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    fun notifyPropertyBought(context: Context, intent: PendingIntent,property: MarsProperty) {

        val typedValue = TypedValue()
        context.theme?.resolveAttribute(R.attr.colorSecondary, typedValue, true)

        val imagePropertyBought = ResourcesCompat.getDrawable(
            context.resources,
            property.imgSrcUrl.toInt(),
            context.theme
        )?.toBitmap()
//
        val collapsed = RemoteViews(context.packageName, R.layout.layout_notification_collapsed).apply {
            setTextColor(R.id.timestamp, typedValue.data)
            setTextColor(R.id.notification_title, typedValue.data)
            setTextViewText(
                R.id.label_bought,
                context.resources.getString(R.string.success_bought, property.id)
            )
            setImageViewBitmap(R.id.leading_icon, imagePropertyBought)
        }



        val builder = NotificationCompat.Builder(context, "CHANNEL_ID")
            .setSmallIcon(R.drawable.mars_notification)
            .setColor(typedValue.data)
            .setContentTitle("Property bought !")
            .setContentText(context.resources.getString(R.string.success_bought, property.id))
            .setLargeIcon(imagePropertyBought)
//            .setCustomContentView(collapsed)
//            .setCustomBigContentView(collapsed)
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(imagePropertyBought)
                .bigLargeIcon(null)
            )

            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(intent)

        NotificationManagerCompat.from(context).notify(propertyBoughtNotifId++, builder.build())

    }
}