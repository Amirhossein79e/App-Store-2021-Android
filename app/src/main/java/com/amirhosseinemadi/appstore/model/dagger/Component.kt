package com.amirhosseinemadi.appstore.model.dagger

import android.content.Context
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.model.dagger.Module
import com.amirhosseinemadi.appstore.view.MainActivity
import dagger.Component
import retrofit2.Retrofit
import javax.inject.Singleton

@Singleton
@Component(modules = [Module::class])
interface Component {

    fun context() : Context

    fun retrofit() : Retrofit

    fun apiCaller() : ApiCaller

    fun inject(activity:MainActivity)

}