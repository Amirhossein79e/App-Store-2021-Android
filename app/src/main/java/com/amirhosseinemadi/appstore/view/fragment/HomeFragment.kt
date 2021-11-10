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
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentHomeBinding
import com.amirhosseinemadi.appstore.model.entity.HomeCategoryModel
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.adapter.MainPagerAdapter
import com.amirhosseinemadi.appstore.view.adapter.MainRecyclerAdapter
import com.amirhosseinemadi.appstore.view.adapter.SubRecyclerAdapter
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.amirhosseinemadi.appstore.view.callback.HomeCallback
import com.amirhosseinemadi.appstore.viewmodel.HomeVm
import com.google.android.material.snackbar.BaseTransientBottomBar
import okhttp3.internal.Util

class HomeFragment : Fragment(),HomeCallback {

    private val viewModel:HomeVm
    private lateinit var homeBinding:FragmentHomeBinding
    private lateinit var loading:Dialog
    private lateinit var snapHelper:PagerSnapHelper
    private lateinit var recyclerList: HashMap<String,RecyclerView>
    private var i:Int

    init
    {
        viewModel = HomeVm(this)
        i = 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        homeBinding = DataBindingUtil.inflate<FragmentHomeBinding>(inflater,R.layout.fragment_home,container,false).also { it.viewModel = viewModel }
        homeBinding.root.layoutParams = ConstraintLayout.LayoutParams(container?.layoutParams)
        homeBinding.lifecycleOwner = this
        loading = Utilities.loadingDialog(requireContext())
        initView()
        handleError()
        homeInit()

        return homeBinding.root
    }


    private fun initView()
    {
        LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false).also { homeBinding.pager.layoutManager = it }
        snapHelper = PagerSnapHelper().also { it.attachToRecyclerView(homeBinding.pager) }
        LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false).also { homeBinding.recycler.layoutManager = it }

        Utilities.onBackPressed(homeBinding.root,object : Callback
        {
            override fun notify(vararg obj: Any?)
            {
                backPressed()
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
                            "home" -> { viewModel.home() }

                            else ->
                            { if (recyclerList.get(it) != null)
                                {
                                    app(it,recyclerList.get(it)!!,recyclerList.get(it)!!.childCount-1)
                                }
                            }
                        }
                    }

                }),"error").commit()
            })
    }


    private fun homeInit()
    {
        recyclerList = HashMap()
        viewModel.home()
        viewModel.homeResponse
            .observe(viewLifecycleOwner,
                {
                    if(it.responseCode == 1)
                    {
                        homeBinding.pager.adapter = MainPagerAdapter(requireContext(), it.data?.slider as List<String>, object : Callback
                        {
                            override fun notify(vararg obj: Any?)
                            {
                                requireActivity().supportFragmentManager.beginTransaction().add(R.id.frame,AppFragment(obj[0] as String),"appFragment").addToBackStack("appFragment").commit()
                            }
                        })
                        homeBinding.recycler.adapter = MainRecyclerAdapter(requireContext(),it.data?.rows as List<HomeCategoryModel>, object : Callback
                        {
                            override fun notify(vararg obj: Any?)
                            {
                                recyclerList.set(obj[0] as String,obj[1] as RecyclerView)
                                app(obj[0] as String,recyclerList.get(obj[0])!!,obj[2] as Int)
                            }
                        })
                    }else
                    {
                        Utilities.showSnack(requireActivity().findViewById(R.id.coordinator),it.message!!,BaseTransientBottomBar.LENGTH_SHORT)
                    }
                })
    }


    private fun app(category:String, recycler:RecyclerView, position:Int)
    {
        viewModel.app(category,object : Callback
        {
            override fun notify(vararg obj: Any?)
            {
                if (viewModel.appResponse.value?.responseCode == 1)
                {
                    if (position %2 == 0)
                    {
                        val layoutManager:LinearLayoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.HORIZONTAL, true)
                        recycler.layoutManager = layoutManager
                    }else
                    {
                        val layoutManager:LinearLayoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.HORIZONTAL, false)
                        recycler.layoutManager = layoutManager
                    }

                        recycler.adapter = SubRecyclerAdapter(requireContext(), viewModel.appResponse.value?.data!!, object : Callback
                            {
                                override fun notify(vararg obj: Any?)
                                {
                                    requireActivity().supportFragmentManager.beginTransaction().add(R.id.frame,AppFragment(obj[0] as String),"appFragment").addToBackStack("appFragment").commit()
                                }
                            })
                    i++
                    if (i == viewModel.homeResponse.value?.data?.rows?.size)
                    {
                        onHide()
                    }
                }else
                {
                    Utilities.showSnack(requireActivity().findViewById(R.id.coordinator),viewModel.appResponse.value?.message!!,BaseTransientBottomBar.LENGTH_SHORT)
                }

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

}