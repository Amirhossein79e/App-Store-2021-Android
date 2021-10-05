package com.amirhosseinemadi.appstore.view.fragment

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentAccountBinding
import com.amirhosseinemadi.appstore.view.bottomsheet.LoginFragment
import com.amirhosseinemadi.appstore.viewmodel.AccountVm

class AccountFragment : Fragment() {

    private lateinit var accountBinding:FragmentAccountBinding
    private lateinit var viewModel:AccountVm

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        viewModel = AccountVm()
        accountBinding = DataBindingUtil.inflate<FragmentAccountBinding>(inflater, R.layout.fragment_account,null,false).also { it.viewModel = viewModel }


        accountBinding.btnLog.setOnClickListener({
            LoginFragment().show(parentFragmentManager,"")
        })


        return accountBinding.root
    }

}