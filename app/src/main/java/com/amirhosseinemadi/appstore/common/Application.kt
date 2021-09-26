package com.amirhosseinemadi.appstore.common

import android.app.Application
import android.app.Dialog
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import com.amirhosseinemadi.appstore.util.dagger.Component
import com.amirhosseinemadi.appstore.util.dagger.DaggerComponent
import com.amirhosseinemadi.appstore.util.dagger.Module
import com.google.android.datatransport.cct.internal.NetworkConnectionInfo

class Application : Application() {

    private lateinit var dialog:Dialog

    companion object
    {
        public lateinit var component: Component
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerComponent.builder().module(Module(applicationContext)).build()

        val connectivityManager:ConnectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(),object : ConnectivityManager.NetworkCallback()
        {
            override fun onAvailable(network: Network)
            {
                super.onAvailable(network)
            }

            override fun onLost(network: Network)
            {
                super.onLost(network)
            }

            override fun onUnavailable()
            {
                super.onUnavailable()
            }
        })
    }

}