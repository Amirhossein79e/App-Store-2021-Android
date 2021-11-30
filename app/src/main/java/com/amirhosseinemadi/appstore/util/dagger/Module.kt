package com.amirhosseinemadi.appstore.util.dagger

import android.content.Context
import android.util.Base64
import com.amirhosseinemadi.appstore.BuildConfig
import com.amirhosseinemadi.appstore.util.CustomInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.CertificateFactorySpi
import java.security.cert.X509CertSelector
import javax.security.cert.X509Certificate
import java.security.spec.X509EncodedKeySpec
import javax.inject.Singleton

@Module
class Module(private val context: Context) {


    @Provides
    fun context() : Context
    {
        return context
    }


    @Provides
    @Singleton
    fun retrofit() : Retrofit
    {


        val certificatePinner:CertificatePinner = CertificatePinner.Builder()
            .add("https://bermoodaco.ir/",BuildConfig.CERTIFICATE)
            .build()

        val client:OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(CustomInterceptor())
            .certificatePinner(certificatePinner)
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