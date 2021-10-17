package com.amirhosseinemadi.appstore.view.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentHomeBinding
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.adapter.MainPagerAdapter
import com.amirhosseinemadi.appstore.view.callback.HomeCallback
import com.amirhosseinemadi.appstore.viewmodel.HomeVm
import com.google.android.material.snackbar.BaseTransientBottomBar

class HomeFragment : Fragment(),HomeCallback {

    private lateinit var viewModel:HomeVm
    private lateinit var homeBinding:FragmentHomeBinding
    private lateinit var loading:Dialog
    private lateinit var snapHelper:PagerSnapHelper
    private lateinit var layoutManager:LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = HomeVm()
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
        layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false).also { homeBinding.pager.layoutManager = it }
        snapHelper = PagerSnapHelper().also { it.attachToRecyclerView(homeBinding.pager) }
    }


    private fun handleError()
    {
        viewModel.error.observe(requireActivity(),
            {
                Utilities.showSnack(requireActivity().findViewById(R.id.coordinator), requireContext().getString(R.string.request_failed),
                    BaseTransientBottomBar.LENGTH_SHORT)
            })
    }


    private fun home()
    {
        viewModel.getHomeResponse()
            .observe(requireActivity(),
                {
                    homeBinding.pager.adapter = MainPagerAdapter(requireContext(),it.data?.slider as List<String>)
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