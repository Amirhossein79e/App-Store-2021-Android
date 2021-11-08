package com.amirhosseinemadi.appstore.view.fragment

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentAppBinding
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.model.entity.AppModel
import com.amirhosseinemadi.appstore.model.entity.ResponseObject
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.adapter.AppImageAdapter
import com.amirhosseinemadi.appstore.view.bottomsheet.DetailFragment
import com.amirhosseinemadi.appstore.view.callback.AppCallback
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.amirhosseinemadi.appstore.viewmodel.AppVm
import com.google.android.material.chip.Chip
import com.squareup.picasso.Picasso
import kotlin.random.Random

class AppFragment(private val packageName:String) : Fragment(),AppCallback {

    private val viewModel:AppVm
    private lateinit var appBinding:FragmentAppBinding
    private lateinit var loading:Dialog
    private lateinit var metrics:DisplayMetrics

    init
    {
        viewModel = AppVm(this)
        metrics = DisplayMetrics()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        appBinding = DataBindingUtil.inflate<FragmentAppBinding>(inflater,R.layout.fragment_app,container,false).also { it.viewModel = viewModel }
        appBinding.lifecycleOwner = this
        loading = Utilities.loadingDialog(requireContext())
        initView()
        handleError()
        app()

        return appBinding.root
    }


    private fun initView()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            requireActivity().display!!.getRealMetrics(metrics)
        }else
        {
            metrics = requireContext().resources.displayMetrics
        }

        Picasso.get().load(ApiCaller.ICON_URL+packageName+".png").into(appBinding.img)

        appBinding.recycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        when(Random.nextInt(1,5))
        {
            1 -> { appBinding.imgTop.setImageResource(R.drawable.background_top_one) }

            2 -> { appBinding.imgTop.setImageResource(R.drawable.background_top_two) }

            3 -> { appBinding.imgTop.setImageResource(R.drawable.background_top_three) }

            4 -> { appBinding.imgTop.setImageResource(R.drawable.background_top_four) }
        }

        appBinding.imgBack.setOnClickListener {
            backPressed()
            Toast.makeText(requireContext(),"BACK",Toast.LENGTH_SHORT).show()
        }

        Utilities.onBackPressed(appBinding.root, object : Callback
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
                requireActivity().supportFragmentManager.beginTransaction().add(R.id.frame,ErrorFragment(object :
                    Callback
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
                            "app" -> {viewModel.app(packageName)}
                        }
                    }

                }),"error").commit()
            })
    }


    private fun app()
    {
        viewModel.app(packageName)
        viewModel.appResponse
            .observe(viewLifecycleOwner,
                { t ->
                    if (t?.responseCode == 1 && t.data != null)
                    {
                        appBinding.recycler.adapter = AppImageAdapter(requireContext(),packageName,t.data!!.imageNum!!)

                        if (PrefManager.getLang().equals("en"))
                        {
                            appBinding.txtName.text = t.data!!.nameEn
                            appBinding.txtDev.text = "by "+t.data!!.devEn
                            appBinding.txtCategory.text = t.data!!.categoryEn
                        }else
                        {
                            appBinding.txtName.text = t.data!!.nameFa
                            appBinding.txtDev.text = "توسط "+t.data!!.devFa
                            appBinding.txtCategory.text = t.data!!.categoryFa
                        }

                        Picasso.get().load(ApiCaller.CATEGORY_URL+t.data!!.categoryIcon).into(appBinding.imgCategory)
                        appBinding.txtRate.text = String.format("%.1f",t.data!!.rate)
                        appBinding.txtSize.text = t.data!!.size
                        appBinding.txtVer.text = t.data!!.verName

                        if (t.data!!.detail!!.length < 120)
                        {
                            appBinding.txtDetail.text = t.data!!.detail
                        }else
                        {
                            appBinding.txtDetail.text = t.data!!.detail!!.substring(0,119)+"..."
                        }

                        appBinding.txtMore.setOnClickListener {
                            DetailFragment(t.data!!.detail!!).show(parentFragmentManager,null)
                        }

                        for (str:String in t.data!!.tag!!.split(","))
                        {
                            val chip:Chip = Chip(requireContext())
                            chip.setChipBackgroundColorResource(R.color.colorOnPrimary)
                            chip.setRippleColorResource(R.color.rippleColor)
                            chip.setTextColor(ContextCompat.getColor(requireContext(),R.color.md_white_1000))
                            chip.text = str
                            chip.setOnClickListener { requireActivity().supportFragmentManager.beginTransaction().add(R.id.frame,SearchFragment(str),"searchFragment").addToBackStack("searchFragment").commit() }

                            appBinding.linearChip.addView(chip)

                            (chip.layoutParams as LinearLayout.LayoutParams).marginEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8f,metrics).toInt()
                        }
                    }else
                    {

                    }
                })
    }


    private fun backPressed()
    {
        if (parentFragmentManager.backStackEntryCount > 0)
        {
            val backStack: FragmentManager.BackStackEntry = parentFragmentManager.getBackStackEntryAt(parentFragmentManager.backStackEntryCount - 1)
            val fragment: Fragment? = parentFragmentManager.findFragmentByTag(backStack.name)

            when (backStack.name)
            {
                "homeInit" -> { parentFragmentManager.beginTransaction().remove(this@AppFragment).commit() }

                "searchInit" -> { parentFragmentManager.beginTransaction().remove(this@AppFragment).commit() }

                "categoryInit" -> { parentFragmentManager.beginTransaction().remove(this@AppFragment).commit() }

                "accountInit" -> { parentFragmentManager.beginTransaction().remove(this@AppFragment).commit() }

                "searchFragment" -> { parentFragmentManager.beginTransaction().remove(this@AppFragment).commit() }

                else -> { requireActivity().finish() }
            }
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