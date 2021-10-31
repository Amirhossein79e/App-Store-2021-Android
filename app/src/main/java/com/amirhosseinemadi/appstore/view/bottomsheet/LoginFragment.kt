package com.amirhosseinemadi.appstore.view.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentLoginBinding
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.callback.AccountCallback
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.amirhosseinemadi.appstore.viewmodel.AccountVm
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.BaseTransientBottomBar

class LoginFragment(val callback: Callback) : BottomSheetDialogFragment(),AccountCallback {

    private lateinit var viewModel:AccountVm
    private lateinit var loginBinding:FragmentLoginBinding
    private lateinit var loading:Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = AccountVm(this)
        loginBinding = DataBindingUtil.inflate<FragmentLoginBinding>(inflater,R.layout.fragment_login,container,false).also{ it.viewModel = viewModel}
        loginBinding.lifecycleOwner = this
        loading = Utilities.loadingDialog(requireContext())
        handleError()

        return loginBinding.root
    }


    private fun handleError()
    {
        viewModel.error.observe(viewLifecycleOwner,
            {
                Utilities.showSnack(loginBinding.coordinator, requireContext().getString(R.string.request_failed),BaseTransientBottomBar.LENGTH_SHORT)
            })
    }


    override fun getTheme(): Int {
        return R.style.bottom_sheet_theme
    }


    override fun onShow() {
        if (!loading.isShowing)
        {
            loading.show()
        }
    }


    override fun onHide() {
        if (loading.isShowing)
        {
            loading.dismiss()
        }
    }


    override fun signUp(email: String, password: String, username: String, token: String) {

        if (!viewModel.signUpResponse.hasObservers())
        {
            viewModel.getSignUpResponse(email, password, username, token)
                .observe(viewLifecycleOwner,
                    {
                        when (it.responseCode)
                        {
                            1 ->
                            {
                                PrefManager.setUser(it.data?.userName!!)
                                PrefManager.setAccess(it.data?.access!!)
                                callback.notify()
                                dismiss()
                            }

                            else -> Utilities.showSnack(
                                loginBinding.coordinator,
                                it.message!!,
                                BaseTransientBottomBar.LENGTH_SHORT
                            )
                        }
                    })
        }else
        {
            viewModel.signUp(email, password, username, token)
        }
    }


    override fun signIn(email: String, password: String) {

        if (!viewModel.signInResponse.hasObservers())
        {
            viewModel.getSignInResponse(email, password)
                .observe(viewLifecycleOwner,
                    {
                        when (it.responseCode)
                        {
                            1 ->
                            {
                                PrefManager.setUser(it.data?.userName!!)
                                PrefManager.setAccess(it.data?.access!!)
                                callback.notify()
                                dismiss()
                            }

                            else -> Utilities.showSnack(
                                loginBinding.coordinator,
                                it.message!!,
                                BaseTransientBottomBar.LENGTH_SHORT
                            )
                        }
                    })
        }else
        {
            viewModel.signIn(email,password)
        }

    }


    override fun onMessage(res: Int) {
        Utilities.showSnack(loginBinding.coordinator,getString(res),BaseTransientBottomBar.LENGTH_SHORT)
    }

    override fun getStr(res: Int) : String {
        return requireActivity().getString(res)
    }


}