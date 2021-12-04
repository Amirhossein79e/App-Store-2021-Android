package com.amirhosseinemadi.appstore.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.model.entity.AppModel
import com.amirhosseinemadi.appstore.model.entity.ResponseObject
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.activity.MainActivity
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import kotlin.random.Random

class UpdateChecker : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?)
    {
        if (PrefManager.getUpdate())
        {
            val apiCaller: ApiCaller = Application.component.apiCaller()
            apiCaller.getUpdates(0, Utilities.getAllPackages(), object : SingleObserver<ResponseObject<List<AppModel>>>
            {
                    override fun onSubscribe(d: Disposable?) {

                    }

                    override fun onSuccess(t: ResponseObject<List<AppModel>>?) {
                        if (t?.data != null && t.data!!.size > 0)
                        {
                            notifyUpdate(context,t.data!! as MutableList<AppModel>)
                        }
                    }

                    override fun onError(e: Throwable?) {

                    }

            })
        }
    }


    private fun notifyUpdate(context: Context?, list:MutableList<AppModel>)
    {
        val notificationManager:NotificationManagerCompat = NotificationManagerCompat.from(context!!)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            if (notificationManager.getNotificationChannel("2") == null)
            {
                notificationManager.createNotificationChannel(NotificationChannel("2","updateNotification",NotificationManager.IMPORTANCE_HIGH))
            }
        }

        val intent:Intent = Intent(context,MainActivity::class.java)
        intent.putExtra("key","update")

        val notificationCompat:NotificationCompat.Builder = NotificationCompat.Builder(context,"2")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(PendingIntent.getActivity(context,0, intent,0))

        if (PrefManager.getLang().equals("en"))
        {
            notificationCompat.setContentTitle("Update Available")
                .setContentText("Update available for ${list.get(0).nameEn} and some other apps ")
        }else
        {
            notificationCompat.setContentTitle("بروزرسانی در دسترس است")
                .setContentText("بروزرسانی برای " + list.get(0).nameFa + "و چند اپلیکیشن دیگر موجود است")
        }

        notificationManager.notify(Random.nextInt(),notificationCompat.build())
    }

}