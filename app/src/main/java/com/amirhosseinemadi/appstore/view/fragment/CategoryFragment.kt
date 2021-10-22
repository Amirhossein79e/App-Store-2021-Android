package com.amirhosseinemadi.appstore.view.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentCategoryBinding
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.adapter.CategoryRecyclerAdapter
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.amirhosseinemadi.appstore.view.callback.CategoryCallback
import com.amirhosseinemadi.appstore.viewmodel.CategoryVm

class CategoryFragment : Fragment(),CategoryCallback {

    private lateinit var viewModel:CategoryVm
    private lateinit var categoryBinding:FragmentCategoryBinding
    private lateinit var dialog:Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = CategoryVm(this)
        categoryBinding = DataBindingUtil.inflate<FragmentCategoryBinding>(inflater,R.layout.fragment_category,container,false).also{ it.viewModel = viewModel }
        dialog = Utilities.loadingDialog(requireContext())
        initView()
        category()

        return categoryBinding.root
    }


    private fun initView()
    {
        categoryBinding.recycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
    }


    private fun category()
    {
        viewModel.getCategoryResponse()
            .observe(viewLifecycleOwner,
                {
                    categoryBinding.recycler.adapter = CategoryRecyclerAdapter(requireContext(),it.data!!,object : Callback
                    {
                        override fun notify(vararg obj: Any?)
                        {

                        }
                    })
                })
    }


    override fun onShow() {
        if (!dialog.isShowing)
        {
            dialog.show()
        }
    }

    override fun onHide() {
        if (dialog.isShowing)
        {
            dialog.dismiss()
        }
    }

}