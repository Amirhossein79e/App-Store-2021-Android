package com.amirhosseinemadi.appstore.common

import android.app.Application
import android.app.Dialog
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.net.Uri
import android.provider.Settings
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.util.Utilities
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
        dialog = Utilities.dialogIcon(null, R.string.network_error,R.string.network_pos,null,true,false,{
            startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(Settings.ACTION_NETWORK_OPERATOR_SETTINGS)))
        },null)

        connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(),object : ConnectivityManager.NetworkCallback()
        {
            override fun onAvailable(network: Network)
            {
                super.onAvailable(network)
                if (dialog.isShowing)
                {
                    dialog.dismiss()
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