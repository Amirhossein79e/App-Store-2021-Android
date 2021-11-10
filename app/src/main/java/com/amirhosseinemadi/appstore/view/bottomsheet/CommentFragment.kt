package com.amirhosseinemadi.appstore.view.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.databinding.DataBindingUtil
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
import com.amirhosseinemadi.appstore.viewmodel.AppVm
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.BaseTransientBottomBar

class CommentFragment(private val packageName:String) : BottomSheetDialogFragment(),AppCallback {

    private val viewModel:AppVm
    private lateinit var fragmentCommentBinding:FragmentCommentBinding
    private lateinit var loading:Dialog
    private val list:MutableList<CommentModel>
    private var more:Boolean = true

    private var commentModel:CommentModel?
    private var callback:Callback?

    init
    {
        viewModel = AppVm(this)
        list = ArrayList()
        commentModel = null
        callback = null
    }

    constructor(commentModel:CommentModel?, packageName:String, callback:Callback) : this(packageName)
    {
         this.commentModel = commentModel
        this.callback = callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentCommentBinding = DataBindingUtil.inflate<FragmentCommentBinding>(inflater,R.layout.fragment_comment,container,false).also { it.viewModel = viewModel }
        fragmentCommentBinding.lifecycleOwner = this
        loading = Utilities.loadingDialog(requireContext())
        initView()
        commentInit(list.size,PrefManager.getAccess(),packageName)

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
                Utilities.dialogIcon(requireContext(),null,R.string.sure_delete,R.string.yes,R.string.no,true,true,
                    {
                        deleteComment(PrefManager.getAccess()!!,packageName)
                    },null).show()
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

            fragmentCommentBinding.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                if (rating < 1f)
                {
                    ratingBar?.rating = 1f
                }
            }

            fragmentCommentBinding.btn.setOnClickListener { submitComment(PrefManager.getAccess()!!,viewModel.comment.value!!,viewModel.rate.value!!,packageName) }
        }
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
                    commentInit(0,access,packageName)
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
                if (it.responseCode == 1)
                {
                    dismiss()
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