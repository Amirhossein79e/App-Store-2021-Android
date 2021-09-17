package com.amirhosseinemadi.appstore.util.dagger

import android.content.Context
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.view.activity.MainActivity
import dagger.Component
import retrofit2.Retrofit
import javax.inject.Singleton

@Singleton
@Component(modules = [Module::class])
interface Component {

    fun context() : Context

    fun retrofit() : Retrofit

    fun apiCaller() : ApiCaller

    fun inject(activity: MainActivity)

}