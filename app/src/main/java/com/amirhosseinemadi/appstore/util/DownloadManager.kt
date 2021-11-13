package com.amirhosseinemadi.appstore.util

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.model.ApiCaller
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import okhttp3.ResponseBody
import retrofit2.Response

class DownloadManager : Service() {

    private val apiCaller:ApiCaller

    init
    {
        apiCaller = Application.component.apiCaller()
    }

    override fun onBind(intent: Intent?): IBinder?
    {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        if (intent != null && intent.extras != null && intent.extras!!.getString("packageName") != null)
        {
            apiCaller.download(intent.extras!!.getString("packageName")!!, object : Observer<String>
            {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(t: String?)
                {
                    println(t)
                }

                override fun onError(e: Throwable?) {

                }

                override fun onComplete() {

                }

            })
        }
        return super.onStartCommand(intent, flags, startId)
    }

}