package com.amirhosseinemadi.appstore.util.dagger

import android.content.Context
import com.amirhosseinemadi.appstore.util.CustomInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class Module(private val context: Context) {


    @Provides
    public fun context() : Context
    {
        return context
    }


    @Provides
    @Singleton
    public fun retrofit() : Retrofit
    {
        val client:OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(CustomInterceptor())
            .build()

        val retrofit:Retrofit = Retrofit.Builder()
            .baseUrl("https://bermoodaco.ir/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(client)
            .build()

        return retrofit
    }

}