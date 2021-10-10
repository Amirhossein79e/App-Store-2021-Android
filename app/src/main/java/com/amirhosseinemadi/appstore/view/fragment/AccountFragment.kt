package com.amirhosseinemadi.appstore.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentAccountBinding
import com.amirhosseinemadi.appstore.view.bottomsheet.LoginFragment
import com.amirhosseinemadi.appstore.view.callback.AccountCallback
import com.amirhosseinemadi.appstore.viewmodel.AccountVm

class AccountFragment : Fragment(),AccountCallback {

    private lateinit var viewModel:AccountVm
    private lateinit var accountBinding:FragmentAccountBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        viewModel = AccountVm(this)
        accountBinding = DataBindingUtil.inflate<FragmentAccountBinding>(inflater, R.layout.fragment_account,null,false).also { it.viewModel = viewModel }

        accountBinding.btnLog.setOnClickListener(
            {
                LoginFragment().show(requireActivity().supportFragmentManager,"")
            })

        return accountBinding.root
    }

    override fun onShow() {
        TODO("Not yet implemented")
    }

    override fun onHide() {
        TODO("Not yet implemented")
    }

    override fun signUp(email: String, password: String, username: String, token: String) {
        TODO("Not yet implemented")
    }

    override fun signIn(email: String, password: String) {
        TODO("Not yet implemented")
    }

    override fun onMessage(message: String) {
        TODO("Not yet implemented")
    }


}