package com.amirhosseinemadi.appstore.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.ActivityIntroBinding
import com.amirhosseinemadi.appstore.view.adapter.IntroPagerAdapter
import com.amirhosseinemadi.appstore.view.fragment.IntroFragment
import com.google.android.material.tabs.TabLayout

class IntroActivity : AppCompatActivity() {

    private lateinit var introBinding:ActivityIntroBinding
    private lateinit var viewPager:ViewPager2
    private lateinit var linearIndicator:LinearLayout
    private lateinit var btnNext:AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        introBinding = DataBindingUtil.setContentView(this,R.layout.activity_intro)
        initView()
        initIndicator()
    }


    private fun initView()
    {
        viewPager = introBinding.viewPager
        linearIndicator = introBinding.linearIndicator
        btnNext = introBinding.btnNext
    }


    private fun initIndicator()
    {
        val fragment:MutableList<Fragment> = ArrayList()
        fragment.add(IntroFragment())
        fragment.add(IntroFragment())
        fragment.add(IntroFragment())

        val pagerAdapter:IntroPagerAdapter = IntroPagerAdapter(supportFragmentManager,this,fragment)
        viewPager.adapter = pagerAdapter

        val views:MutableList<View> = ArrayList()

        for (i:Int in 0 until fragment.size)
        {
            val view:View = layoutInflater.inflate(R.layout.view_pager_indicator_item,null)
            view.findViewById<AppCompatTextView>(R.id.txt_indicator).setOnClickListener { viewPager.currentItem = i }
            views.add(view)
            linearIndicator.addView(view)
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback()
        {
            override fun onPageSelected(position: Int)
            {
                super.onPageSelected(position)
                for (i:Int in 0 until views.size)
                {
                    if (views.get(i).isSelected)
                    {
                        views.get(i).isSelected = false
                    }
                }
                views.get(position).isSelected = true
            }
        })
    }
}