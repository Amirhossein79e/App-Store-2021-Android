package com.amirhosseinemadi.appstore.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {

    private lateinit var activityIntroBinding:ActivityIntroBinding
    private lateinit var viewPager:ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityIntroBinding = DataBindingUtil.setContentView(this,R.layout.activity_intro)
        initView()

    }

    private fun initView()
    {
        viewPager = activityIntroBinding.viewPager
    }
}