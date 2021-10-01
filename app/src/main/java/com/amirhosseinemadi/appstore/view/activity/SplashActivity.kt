package com.amirhosseinemadi.appstore.view.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.databinding.ActivitySplashBinding
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.viewmodel.SplashVm
import com.google.firebase.crashlytics.internal.common.CrashlyticsReportDataCapture
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*

class SplashActivity : AppCompatActivity() {

    private lateinit var viewModel:SplashVm
    private lateinit var splashBinding:ActivitySplashBinding
    private lateinit var dialog:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = SplashVm()
        splashBinding = DataBindingUtil.setContentView<ActivitySplashBinding>(this,R.layout.activity_splash).also { it.viewModel = viewModel }
        handleError()
        checkInit()
    }


    private fun handleError()
    {
        viewModel.error.observe(this,
            {
                dialog = Utilities.dialogIcon(this,null,R.string.request_failed,R.string.request_failed_pos,R.string.request_failed_neg,true,true,{
                    dialog.dismiss()
                    checkInit()
                },{
                    dialog.dismiss()
                    finish()
                })
                dialog.show()
            })
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
                            startActivity(Intent(this,IntroActivity::class.java))
                            finish()
                        }else
                        {
                            dialog = Utilities.dialogIcon(this,null, R.string.request_failed,R.string.request_failed_pos,R.string.request_failed_neg,true,true,{
                                dialog.dismiss()
                                viewModel.getInitResponse(uid,token)
                            },{
                                dialog.dismiss()
                                finish()
                            })
                            dialog.show()
                        }
                    })
        }else
        {
            viewModel.getSyncResponse(uid, token)
                .observe(this@SplashActivity,
                    { t ->
                        if (t?.responseCode == 1)
                        {
                            startActivity(Intent(this,IntroActivity::class.java))
                            finish()
                        }else
                        {
                            dialog = Utilities.dialogIcon(this,null, R.string.request_failed,R.string.request_failed_pos,R.string.request_failed_neg,true,true,{
                                dialog.dismiss()
                                viewModel.getInitResponse(uid,token)
                            },{
                                dialog.dismiss()
                                finish()
                            })
                            dialog.show()
                        }
                    })
        }
    }


    override fun attachBaseContext(newBase: Context?) {
        val configuration:Configuration = newBase!!.resources.configuration
        configuration.setLocale(Locale(PrefManager.getLang()))
        super.attachBaseContext(newBase.createConfigurationContext(configuration))
    }

}