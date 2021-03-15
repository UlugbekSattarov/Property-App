package com.example.marsrealestate.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.marsrealestate.R
import com.example.marsrealestate.data.MarsProperty


object NotificationHelper {

    private const val CHANNEL_ID_PROPERTY_BOUGHT = "CHANNEL_PROPERTY_BOUGHT"
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

    fun notifyPropertyBought(context: Context, navController: NavController,property: MarsProperty) {

        val deeplink = navController.createDeepLink()
            .setArguments(bundleOf("MarsProperty" to property))
            .setGraph(R.navigation.nav_graph_main)
            .setDestination(R.id.dest_detail)
            .createPendingIntent()

        val notif = buildNotificationPropertyBought(context,deeplink,property)

        NotificationManagerCompat.from(context).notify(propertyBoughtNotifId++, notif)

    }

    private fun buildNotificationPropertyBought(context: Context, intent: PendingIntent,property: MarsProperty) : Notification {

        val colorSecondary = resolveColorSecondary(context)
        val imagePropertyBought = getPropertyImage(context,property)



        val builder = NotificationCompat.Builder(context, "CHANNEL_ID")
            .setSmallIcon(R.drawable.mars_notification)
            .setColor(colorSecondary)
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


        return builder.build()
    }



    private fun resolveColorSecondary(context: Context) : Int {
        val typedValue = TypedValue()
        context.theme?.resolveAttribute(R.attr.colorSecondary, typedValue, true)
        return typedValue.data
    }

    private fun getPropertyImage(context: Context,property: MarsProperty) : Bitmap {
        return ResourcesCompat.getDrawable(
            context.resources,
            property.imgSrcUrl.toInt(),
            context.theme
        )?.toBitmap() ?: Bitmap.createBitmap(0,0,Bitmap.Config.ALPHA_8)
    }

    private fun buildCollapsedNotif(context: Context,colorSecondary : Int,property: MarsProperty,imagePropertyBought : Bitmap) {
        val collapsed = RemoteViews(context.packageName, R.layout.layout_notification_collapsed).apply {
            setTextColor(R.id.timestamp, colorSecondary)
            setTextColor(R.id.notification_title, colorSecondary)
            setTextViewText(
                R.id.label_bought,
                context.resources.getString(R.string.success_bought, property.id)
            )
            setImageViewBitmap(R.id.leading_icon, imagePropertyBought)
        }
    }








}