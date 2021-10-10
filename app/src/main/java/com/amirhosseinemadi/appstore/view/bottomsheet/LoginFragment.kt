package com.amirhosseinemadi.appstore.view.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentLoginBinding
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.callback.AccountCallback
import com.amirhosseinemadi.appstore.viewmodel.AccountVm
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.BaseTransientBottomBar

class LoginFragment : BottomSheetDialogFragment(),AccountCallback {

    private lateinit var viewModel:AccountVm
    private lateinit var loginBinding:FragmentLoginBinding
    private lateinit var loading:Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = AccountVm(this)
        loginBinding = DataBindingUtil.inflate<FragmentLoginBinding>(inflater,R.layout.fragment_login,container,false).also{ it.viewModel = viewModel}
        loginBinding.setLifecycleOwner { requireActivity().lifecycle }
        loading = Utilities.loadingDialog(requireContext())

        return loginBinding.root
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
        viewModel.getSignUpResponse(email, password, username, token)
            .observe(this,
                {
                    when(it.responseCode)
                    {
                        1 ->
                        {
                            Utilities.showSnack(loginBinding.coordinator,"حساب کاربری با موفقیت ایجاد شد",BaseTransientBottomBar.LENGTH_SHORT)
                            dismiss()
                        }

                        else -> Utilities.showSnack(loginBinding.coordinator,it.message!!,BaseTransientBottomBar.LENGTH_SHORT)
                    }
                })
    }

    override fun signIn(email: String, password: String) {
        viewModel.getSignInResponse(email, password)
            .observe(this,
                {
                    when(it.responseCode)
                    {
                        1 ->
                        {
                            Utilities.showSnack(loginBinding.coordinator,"با موفقیت وارد حساب کاربری خود شدید",BaseTransientBottomBar.LENGTH_SHORT)
                            dismiss()
                        }

                        else -> Utilities.showSnack(loginBinding.coordinator,it.message!!,BaseTransientBottomBar.LENGTH_SHORT)
                    }
                })
    }

    override fun onMessage(message: String) {
        Utilities.showSnack(loginBinding.coordinator,message,BaseTransientBottomBar.LENGTH_SHORT)
    }


}