package com.amirhosseinemadi.appstore.view.bottomsheet

import android.os.Bundle
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.model.content.BlurEffect
import com.amirhosseinemadi.appstore.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LoginFragment : BottomSheetDialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_login,null,false)
    }


    override fun getTheme(): Int {
        return R.style.bottom_sheet_theme
    }

}