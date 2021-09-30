package com.amirhosseinemadi.appstore.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.model.entity.*
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var apiCaller: ApiCaller

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Application.component.inject(this)

        apiCaller.getCategories(object : SingleObserver<ResponseObject<List<CategoryModel>>>
        {
            override fun onSubscribe(d: Disposable?) {

            }

            override fun onSuccess(t: ResponseObject<List<CategoryModel>>?) {
                if(t?.responseCode == 1)
                {
                    println(t.data?.get(0)?.icon)
                }else
                {
                    println(t?.message)
                }
            }

            override fun onError(e: Throwable?) {
                e?.printStackTrace()

            }

        })


    }
}