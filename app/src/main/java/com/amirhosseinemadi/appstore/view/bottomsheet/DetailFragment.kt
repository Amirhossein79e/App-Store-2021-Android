package com.amirhosseinemadi.appstore.view.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DetailFragment(private val text:String) : BottomSheetDialogFragment() {

    private lateinit var fragmentDetailBinding:FragmentDetailBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentDetailBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_detail,container,false)
        fragmentDetailBinding.lifecycleOwner = this
        initView()

        return fragmentDetailBinding.root
    }


    private fun initView()
    {
        fragmentDetailBinding.txt.text = text
    }


    override fun getTheme(): Int {
        return R.style.bottom_sheet_theme
    }

}