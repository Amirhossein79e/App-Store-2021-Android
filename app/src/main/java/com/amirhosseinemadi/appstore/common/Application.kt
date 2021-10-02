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

    companion object
    {
        public lateinit var component: Component
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerComponent.builder().module(Module(applicationContext)).build()
    }

}