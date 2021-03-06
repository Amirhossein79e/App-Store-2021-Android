package com.amirhosseinemadi.appstore.view.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentCategoryBinding
import com.amirhosseinemadi.appstore.model.entity.AppModel
import com.amirhosseinemadi.appstore.model.entity.ResponseObject
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.activity.MainActivity
import com.amirhosseinemadi.appstore.view.adapter.AppRecyclerAdapter
import com.amirhosseinemadi.appstore.view.adapter.CategoryRecyclerAdapter
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.amirhosseinemadi.appstore.view.callback.CategoryCallback
import com.amirhosseinemadi.appstore.viewmodel.CategoryVm
import com.google.android.material.snackbar.BaseTransientBottomBar

class CategoryFragment() : Fragment(),CategoryCallback {

    private val viewModel:CategoryVm
    private lateinit var categoryBinding:FragmentCategoryBinding
    private lateinit var dialog:Dialog
    private var category:String? = null
    private var more:Boolean = true
    private var appList:MutableList<AppModel>? = null

    init
    {
        viewModel = CategoryVm(this)
    }

    constructor(category:String) : this()
    {
        this.category = category
        appList = ArrayList()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        categoryBinding = DataBindingUtil.inflate<FragmentCategoryBinding>(inflater,R.layout.fragment_category,container,false).also{ it.viewModel = viewModel }
        categoryBinding.lifecycleOwner = this
        dialog = Utilities.loadingDialog(requireContext())
        initView()
        handleError()

        if (category == null)
        {
            categoryInit()
        }else
        {
            appByCategory(appList!!.size,category!!)
            categoryBinding.imgBack.visibility = View.VISIBLE
            categoryBinding.imgBack.setOnClickListener(this::backPressed)
            (categoryBinding.recycler.layoutParams as ConstraintLayout.LayoutParams).topToBottom = R.id.img_back
        }

        return categoryBinding.root
    }


    private fun initView()
    {
        categoryBinding.recycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

        if (category != null)
        {
            categoryBinding.recycler.adapter = AppRecyclerAdapter(requireContext(), appList as List<AppModel>, object : Callback
            {
                override fun notify(vararg obj: Any?)
                {
                    requireActivity().supportFragmentManager.beginTransaction().add(R.id.frame,AppFragment(obj[0] as String),"appFragment").addToBackStack("appFragment").commit()
                }
            })

            categoryBinding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener()
            {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (appList!!.size % 10 == 0 && more && (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() >= appList!!.size-2 && !dialog.isShowing)
                    {
                        viewModel.appByCategory(appList!!.size, category!!)
                    }
                }
            })
        }

        Utilities.onBackPressed(categoryBinding.root,object : Callback
        {
            override fun notify(vararg obj: Any?)
            {
                backPressed(null)
            }
        })
    }


    private fun handleError()
    {
        viewModel.error.observe(viewLifecycleOwner,
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
                            "category" -> { viewModel.category() }

                            else -> { viewModel.appByCategory(appList!!.size,it) }
                        }
                    }

                }),"error").commit()
            })
    }


    private fun categoryInit()
    {
        viewModel.category()
        viewModel.categoryResponse
            .observe(viewLifecycleOwner,
                {
                    if (it.responseCode == 1)
                    {
                        categoryBinding.recycler.adapter = CategoryRecyclerAdapter(requireContext(), it.data!!, object : Callback
                        {
                            override fun notify(vararg obj: Any?)
                            {
                                requireActivity().supportFragmentManager.beginTransaction().add(R.id.frame,CategoryFragment(obj[0] as String),"appCategoryFragment").addToBackStack("appCategoryFragment").commit()
                            }
                        })
                    }else
                    {
                        Utilities.showSnack(requireActivity().findViewById(R.id.coordinator),it.message!!,BaseTransientBottomBar.LENGTH_SHORT)
                    }
                })
    }


    private fun appByCategory(offset:Int, category:String)
    {
        viewModel.appByCategory(offset,category)
        viewModel.appResponse
            .observe(viewLifecycleOwner,
                {
                    if (it.responseCode == 1)
                    {
                        if(it.data != null)
                        {
                            more = true
                            appList?.addAll(it.data!!)
                            categoryBinding.recycler.adapter?.notifyItemRangeInserted(appList!!.size - it.data!!.size, it.data!!.size)
                        }else
                        {
                            more = false
                            if (appList?.size == 0)
                            {
                                categoryBinding.img.visibility = View.VISIBLE
                                categoryBinding.txt.visibility = View.VISIBLE
                            }
                        }
                    }else
                    {
                        Utilities.showSnack(requireActivity().findViewById(R.id.coordinator),it.message!!,BaseTransientBottomBar.LENGTH_SHORT)
                    }
                })
    }


    private fun backPressed(view:View?) {
        if (parentFragmentManager.backStackEntryCount > 0)
        {
            val backStack: FragmentManager.BackStackEntry = parentFragmentManager.getBackStackEntryAt(parentFragmentManager.backStackEntryCount - 1)

            when (backStack.name)
            {
                "appCategoryFragment" ->
                {
                    parentFragmentManager.popBackStack(parentFragmentManager.getBackStackEntryAt(parentFragmentManager.backStackEntryCount - 1).name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }

                "categoryInit" ->
                {
                    requireActivity().finish()
                }

                else ->
                {
                    requireActivity().finish()
                }
            }
        } else
        {
            requireActivity().finish()
        }
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