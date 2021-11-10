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
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentAppBinding
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.model.entity.CommentModel
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.adapter.AppImageAdapter
import com.amirhosseinemadi.appstore.view.bottomsheet.CommentFragment
import com.amirhosseinemadi.appstore.view.bottomsheet.DetailFragment
import com.amirhosseinemadi.appstore.view.callback.AppCallback
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.amirhosseinemadi.appstore.viewmodel.AppVm
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.squareup.picasso.Picasso
import kotlin.random.Random

class AppFragment() : Fragment(),AppCallback {

    private var viewModel:AppVm
    private var packageName:String
    private lateinit var appBinding:FragmentAppBinding
    private lateinit var loading:Dialog
    private lateinit var metrics:DisplayMetrics

    init
    {
        viewModel = AppVm(this)
        packageName = ""
    }

    constructor(packageName:String) : this()
    {
        viewModel = AppVm(this)
        metrics = DisplayMetrics()
        this.packageName = packageName
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        appBinding = DataBindingUtil.inflate<FragmentAppBinding>(inflater,R.layout.fragment_app,container,false).also { it.viewModel = viewModel }
        appBinding.lifecycleOwner = this
        loading = Utilities.loadingDialog(requireContext())
        initView()
        handleError()
        appInit()

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


    private fun appInit()
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

                        appBinding.cardCategory.setOnClickListener {
                            requireActivity().supportFragmentManager.beginTransaction().add(R.id.frame,CategoryFragment(t.data!!.category!!),"appCategoryFragment").addToBackStack("appCategoryFragment").commit()
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

                        commentInit(0,PrefManager.getAccess(),packageName)

                    }else
                    {
                        Utilities.showSnack(requireActivity().findViewById(R.id.coordinator),t.message!!, BaseTransientBottomBar.LENGTH_SHORT)
                    }
                })
    }


    private fun commentInit(offset:Int, access:String?, packageName:String)
    {
        viewModel.comment(offset,access,packageName)
        viewModel.commentResponse.observe(viewLifecycleOwner,
            {
                if (it.responseCode == 1)
                {
                    if (it.data != null && it.data!!.size > 0)
                    {
                        appBinding.txtMoreComment.setOnClickListener() { CommentFragment(packageName).show(parentFragmentManager,null) }
                        appBinding.txtMoreComment.visibility = View.VISIBLE

                        if (it.data!!.size >= 3)
                        {
                            for (i: Int in 0 until 3)
                            {
                                inflateComments(it.data!!,i)
                            }
                        }else
                        {
                            for (i: Int in it.data!!.indices)
                            {
                                inflateComments(it.data!!,i)
                            }
                        }
                    }else
                    {
                        appBinding.txtNoCommentApp.visibility = View.VISIBLE
                    }
                }else
                {
                    Utilities.showSnack(requireActivity().findViewById(R.id.coordinator),it.message!!, BaseTransientBottomBar.LENGTH_SHORT)
                }
            })
    }


    private fun deleteComment(access:String, packageName:String)
    {
        viewModel.deleteComment(access, packageName)
        viewModel.deleteResponse.observe(viewLifecycleOwner,
            {
                if (it.responseCode == 1)
                {
                    commentInit(0,access,packageName)
                }else
                {
                    Utilities.showSnack(requireActivity().findViewById(R.id.coordinator),it.message!!, BaseTransientBottomBar.LENGTH_SHORT)
                }
            })
    }


    private fun inflateComments(list:List<CommentModel>, i:Int)
    {
        val view:View = layoutInflater.inflate(R.layout.comment_item,appBinding.linearComment,false)
        view.findViewById<AppCompatTextView>(R.id.txt_name).text = list.get(i).username
        view.findViewById<AppCompatTextView>(R.id.txt_comment).text = list.get(i).detail
        view.findViewById<AppCompatRatingBar>(R.id.rating_bar).rating = list.get(i).rate!!

        if (list.get(i).isAccess == 1)
        {
            view.findViewById<AppCompatTextView>(R.id.txt_delete).let {
                it.visibility = View.VISIBLE
                it.setOnClickListener {
                    Utilities.dialogIcon(requireContext(),null,R.string.sure_delete,R.string.yes,R.string.no,true,true,
                        {
                            deleteComment(PrefManager.getAccess()!!,packageName)
                        },null).show()
                }
            }
        }

        appBinding.linearComment.addView(view)
    }


    private fun backPressed()
    {
        if (parentFragmentManager.backStackEntryCount > 0)
        {
            parentFragmentManager.popBackStack(parentFragmentManager.getBackStackEntryAt(parentFragmentManager.backStackEntryCount-1).name,FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }else
        {
            requireActivity().finish()
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