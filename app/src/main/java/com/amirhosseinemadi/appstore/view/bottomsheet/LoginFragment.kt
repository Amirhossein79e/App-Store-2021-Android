package com.amirhosseinemadi.appstore.view.bottomsheet

import android.os.Bundle
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.airbnb.lottie.model.content.BlurEffect
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentLoginBinding
import com.amirhosseinemadi.appstore.viewmodel.AccountVm
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LoginFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel:AccountVm
    private lateinit var loginBinding:FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = AccountVm()
        loginBinding = DataBindingUtil.inflate<FragmentLoginBinding>(inflater,R.layout.fragment_login,null,false).also{ it.viewModel = viewModel}

        return loginBinding.root
    }


    override fun getTheme(): Int {
        return R.style.bottom_sheet_theme
    }

}