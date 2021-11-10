package com.amirhosseinemadi.appstore.view.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentAccountBinding
import com.amirhosseinemadi.appstore.model.entity.AppModel
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.activity.MainActivity
import com.amirhosseinemadi.appstore.view.activity.SettingsActivity
import com.amirhosseinemadi.appstore.view.adapter.AppRecyclerAdapter
import com.amirhosseinemadi.appstore.view.bottomsheet.LoginFragment
import com.amirhosseinemadi.appstore.view.callback.AccountCallback
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.amirhosseinemadi.appstore.viewmodel.AccountVm
import com.google.android.material.snackbar.BaseTransientBottomBar

class AccountFragment : Fragment(),AccountCallback,Callback {

    private val viewModel:AccountVm
    private lateinit var accountBinding:FragmentAccountBinding
    private lateinit var loading: Dialog
    private var more:Boolean
    private var appList:MutableList<AppModel>

    init
    {
        viewModel = AccountVm(this)
        more = true
        appList = ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        accountBinding = DataBindingUtil.inflate<FragmentAccountBinding>(inflater, R.layout.fragment_account,null,false).also { it.viewModel = viewModel }
        accountBinding.lifecycleOwner = this
        loading = Utilities.loadingDialog(requireContext())
        initView()
        handleError()
        checkAccountInit()

        return accountBinding.root
    }


    private fun initView()
    {
        accountBinding.btnSettings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

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

                Utilities.showSnack(requireActivity().findViewById(R.id.coordinator),requireContext().getString(R.string.sign_out_successful),BaseTransientBottomBar.LENGTH_SHORT)
            }
        }else
        {
            accountBinding.btnLog.setOnClickListener {
                accountBinding.txtWord.text = requireContext().getText(R.string.user).substring(0,1).uppercase()
                accountBinding.txtUser.text = requireContext().getText(R.string.user)
                accountBinding.btnLog.text = requireContext().getText(R.string.sign_in)

                LoginFragment(this).show(requireActivity().supportFragmentManager,"")
            }

            accountBinding.txtWord.text = requireContext().getText(R.string.user).substring(0,1).uppercase()
            checkUpdate()
        }


        accountBinding.recycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        accountBinding.recycler.adapter = AppRecyclerAdapter(requireContext(),appList,object : Callback
        {
            override fun notify(vararg obj: Any?)
            {
                requireActivity().supportFragmentManager.beginTransaction().add(R.id.frame,AppFragment(obj[0] as String),"appFragment").addToBackStack("appFragment").commit()
            }
        })

        accountBinding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (appList.size % 10 == 0 && more && (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() >= appList!!.size-2 && !loading.isShowing)
                {
                    viewModel.update(appList.size,Utilities.getAllPackages(),false)
                }
            }
        })

        Utilities.onBackPressed(accountBinding.root,object : Callback
        {
            override fun notify(vararg obj: Any?)
            {
                backPressed()
            }
        })

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
                            "validateUser" -> { viewModel.validateUser(PrefManager.getAccess()!!) }

                            "update" -> { viewModel.update(appList!!.size,Utilities.getAllPackages(),false) }
                        }
                    }

                }),"error").commit()
            })
    }


    private fun checkAccountInit()
    {
        if (PrefManager.checkSignIn())
        {
            viewModel.validateUser(PrefManager.getAccess()!!)
            viewModel.validateResponse
                .observe(viewLifecycleOwner,
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
                        checkUpdate()
                    })
        }
    }


    private fun checkUpdate()
    {
        viewModel.update(0,Utilities.getAllPackages(),PrefManager.checkSignIn())
        viewModel.updateResponse
            .observe(viewLifecycleOwner,
                {
                    if (it.responseCode == 1)
                    {
                        if(it.data != null)
                        {
                            more = true
                            appList.addAll(it.data!!)
                            accountBinding.recycler.adapter?.notifyItemRangeInserted(appList.size - it.data!!.size, it.data!!.size)
                        }else
                        {
                            more = false
                            if (appList.size == 0)
                            {
                                accountBinding.img.visibility = View.VISIBLE
                                accountBinding.txt.visibility = View.VISIBLE
                            }
                        }
                    }else
                    {
                        Utilities.showSnack(requireActivity().findViewById(R.id.coordinator),it.message!!,BaseTransientBottomBar.LENGTH_SHORT)
                    }
                })
    }


    private fun backPressed()
    {
        requireActivity().finish()
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

    override fun onMessage(res: Int) {

    }

    override fun getStr(res: Int): String {
        return ""
    }

    override fun notify(vararg obj: Any?) {
        initView()
    }


}