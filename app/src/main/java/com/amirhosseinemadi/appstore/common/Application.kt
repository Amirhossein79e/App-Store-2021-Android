package com.amirhosseinemadi.appstore.common

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.util.dagger.Component
import com.amirhosseinemadi.appstore.util.dagger.DaggerComponent
import com.amirhosseinemadi.appstore.util.dagger.Module

class Application : Application() {

    companion object
    {
        lateinit var component: Component
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerComponent.builder().module(Module(applicationContext)).build()

        when(PrefManager.getTheme())
        {
            "dark" ->
            {
                if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES)
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }

            "light" ->
            {
                if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO)
                {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
    }

}