package com.example.marsrealestate.util.helpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.marsrealestate.R
import com.example.marsrealestate.data.MarsProperty
import com.example.marsrealestate.detail.DetailFragmentArgs
import com.example.marsrealestate.util.resolveColor


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

    fun notifyPropertyBought(context: Context,property: MarsProperty) {
        val deeplink = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_graph_main)
            .setDestination(R.id.dest_detail)
            .setArguments(bundleOf("MarsProperty" to property))
            .createPendingIntent()

        buildNotificationPropertyBought(context,deeplink,property) {
            NotificationManagerCompat.from(context).notify(propertyBoughtNotifId++, it)
        }

    }


    private fun buildNotificationPropertyBought(
        context: Context,
        intent: PendingIntent,
        property: MarsProperty,
        onNotifBuilt : (notif : Notification) -> Unit) : Notification {

        val layout = RemoteViews(context.packageName, R.layout.layout_notification_expanded)
        val colorSecondary = context.resolveColor(R.attr.colorSecondary)

        //This is the color of the icon or app name for versions of android >= M
        //otherwise it is the color of the background
        val tint = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            colorSecondary
        else
            context.resolveColor(android.R.attr.colorBackground)


        return NotificationCompat.Builder(context, CHANNEL_ID_PROPERTY_BOUGHT)
            .setColor(tint)
            .setSmallIcon(R.drawable.mars_notification)
            .setContentTitle("Property bought !")
            .setContentText(context.resources.getString(R.string.success_bought, property.id))

            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(intent)


            .setCustomBigContentView(layout)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())

            .build()

            //The static part of the notification has been built, now we have to load the
            //image asynchronously and call onNotifBuilt() when the image has been loaded

            .also {
                val tgt = PropertyBoughtNotificationTarget(
                    R.id.notification_big_image,
                    layout) { onNotifBuilt(it) }

                Glide.with(context)
                    .asBitmap()
                    .override(1280,720)
                    .load(property.imgSrcUrl.toUri())
                    .into(tgt)
            }
    }




    class PropertyBoughtNotificationTarget(
        private val viewId : Int,
        private val remoteView : RemoteViews,
        private val onImageLoaded : () -> Unit) : CustomTarget<Bitmap>() {



        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            setBitmap(resource)
            onImageLoaded()
        }

        override fun onLoadCleared(placeholder: Drawable?) {
            remoteView.setImageViewBitmap(viewId,null)
        }

        private fun setBitmap(bitmap : Bitmap?) {
            remoteView.setImageViewBitmap(viewId,bitmap)
        }


    }

}