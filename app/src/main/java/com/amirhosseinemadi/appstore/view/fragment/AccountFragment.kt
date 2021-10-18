package com.amirhosseinemadi.appstore.view.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentAccountBinding
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.bottomsheet.LoginFragment
import com.amirhosseinemadi.appstore.view.callback.AccountCallback
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.amirhosseinemadi.appstore.viewmodel.AccountVm
import com.google.android.material.snackbar.BaseTransientBottomBar

class AccountFragment : Fragment(),AccountCallback,Callback {

    private lateinit var viewModel:AccountVm
    private lateinit var accountBinding:FragmentAccountBinding
    private lateinit var loading: Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        viewModel = AccountVm(this)
        accountBinding = DataBindingUtil.inflate<FragmentAccountBinding>(inflater, R.layout.fragment_account,null,false).also { it.viewModel = viewModel }
        accountBinding.lifecycleOwner = this
        loading = Utilities.loadingDialog(requireContext())
        initView()
        handleError()
        checkAccount()

        return accountBinding.root
    }


    private fun initView()
    {
        if (PrefManager.checkSignIn())
        {
            accountBinding.txtUser.text = PrefManager.getUser()
            accountBinding.txtWord.text = PrefManager.getUser()!!.substring(0,1).uppercase()
            accountBinding.btnLog.text = requireContext().getText(R.string.sign_out)

            accountBinding.btnLog.setOnClickListener {
                accountBinding.txtWord.text = requireContext().getText(R.string.user).substring(0,1).uppercase()
                accountBinding.txtUser.text = requireContext().getText(R.string.user)
                accountBinding.btnLog.text = requireContext().getText(R.string.sign_in)

                PrefManager.setUser(null)
                PrefManager.setAccess(null)

                accountBinding.btnLog.setOnClickListener {
                    LoginFragment(this).show(requireActivity().supportFragmentManager,"")
                }

                Utilities.showSnack(requireActivity().findViewById(R.id.coordinator),requireContext().getString(R.string.sign_out_successfull),BaseTransientBottomBar.LENGTH_SHORT)
            }
        }else
        {
            accountBinding.btnLog.setOnClickListener {
                accountBinding.txtWord.text = requireContext().getText(R.string.user).substring(0,1).uppercase()
                accountBinding.txtUser.text = requireContext().getText(R.string.user)
                accountBinding.btnLog.text = requireContext().getText(R.string.sign_in)

                LoginFragment(this).show(requireActivity().supportFragmentManager,"")
            }
        }
    }


    private fun handleError()
    {
        viewModel.error.observe(requireActivity(),
            {
                requireActivity().supportFragmentManager.beginTransaction().add(R.id.frame,ErrorFragment(object : Callback
                {
                    override fun notify(vararg obj: Any?)
                    {
                        val fragment:Fragment? = requireActivity().supportFragmentManager.findFragmentByTag("error")

                        if (fragment != null)
                        {
                            requireActivity().supportFragmentManager.beginTransaction().remove(fragment).commit()
                        }
                        when(it)
                        {
                            "validateUser" -> { checkAccount() }
                        }
                    }

                }),"error").commit()
            })
    }


    private fun checkAccount()
    {
        if (PrefManager.checkSignIn())
        {
            viewModel.getValidateResponse(PrefManager.getAccess()!!)
                .observe(requireActivity(),
                    {
                        when(it.responseCode)
                        {
                            1 -> {}

                            else ->
                            {
                                PrefManager.setUser(null)
                                PrefManager.setAccess(null)
                                accountBinding.txtWord.text = requireContext().getText(R.string.user).substring(0,1).uppercase()
                                accountBinding.txtUser.text = requireContext().getText(R.string.user)
                                accountBinding.btnLog.text = requireContext().getText(R.string.sign_in)
                                accountBinding.btnLog.setOnClickListener {
                                    LoginFragment(this).show(requireActivity().supportFragmentManager,"")
                                }
                            }
                        }
                    })
        }
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

    }

    override fun signIn(email: String, password: String) {

    }

    override fun onMessage(message: String) {

    }

    override fun notify(vararg obj: Any?) {
        initView()
    }


}