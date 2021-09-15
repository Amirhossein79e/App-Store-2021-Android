package com.amirhosseinemadi.appstore.view.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter

class IntroPagerAdapter(fragmentManager: FragmentManager,activity:AppCompatActivity) : FragmentStateAdapter(fragmentManager,activity.lifecycle) {

    private var fragments:MutableList<Fragment>

    init
    {
        fragments = ArrayList()
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments.get(position)
    }
}