package com.amirhosseinemadi.appstore.view.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentCommentBinding
import com.amirhosseinemadi.appstore.model.entity.CommentModel
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.adapter.CommentAdapter
import com.amirhosseinemadi.appstore.view.callback.AppCallback
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.amirhosseinemadi.appstore.view.fragment.ErrorFragment
import com.amirhosseinemadi.appstore.viewmodel.AppVm
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.BaseTransientBottomBar

class CommentFragment(private val packageName:String, private val mainCallback: Callback) : BottomSheetDialogFragment(),AppCallback {

    private val viewModel:AppVm
    private lateinit var fragmentCommentBinding:FragmentCommentBinding
    private lateinit var loading:Dialog
    private val list:MutableList<CommentModel>
    private var more:Boolean = true
    private lateinit var deleteDialog:Dialog

    private var commentModel:CommentModel?
    private var callback:Callback?

    init
    {
        viewModel = AppVm(this)
        list = ArrayList()
        commentModel = null
        callback = null
    }

    constructor(commentModel:CommentModel?, packageName:String, callback:Callback) : this(packageName,callback)
    {
         this.commentModel = commentModel
        this.callback = callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentCommentBinding = DataBindingUtil.inflate<FragmentCommentBinding>(inflater,R.layout.fragment_comment,container,false).also { it.viewModel = viewModel }
        fragmentCommentBinding.lifecycleOwner = this
        loading = Utilities.loadingDialog(requireContext())
        initView()
        handleError()
        if (callback == null)
        {
            commentInit(list.size, PrefManager.getAccess(), packageName)
        }

        return fragmentCommentBinding.root
    }


    private fun initView()
    {
        fragmentCommentBinding.recycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

        fragmentCommentBinding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (list.size % 25 == 0 && more && (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() >= list.size-2 && !loading.isShowing)
                {
                    viewModel.comment(list.size,PrefManager.getAccess(),packageName)
                }
            }
        })

        fragmentCommentBinding.recycler.adapter = CommentAdapter(requireContext(),list,object : Callback
        {
            override fun notify(vararg obj: Any?)
            {
                deleteDialog = Utilities.dialogIcon(requireContext(),null,R.string.sure_delete,R.string.yes,R.string.no,true,true,
                    {
                        deleteDialog.dismiss()
                        deleteComment(PrefManager.getAccess()!!,packageName)
                    },null)
                deleteDialog.show()
            }
        })

        if (callback != null)
        {
            fragmentCommentBinding.txtDetail.visibility = View.GONE
            fragmentCommentBinding.recycler.visibility = View.GONE
            fragmentCommentBinding.ratingBar.visibility = View.VISIBLE
            fragmentCommentBinding.txtComment.visibility = View.VISIBLE
            fragmentCommentBinding.btn.visibility = View.VISIBLE

            if (commentModel != null)
            {
                viewModel.comment.value = commentModel!!.detail
                viewModel.rate.value = commentModel!!.rate
            }

            viewModel.rate.observe(viewLifecycleOwner,
                {
                    if (it < 1)
                    {
                        viewModel.rate.value = 1f
                    }
                })

            fragmentCommentBinding.btn.setOnClickListener(this::submitClick)
        }
    }


    private fun submitClick(view: View)
    {
        submitComment(PrefManager.getAccess()!!,viewModel.comment.value!!,viewModel.rate.value!!,packageName)
    }


    private fun handleError()
    {
        viewModel.error.observe(viewLifecycleOwner,
            {
                Utilities.showSnack(requireActivity().findViewById(R.id.coordinator),getString(R.string.request_failed), BaseTransientBottomBar.LENGTH_SHORT)
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
                        if(it.data != null)
                        {
                            more = true
                            list.addAll(it.data!!)
                            fragmentCommentBinding.recycler.adapter?.notifyItemRangeInserted(list.size - it.data!!.size, it.data!!.size)
                        }else
                        {
                            more = false
                        }
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
                    list.removeAt(0)
                    fragmentCommentBinding.recycler.adapter?.notifyItemRemoved(0)
                    mainCallback.notify("delete")
                    if (list.size == 0)
                    {
                        dismiss()
                    }else
                    {
                        //commentInit(0,access,packageName)
                    }
                }else
                {
                    Utilities.showSnack(requireActivity().findViewById(R.id.coordinator),it.message!!, BaseTransientBottomBar.LENGTH_SHORT)
                }
            })
    }


    private fun submitComment(access:String, comment:String, rate:Float, packageName:String)
    {
        viewModel.submitComment(access,comment,rate,packageName)
        viewModel.submitResponse.observe(viewLifecycleOwner,
            {
                if (it.responseCode == 1 || it.responseCode == 2)
                {
                    dismiss()
                    callback?.notify()
                }else
                {
                    Utilities.showSnack(requireActivity().findViewById(R.id.coordinator),it.message!!, BaseTransientBottomBar.LENGTH_SHORT)
                }
            })
    }


    override fun getTheme(): Int {
        return R.style.bottom_sheet_theme
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