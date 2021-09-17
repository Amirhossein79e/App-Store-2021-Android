package com.amirhosseinemadi.appstore.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentIntroBinding

class IntroFragment : Fragment() {

    private lateinit var introBinding: FragmentIntroBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        introBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_intro,container,false)

        return introBinding.root
    }

}