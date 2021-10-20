package com.amirhosseinemadi.appstore.view.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentHomeBinding
import com.amirhosseinemadi.appstore.model.entity.AppModel
import com.amirhosseinemadi.appstore.model.entity.HomeCategoryModel
import com.amirhosseinemadi.appstore.model.entity.ResponseObject
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.adapter.MainPagerAdapter
import com.amirhosseinemadi.appstore.view.adapter.MainRecyclerAdapter
import com.amirhosseinemadi.appstore.view.adapter.SubRecyclerAdapter
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.amirhosseinemadi.appstore.view.callback.HomeCallback
import com.amirhosseinemadi.appstore.viewmodel.HomeVm
import com.google.android.material.snackbar.BaseTransientBottomBar

class HomeFragment : Fragment(),HomeCallback {

    private lateinit var viewModel:HomeVm
    private lateinit var homeBinding:FragmentHomeBinding
    private lateinit var loading:Dialog
    private lateinit var snapHelper:PagerSnapHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = HomeVm(this)
        homeBinding = DataBindingUtil.inflate<FragmentHomeBinding>(inflater,R.layout.fragment_home,container,false).also { it.viewModel = viewModel }
        homeBinding.root.layoutParams = ConstraintLayout.LayoutParams(container?.layoutParams)
        homeBinding.lifecycleOwner = this
        loading = Utilities.loadingDialog(requireContext())
        initView()
        handleError()
        home()

        return homeBinding.root
    }


    private fun initView()
    {
        LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false).also { homeBinding.pager.layoutManager = it }
        snapHelper = PagerSnapHelper().also { it.attachToRecyclerView(homeBinding.pager) }
        LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false).also { homeBinding.recycler.layoutManager = it }
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
                            "home" -> { home() }
                        }
                    }

                }),"error").commit()
            })
    }


    private fun home()
    {
        viewModel.getHomeResponse()
            .observe(requireActivity(),
                {
                    if(it.responseCode == 1)
                    {
                        homeBinding.pager.adapter = MainPagerAdapter(requireContext(), it.data?.slider as List<String>)
                        homeBinding.recycler.adapter = MainRecyclerAdapter(requireContext(),it.data?.rows as List<HomeCategoryModel>, object : Callback
                        {
                            override fun notify(vararg obj: Any?)
                            {
                                val recycler:RecyclerView = obj[1] as RecyclerView
                                viewModel.app(obj[0] as String,object : Callback
                                {
                                    override fun notify(vararg obj: Any?)
                                    {
                                        recycler.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)
                                        recycler.adapter = SubRecyclerAdapter(requireActivity(),viewModel.appResponse.value?.data!!, object : Callback
                                        {
                                            override fun notify(vararg obj: Any?)
                                            {

                                            }
                                        })
                                    }
                                })
                            }
                        })
                    }else
                    {
                        Utilities.showSnack(requireActivity().findViewById(R.id.coordinator),it.message!!,BaseTransientBottomBar.LENGTH_SHORT)
                    }
                })
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

}