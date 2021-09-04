package com.amirhosseinemadi.appstore.view
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.widget.Toast
import com.amirhosseinemadi.appstore.BuildConfig
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.common.Module
import com.amirhosseinemadi.appstore.model.Service
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.util.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val service:Service = Application.component.retrofit().create(Service::class.java)
        val test = Random.nextBytes(155)
        val str = "{\"uid\":\"12345678\",\"token\":\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\"}"
        val  call:Call<ResponseBody> = service.test(100,str)

        call.enqueue(object : Callback<ResponseBody>
        {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                println(response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MainActivity,"FAILED",Toast.LENGTH_LONG).show()
                t.printStackTrace()
                println(t.message+"!!!!!!!!!!!!!!!!!!")
            }
        })

    }
}