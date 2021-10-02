package com.amirhosseinemadi.appstore.view.activity

import android.app.Dialog
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.databinding.DataBindingUtil
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.ActivityMainBinding
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.viewmodel.MainVm
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel:MainVm
    private lateinit var mainBinding:ActivityMainBinding
    private lateinit var dialog:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = MainVm()
        mainBinding = DataBindingUtil.setContentView<ActivityMainBinding>(this,R.layout.activity_main).also { it.viewModel = viewModel }
        initView()
        registerNetworkListener()
    }


    private fun initView()
    {
        mainBinding.cardBottomNav.setBackgroundResource(R.drawable.bottom_nav_card_background)
    }


    private fun registerNetworkListener()
    {
        val connectivityManager:ConnectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        dialog = Utilities.dialogIcon(this,R.drawable.ic_network_error, R.string.network_error,R.string.network_pos,null,true,false,{
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        },null)

        connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(),object : ConnectivityManager.NetworkCallback()
        {
            override fun onAvailable(network: Network)
            {
                super.onAvailable(network)
                if (dialog.isShowing)
                {
                    runOnUiThread({
                        //TODO Dialog dismiss problem
                        println(Thread.currentThread().name)
                        dialog.dismiss()
                        dialog.cancel()
                    })
                }
            }

            override fun onLost(network: Network)
            {
                super.onLost(network)
                if (!dialog.isShowing)
                {
                    dialog.show()
                }
            }

            override fun onUnavailable()
            {
                super.onUnavailable()
                if (!dialog.isShowing)
                {
                    dialog.show()
                }
            }
        })
    }
}