package com.amirhosseinemadi.appstore.view.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.ActivitySplashBinding
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.viewmodel.SplashVm
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*

class SplashActivity : AppCompatActivity() {

    private lateinit var viewModel:SplashVm
    private lateinit var splashBinding:ActivitySplashBinding
    private lateinit var dialog:Dialog
    private lateinit var fDialog:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = SplashVm()
        splashBinding = DataBindingUtil.setContentView<ActivitySplashBinding>(this,R.layout.activity_splash).also { it.viewModel = viewModel }
        initView()
        handleError()
        sync()
    }


    private fun initView()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            Utilities.underApiStatusBarHandle(this)
        }
    }


    private fun handleError()
    {
        viewModel.error.observe(this,
            {
                dialog = Utilities.dialogIcon(this,null,R.string.request_failed,R.string.request_failed_pos,R.string.request_failed_neg,true,true,{
                    dialog.dismiss()
                    sync()
                },{
                    dialog.dismiss()
                    finish()
                })
                dialog.show()
            })
    }


    private fun sync()
    {
        val token: String? = PrefManager.getToken()

        if (token == null)
        {
            FirebaseMessaging.getInstance()
                .token.addOnCompleteListener{ task ->
                        if (task.isSuccessful)
                        {
                            PrefManager.setToken(task.result!!)
                            if (PrefManager.getAccess() == null)
                            {
                                syncOrInit(Utilities.getUid(), task.result!!)
                            }else
                            {
                                syncUser(PrefManager.getAccess()!!,task.result!!)
                            }
                        } else
                        {
                            task.exception?.printStackTrace()
                            fDialog = Utilities.dialogIcon(this,null,R.string.request_failed,R.string.request_failed_pos,R.string.request_failed_neg,true,true,{
                                dialog.dismiss()
                                sync()
                            },{
                                dialog.dismiss()
                                finish()
                            })
                            dialog.show()
                        }
                    }
        }else
        {
            if (PrefManager.getAccess() == null)
            {
                syncOrInit(Utilities.getUid(),token)
            }else
            {
                syncUser(PrefManager.getAccess()!!,token)
            }
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
                                viewModel.init(uid,token)
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
                .observe(this,
                    {
                        if (it?.responseCode == 1)
                        {
                            startActivity(Intent(this,MainActivity::class.java))
                            finish()
                        }else
                        {
                            dialog = Utilities.dialogIcon(this,null, R.string.request_failed,R.string.request_failed_pos,R.string.request_failed_neg,true,true,{
                                dialog.dismiss()
                                viewModel.sync(uid,token)
                            },{
                                dialog.dismiss()
                                finish()
                            })
                            dialog.show()
                        }
                    })
        }
    }


    private fun syncUser(access:String, token: String)
    {
        viewModel.getSyncUserResponse(access, token)
            .observe(this,
                {
                    if (it?.responseCode == 1)
                    {
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }else
                    {
                        dialog = Utilities.dialogIcon(this,null, R.string.request_failed,R.string.request_failed_pos,R.string.request_failed_neg,true,true,{
                            dialog.dismiss()
                            viewModel.syncUser(access, token)
                        },{
                            dialog.dismiss()
                            finish()
                        })
                        dialog.show()
                    }
                })
    }


    override fun attachBaseContext(newBase: Context?) {
        val configuration:Configuration = newBase!!.resources.configuration
        configuration.setLocale(Locale(PrefManager.getLang(),"IR"))
        super.attachBaseContext(newBase.createConfigurationContext(configuration))
    }

}