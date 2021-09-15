package com.amirhosseinemadi.appstore.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.databinding.DataBindingUtil
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.ActivitySplashBinding
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.viewmodel.SplashVm
import com.google.firebase.messaging.FirebaseMessaging

class SplashActivity : AppCompatActivity() {

    private lateinit var viewModel:SplashVm
    private lateinit var splashBinding:ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = SplashVm()
        splashBinding = DataBindingUtil.setContentView<ActivitySplashBinding>(this,R.layout.activity_splash).also { it.viewModel = viewModel }

        checkInit()

    }


    private fun checkInit()
    {
        val uidNotHex: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        var uid = ""

        if (uidNotHex.length == 16)
        {
            for (i: Int in uidNotHex.indices)
            {
                if (i % 2 == 0 && i > 1)
                {
                    uid += ":"
                }
                uid += uidNotHex.get(i)
            }
        }else if (uidNotHex.length == 23)
        {
            uid = uidNotHex
        }

        var token: String? = PrefManager.getToken()

        if (token == null)
        {
            FirebaseMessaging.getInstance()
                .token.addOnCompleteListener(
                    { task ->
                        if (task.isSuccessful)
                        {
                            token = task.getResult()
                            syncOrInit(uid, token!!)
                        } else
                        {
                            task.exception?.printStackTrace()
                        }
                    })
        }else
        {
            syncOrInit(uid,token!!)
        }
    }


    private fun syncOrInit(uid:String,token:String)
    {
        if (PrefManager.getFirst())
        {
            viewModel.getInitResponse(uid, token)
                .observe(this@SplashActivity,
                    { t ->
                        if (t?.responseCode == 1)
                        {
                            PrefManager.setFirst(false)
                            //TODO
                        }
                    })
        }else
        {
            viewModel.getSyncResponse(uid, token)
                .observe(this@SplashActivity,
                    { t ->
                        if (t?.responseCode == 1)
                        {
                            //TODO
                        }
                    })
        }
    }

}