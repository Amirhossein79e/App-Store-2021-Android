package com.amirhosseinemadi.appstore.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.ActivityIntroBinding
import com.amirhosseinemadi.appstore.view.adapter.IntroPagerAdapter
import com.amirhosseinemadi.appstore.view.fragment.IntroFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class IntroActivity : AppCompatActivity() {

    private lateinit var introBinding:ActivityIntroBinding
    private lateinit var viewPager:ViewPager2
    private lateinit var tabLayout:TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        introBinding = DataBindingUtil.setContentView(this,R.layout.activity_intro)
        initView()

        val fragment:MutableList<Fragment> = ArrayList()
        val one:Fragment = IntroFragment()
        val two:Fragment = IntroFragment()
        val three:Fragment = IntroFragment()
        fragment.add(one)
        fragment.add(two)
        fragment.add(three)
        val pagerAdapter:IntroPagerAdapter = IntroPagerAdapter(supportFragmentManager,this,fragment)
        viewPager.adapter = pagerAdapter
        TabLayoutMediator(tabLayout,viewPager,{ tab, position -> }).attach()


    }

    private fun initView()
    {
        viewPager = introBinding.viewPager
        tabLayout = introBinding.tabLayout
    }
}