package com.amirhosseinemadi.appstore.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.viewmodel.SplashVm

class SplashActivity : AppCompatActivity() {

    lateinit var viewModel:SplashVm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = SplashVm()




    }
}