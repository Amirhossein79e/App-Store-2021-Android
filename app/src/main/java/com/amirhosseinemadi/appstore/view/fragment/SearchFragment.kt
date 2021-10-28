package com.amirhosseinemadi.appstore.view.fragment

import android.app.ActionBar
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentSearchBinding
import com.amirhosseinemadi.appstore.model.entity.AppModel
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.adapter.AppRecyclerAdapter
import com.amirhosseinemadi.appstore.view.adapter.TitleRecyclerAdapter
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.amirhosseinemadi.appstore.view.callback.SearchCallback
import com.amirhosseinemadi.appstore.viewmodel.SearchVm
import com.google.android.material.snackbar.BaseTransientBottomBar
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment(),SearchCallback {

    private lateinit var viewModel:SearchVm
    private lateinit var searchBinding:FragmentSearchBinding
    private lateinit var loading:Dialog
    private var more:Boolean
    private var fromTitle:Boolean
    private var isNew:Boolean
    private val appList:MutableList<AppModel>
    private val titleList:MutableList<String>
    private lateinit var activityResultLauncher:ActivityResultLauncher<Intent>

    init
    {
        viewModel = SearchVm(this)
        more = true
        fromTitle = false
        isNew = true
        appList = ArrayList()
        titleList = ArrayList()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == AppCompatActivity.RESULT_OK)
            {
                fromTitle = false
                searchBinding.sch.setQuery(it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).toString().replace("[","").replace("]",""),true)
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        searchBinding = DataBindingUtil.inflate<FragmentSearchBinding>(inflater,R.layout.fragment_search,container,false).also { it.viewModel = viewModel }
        loading = Utilities.loadingDialog(requireContext())
        searchBinding.lifecycleOwner = this
        handleError()
        initView()

        return searchBinding.root
    }


    private fun initView()
    {
        searchBinding.btnVoice.setOnClickListener(this::voiceCLick)

        searchBinding.sch.setOnQueryTextListener(object : SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (searchBinding.sch.hasFocus())
                {
                    searchBinding.sch.clearFocus()
                }
                isNew = true
                titleList.clear()
                searchBinding.recyclerTitle.adapter?.notifyDataSetChanged()
                app(0,query!!)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText != null && newText.length > 1 && !fromTitle)
                {
                    title(newText)
                }else
                {
                    fromTitle = false
                    titleList.clear()
                    searchBinding.recyclerTitle.adapter?.notifyDataSetChanged()
                }

                searchBinding.img.visibility = View.GONE
                searchBinding.txt.visibility = View.GONE

                return false
            }
        })

        searchBinding.recycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        searchBinding.recycler.adapter = AppRecyclerAdapter(requireContext(),appList,object : Callback
        {
            override fun notify(vararg obj: Any?)
            {

            }
        })

        searchBinding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (appList.size % 10 == 0 && more && (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() >= appList.size-2 && !loading.isShowing)
                {
                    app(appList.size,searchBinding.sch.query.toString())
                }
            }
        })

        searchBinding.recyclerTitle.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        searchBinding.recyclerTitle.adapter = TitleRecyclerAdapter(requireContext(),titleList,object : Callback
        {
            override fun notify(vararg obj: Any?)
            {
                fromTitle = true
                searchBinding.sch.setQuery(obj[0] as String,true)
            }
        })
    }


    private fun handleError()
    {
        viewModel.error.observe(viewLifecycleOwner,
            {
                if (it.equals("app"))
                {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .add(R.id.frame, ErrorFragment(object : Callback {
                            override fun notify(vararg obj: Any?)
                            {
                                val fragment: Fragment? =
                                    requireActivity().supportFragmentManager.findFragmentByTag("error")

                                if (fragment != null)
                                {
                                    requireActivity().supportFragmentManager.beginTransaction()
                                        .remove(fragment).commit()
                                }
                                app(appList.size,searchBinding.sch.query.toString())
                            }
                        }), "error").commit()
                }else
                {
                    Utilities.showSnack(requireActivity().findViewById(R.id.coordinator),requireContext().getString(R.string.request_failed),
                        BaseTransientBottomBar.LENGTH_SHORT)
                }
            })
    }


    private fun voiceCLick(view: View)
    {
        val intent:Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,requireContext().getString(R.string.search))
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale("fa"))
        activityResultLauncher.launch(intent)
    }


    private fun title(title:String)
    {
        if (!viewModel.titleResponse.hasObservers())
        {
            viewModel.getTitleResponse(title)
                .observe(viewLifecycleOwner,
                    {
                        if (it.responseCode == 1)
                        {
                            titleList.clear()
                            if (it.data != null) {
                                titleList.addAll(it.data!!)
                            }
                            searchBinding.recyclerTitle.adapter?.notifyDataSetChanged()
                        }else
                        {
                            Utilities.showSnack(requireActivity().findViewById(R.id.coordinator),it.message!!,BaseTransientBottomBar.LENGTH_SHORT)
                        }
                    })
        }else
        {
            viewModel.title(title)
        }
    }


    private fun app(offset:Int, title:String)
    {
        if (!viewModel.appResponse.hasObservers())
        {
            viewModel.getAppResponse(offset,title)
                .observe(viewLifecycleOwner,
                    {
                        if (it.responseCode == 1)
                        {
                            if (it.data != null) {
                                more = true
                                if (isNew) {
                                    appList.clear()
                                }
                                appList.addAll(it.data!!)
                                searchBinding.img.visibility = View.GONE
                                searchBinding.txt.visibility = View.GONE
                                if (isNew) {
                                    searchBinding.recycler.adapter?.notifyDataSetChanged()
                                    isNew = false
                                } else {
                                    searchBinding.recycler.adapter?.notifyItemRangeInserted(
                                        appList.size - it.data!!.size,
                                        it.data!!.size
                                    )
                                }
                            } else {
                                more = false
                                if (isNew || appList.size == 0) {
                                    appList.clear()
                                    searchBinding.recycler.adapter?.notifyDataSetChanged()
                                    searchBinding.img.visibility = View.VISIBLE
                                    searchBinding.txt.visibility = View.VISIBLE
                                }
                            }
                        }else
                        {
                            Utilities.showSnack(requireActivity().findViewById(R.id.coordinator),it.message!!,BaseTransientBottomBar.LENGTH_SHORT)
                        }
                    })
        }else
        {
            viewModel.app(offset,title)
        }
    }


    override fun onShow() {
        if (!loading.isShowing && !isNew)
        {
            loading.show()
        }else if (isNew)
        {
            searchBinding.btnVoice.visibility = View.GONE
            searchBinding.loading.visibility = View.VISIBLE
            searchBinding.loading.playAnimation()
        }
    }

    override fun onHide() {
        if (loading.isShowing && !isNew)
        {
            loading.dismiss()
        }else if (searchBinding.loading.isAnimating)
        {
            searchBinding.loading.pauseAnimation()
            searchBinding.loading.visibility = View.GONE
            searchBinding.btnVoice.visibility = View.VISIBLE
        }
    }

}