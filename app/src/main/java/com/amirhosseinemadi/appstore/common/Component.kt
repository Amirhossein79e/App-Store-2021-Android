package com.amirhosseinemadi.appstore.common

import android.content.Context
import dagger.Component
import retrofit2.Retrofit
import javax.inject.Singleton

@Singleton
@Component(modules = [Module::class])
interface Component {

    fun context() : Context

    fun retrofit() : Retrofit

}