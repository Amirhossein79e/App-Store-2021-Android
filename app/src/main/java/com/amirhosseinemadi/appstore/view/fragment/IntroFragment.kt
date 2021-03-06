package com.amirhosseinemadi.appstore.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentIntroBinding

class IntroFragment() : Fragment() {

    private lateinit var introBinding: FragmentIntroBinding
    private var imgRes:Int
    private var txtTitleRes:Int
    private var txtSubRes:Int

    init
    {
        imgRes = 0
        txtTitleRes = 0
        txtSubRes = 0
    }

    constructor(@DrawableRes imgRes:Int, @StringRes txtTitleRes:Int, @StringRes txtSubRes:Int) : this()
    {
        this.imgRes = imgRes
        this.txtTitleRes = txtTitleRes
        this.txtSubRes = txtSubRes
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        introBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_intro,container,false)
        setAttrs()
        return introBinding.root
    }


    private fun setAttrs()
    {
        introBinding.img.setImageDrawable(ContextCompat.getDrawable(requireContext(),imgRes))
        introBinding.txtTitle.text = resources.getString(txtTitleRes)
        introBinding.txtSub.text = resources.getString(txtSubRes)
    }

}