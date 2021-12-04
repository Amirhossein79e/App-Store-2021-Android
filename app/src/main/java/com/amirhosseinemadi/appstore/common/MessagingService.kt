package com.amirhosseinemadi.appstore.common

import android.app.Notification
import android.app.NotificationManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception
import kotlin.random.Random

class MessagingService : FirebaseMessagingService() {

    private var hasIcon:Boolean = false
    private lateinit var iconCallback:Callback

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        PrefManager.setToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data:Map<String,String> = remoteMessage.data

        val notificationManager:NotificationManagerCompat = NotificationManagerCompat.from(this)
        val channel:NotificationChannelCompat = NotificationChannelCompat.Builder("3453545",4)
            .setName("messages")
            .build()
        notificationManager.createNotificationChannel(channel)

        val notification:NotificationCompat.Builder = NotificationCompat.Builder(this,"3453545")

        if (PrefManager.getLang().equals("fa"))
        {
            notification.setContentTitle(data.get("title_fa"))
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setSubText("message")
                .setContentText(data.get("message_fa"))
        }else
        {
            notification.setContentTitle(data.get("title_en"))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setSubText("message")
                .setContentText(data.get("message_en"))
        }

        if (data.get("icon") != null)
        {
            hasIcon = true

            Picasso.get().load(data.get("icon")).into(object : Target
            {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    iconCallback.notify(bitmap)
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    hasIcon = false
                    notificationManager.notify(Random.nextInt(),notification.build())
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    iconCallback = object : Callback
                    {
                        override fun notify(vararg obj: Any?)
                        {
                            notification.setLargeIcon(obj[0] as Bitmap)
                            notificationManager.notify(Random.nextInt(),notification.build())
                        }
                    }
                }
            })
        }

        if (!hasIcon)
        {
            notificationManager.notify(Random.nextInt(),notification.build())
        }

    }

}