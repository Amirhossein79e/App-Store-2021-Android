package com.amirhosseinemadi.appstore.common

import android.app.Application
import com.amirhosseinemadi.appstore.model.dagger.Component
import com.amirhosseinemadi.appstore.model.dagger.Module

class Application : Application() {

    companion object
    {
        public lateinit var component: Component;
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerComponent.builder().module(Module(applicationContext)).build()
    }

}