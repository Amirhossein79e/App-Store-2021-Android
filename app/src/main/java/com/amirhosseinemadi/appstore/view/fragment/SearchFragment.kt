package com.amirhosseinemadi.appstore.view.fragment

import android.app.ActionBar
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleCursorAdapter
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentSearchBinding
import com.amirhosseinemadi.appstore.model.entity.AppModel
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.adapter.AppRecyclerAdapter
import com.amirhosseinemadi.appstore.view.adapter.TitleRecyclerAdapter
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.amirhosseinemadi.appstore.view.callback.SearchCallback
import com.amirhosseinemadi.appstore.viewmodel.SearchVm
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment(),SearchCallback {

    private var viewModel:SearchVm
    private lateinit var searchBinding:FragmentSearchBinding
    private lateinit var loading:Dialog
    private var more:Boolean
    private val appList:MutableList<AppModel>
    private val titleList:MutableList<String>

    init
    {
        viewModel = SearchVm(this)
        more = true
        appList = ArrayList()
        titleList = ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        searchBinding = DataBindingUtil.inflate<FragmentSearchBinding>(inflater,R.layout.fragment_search,container,false).also { it.viewModel = viewModel }
        loading = Utilities.loadingDialog(requireContext())
        searchBinding.lifecycleOwner = this
        initView()
        return searchBinding.root
    }


    private fun initView()
    {
        searchBinding.sch.setOnQueryTextListener(object : SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                title(newText!!)
                return false
            }
        })

        searchBinding.recycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        searchBinding.recycler.adapter = AppRecyclerAdapter(requireContext(),appList,object : Callback
        {
            override fun notify(vararg obj: Any?)
            {
                TODO("Not yet implemented")
            }
        })
        searchBinding.recyclerTitle.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        searchBinding.recyclerTitle.adapter = TitleRecyclerAdapter(requireContext(),titleList,object : Callback
        {
            override fun notify(vararg obj: Any?)
            {
                TODO("Not yet implemented")
            }
        })
    }


    private fun title(title:String)
    {
        if (!viewModel.titleResponse.hasObservers())
        {
            viewModel.getTitleResponse(title)
                .observe(viewLifecycleOwner,
                    {
                        titleList.clear()
                        if (it.data != null)
                        {
                            titleList.addAll(it.data!!)
                        }
                        searchBinding.recyclerTitle.adapter?.notifyDataSetChanged()
                    })
        }else
        {
            viewModel.title(title)
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

}