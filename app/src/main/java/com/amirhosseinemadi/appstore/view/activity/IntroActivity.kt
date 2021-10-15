package com.amirhosseinemadi.appstore.view.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.ActivityIntroBinding
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.adapter.IntroPagerAdapter
import com.amirhosseinemadi.appstore.view.fragment.IntroFragment
import com.google.android.material.tabs.TabLayout

class IntroActivity : AppCompatActivity() {

    private lateinit var introBinding:ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        introBinding = DataBindingUtil.setContentView(this,R.layout.activity_intro)
        initView()
        initIndicator()
    }


    private fun initView()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            Utilities.underApiStatusBarHandle(this)
        }

        introBinding.btnNext.setOnClickListener(this::nextClick)
    }


    private fun nextClick(view: View)
    {
        if (introBinding.viewPager.currentItem < introBinding.viewPager.adapter?.itemCount!! -1)
        {
            introBinding.viewPager.currentItem = introBinding.viewPager.currentItem+1
        }else
        {
            PrefManager.setFirst(false)
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }


    private fun initIndicator()
    {
        val fragment:MutableList<Fragment> = ArrayList()
        fragment.add(IntroFragment(R.drawable.ic_welcome_intro,R.string.intro_title_one,R.string.intro_sub_one))
        fragment.add(IntroFragment(R.drawable.ic_comment_intro,R.string.intro_title_two,R.string.intro_sub_two))
        fragment.add(IntroFragment(R.drawable.ic_develop_intro,R.string.intro_title_three,R.string.intro_sub_three))

        val pagerAdapter:IntroPagerAdapter = IntroPagerAdapter(supportFragmentManager,this,fragment)
        introBinding.viewPager.adapter = pagerAdapter

        val views:MutableList<View> = ArrayList()

        for (i:Int in 0 until fragment.size)
        {
            val view:View = layoutInflater.inflate(R.layout.view_pager_indicator_item,introBinding.linearIndicator,false)
            view.findViewById<AppCompatTextView>(R.id.txt_indicator).setOnClickListener { introBinding.viewPager.currentItem = i }
            views.add(view)
            introBinding.linearIndicator.addView(view)
        }

        introBinding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback()
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
                if (position == introBinding.viewPager.adapter?.itemCount!! -1)
                {
                    introBinding.btnNext.text = resources.getString(R.string.finish)
                }else
                {
                    introBinding.btnNext.text = resources.getString(R.string.next)
                }
            }
        })
    }
}