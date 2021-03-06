package com.amirhosseinemadi.appstore.view.fragment

import android.app.Dialog
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.databinding.FragmentAppBinding
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.model.entity.AppModel
import com.amirhosseinemadi.appstore.model.entity.CommentModel
import com.amirhosseinemadi.appstore.model.entity.DownloadModel
import com.amirhosseinemadi.appstore.util.DownloadManager
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.util.Utilities
import com.amirhosseinemadi.appstore.view.adapter.AppImageAdapter
import com.amirhosseinemadi.appstore.view.bottomsheet.CommentFragment
import com.amirhosseinemadi.appstore.view.bottomsheet.DetailFragment
import com.amirhosseinemadi.appstore.view.bottomsheet.LoginFragment
import com.amirhosseinemadi.appstore.view.callback.AppCallback
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.amirhosseinemadi.appstore.viewmodel.AppVm
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.squareup.picasso.Picasso
import java.io.File
import java.lang.IllegalArgumentException
import java.security.Permission
import java.util.jar.Manifest
import kotlin.random.Random

class AppFragment(private val packageName:String) : Fragment(),AppCallback {

    private var viewModel:AppVm
    private lateinit var appBinding:FragmentAppBinding
    private lateinit var loading:Dialog
    private var metrics:DisplayMetrics
    private val commentList:MutableList<CommentModel>
    private lateinit var deleteDialog:Dialog
    private lateinit var requestPermission:ActivityResultLauncher<String>
    private var appModel:AppModel?

    companion object
    {
        var isRunning:Boolean? = null
        private var callback:Callback? = null

        fun notifyPackage()
        {
            callback?.notify()
        }
    }

    init
    {
        viewModel = AppVm(this)
        appModel = null
        isRunning = true

        metrics = DisplayMetrics()
        commentList = ArrayList()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()
        ) {
            if (it)
            {
                val downloadModel:DownloadModel = DownloadModel().also {
                    it.packageName = appModel?.packageName
                    it.appName = appModel?.nameEn
                    it.isCancel = false
                    it.progress = -1
                }

                val intent = Intent(requireContext(),DownloadManager::class.java)
                intent.putExtra("task","start")
                intent.putExtra("download",downloadModel)
                requireActivity().startService(intent)

                appBinding.linearBtn.visibility = View.GONE
                appBinding.txtDownloadStatus.visibility = View.VISIBLE
                appBinding.progress.visibility = View.VISIBLE
                appBinding.btnCancel.visibility = View.VISIBLE

                LocalBroadcastManager.getInstance(requireContext()).registerReceiver(QueueBroadCast(),IntentFilter("QUEUE_RESULT"))
            }else
            {
                Utilities.showSnack(appBinding.coordinator,"For download we need access to storage permission",BaseTransientBottomBar.LENGTH_SHORT)
            }
        }
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


    override fun onDestroyView() {
        super.onDestroyView()

        isRunning = false
        isRunning = null
        callback = null

        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(QueueBroadCast())
    }


    private fun initView()
    {
        callback = object : Callback
        {
            override fun notify(vararg obj: Any?)
            {
                initInstallBtn()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            requireActivity().display!!.getRealMetrics(metrics)
        }else
        {
            metrics = requireContext().resources.displayMetrics
        }

        appBinding.recycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        when(Random.nextInt(1,5))
        {
            1 -> { appBinding.imgTop.setImageResource(R.drawable.background_top_one) }

            2 -> { appBinding.imgTop.setImageResource(R.drawable.background_top_two) }

            3 -> { appBinding.imgTop.setImageResource(R.drawable.background_top_three) }

            4 -> { appBinding.imgTop.setImageResource(R.drawable.background_top_four) }
        }

        Picasso.get().load(ApiCaller.ICON_URL+packageName+".png").into(appBinding.img)

        handleProgress()
        if(DownloadManager.downloadQueue != null && DownloadManager.downloadQueue!!.size > 0)
        {
            for (downloadModel:DownloadModel in DownloadManager.downloadQueue!!)
            {
                if (downloadModel.packageName.equals(packageName))
                {
                    appBinding.linearBtn.visibility = View.GONE
                    appBinding.txtDownloadStatus.visibility = View.VISIBLE
                    appBinding.progress.visibility = View.VISIBLE
                    appBinding.btnCancel.visibility = View.VISIBLE
                    break
                }
            }
        }

        appBinding.btnSubmitComment.setOnClickListener(this::commentClick)
        appBinding.imgBack.setOnClickListener(this::backPressed)
        appBinding.btnCancel.setOnClickListener(this::cancelClick)

        Utilities.onBackPressed(appBinding.root, object : Callback
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

                            "rating" -> {viewModel.getRating(packageName)}

                            "comment" -> {viewModel.comment(0,PrefManager.getAccess(),packageName)}
                        }
                    }

                }),"error").commit()
            })
    }


    private fun initInstallBtn()
    {
        if (!Utilities.checkPackageInstalled(packageName))
        {
            appBinding.btnInstall.text = requireContext().getString(R.string.install)
            (appBinding.btnInstall.layoutParams as LinearLayout.LayoutParams).marginEnd = Utilities.dpToPx(requireActivity(),0f)
            appBinding.btnUninstall.visibility = View.GONE

        }else if (Utilities.checkPackageInstalled(packageName) && requireContext().packageManager.getPackageInfo(packageName,0).versionCode < appModel?.verCode!!)
        {
            val marginInPx = Utilities.dpToPx(requireContext(),4f)
            appBinding.btnInstall.text = requireContext().getString(R.string.update)
            appBinding.btnUninstall.visibility = View.VISIBLE
            (appBinding.btnUninstall.layoutParams as LinearLayout.LayoutParams).setMargins(marginInPx,0,0,marginInPx)
            (appBinding.btnInstall.layoutParams as LinearLayout.LayoutParams).setMargins(0,0,marginInPx,marginInPx)
        }else
        {
            val marginInPx = Utilities.dpToPx(requireContext(),4f)
            appBinding.btnInstall.text = requireContext().getString(R.string.open)
            appBinding.btnUninstall.visibility = View.VISIBLE
            (appBinding.btnUninstall.layoutParams as LinearLayout.LayoutParams).setMargins(marginInPx,0,0,marginInPx)
            (appBinding.btnInstall.layoutParams as LinearLayout.LayoutParams).setMargins(0,0,marginInPx,marginInPx)
        }
        appBinding.btnInstall.setOnClickListener(this::installClick)
        appBinding.btnUninstall.setOnClickListener(this::unInstallClick)
    }


    private fun installClick(view:View)
    {
        if ((!Utilities.checkPackageInstalled(packageName)) ||
            (Utilities.checkPackageInstalled(packageName) && requireContext().packageManager.getPackageInfo(packageName,0).versionCode < appModel?.verCode!!))
        {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            {
                if (requireContext().checkSelfPermission(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    val downloadModel:DownloadModel = DownloadModel().also {
                        it.packageName = appModel?.packageName
                        it.appName = appModel?.nameEn
                        it.isCancel = false
                        it.progress = -1
                    }

                    val intent = Intent(requireContext(),DownloadManager::class.java)
                    intent.putExtra("task","start")
                    intent.putExtra("download",downloadModel)
                    requireActivity().startService(intent)

                    appBinding.linearBtn.visibility = View.GONE
                    appBinding.txtDownloadStatus.visibility = View.VISIBLE
                    appBinding.progress.visibility = View.VISIBLE
                    appBinding.btnCancel.visibility = View.VISIBLE

                    LocalBroadcastManager.getInstance(requireContext()).registerReceiver(QueueBroadCast(),IntentFilter("QUEUE_RESULT"))
                }else
                {
                    requestPermission.launch(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                }

            }else
            {
                val downloadModel:DownloadModel = DownloadModel().also {
                    it.packageName = appModel?.packageName
                    it.appName = appModel?.nameEn
                    it.isCancel = false
                    it.progress = -1
                }

                val intent = Intent(requireContext(),DownloadManager::class.java)
                intent.putExtra("task","start")
                intent.putExtra("download",downloadModel)
                requireActivity().startService(intent)

                appBinding.linearBtn.visibility = View.GONE
                appBinding.txtDownloadStatus.visibility = View.VISIBLE
                appBinding.progress.visibility = View.VISIBLE
                appBinding.btnCancel.visibility = View.VISIBLE

                LocalBroadcastManager.getInstance(requireContext()).registerReceiver(QueueBroadCast(),IntentFilter("QUEUE_RESULT"))
            }

        }else
        {
            startActivity(requireContext().packageManager.getLaunchIntentForPackage(packageName))
        }
    }


    private fun unInstallClick(view:View)
    {
        if (Utilities.checkPackageInstalled(packageName))
        {
            val intent:Intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE,Uri.parse("package:$packageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }


    private fun cancelClick(view: View)
    {
        appBinding.progress.isIndeterminate = true
        appBinding.txtDownloadStatus.text = getString(R.string.in_queue)

        val downloadModel:DownloadModel = DownloadModel().also {
            it.packageName = appModel?.packageName
            it.appName = appModel?.nameEn
            it.isCancel = true
        }

        val intent = Intent(requireContext(),DownloadManager::class.java)
        intent.putExtra("task","stop")
        intent.putExtra("download",downloadModel)
        requireActivity().startService(intent)
    }


    private fun commentClick(view:View)
    {
        if (PrefManager.checkSignIn())
        {
            openCommentSubmit()
        }else
        {
            LoginFragment(object : Callback
            {
                override fun notify(vararg obj: Any?)
                {
                    openCommentSubmit()
                }
            }).show(parentFragmentManager,null)
        }
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
                    deleteDialog = Utilities.dialogIcon(requireContext(),null,R.string.sure_delete,R.string.yes,R.string.no,true,true,
                        {
                            deleteComment(PrefManager.getAccess()!!,packageName)
                            deleteDialog.dismiss()
                        },null)
                    deleteDialog.show()
                }
            }
        }

        appBinding.linearComment.addView(view)
    }


    private fun handleProgress()
    {
        if (DownloadManager.downloadProgress != null && !DownloadManager.downloadProgress!!.hasActiveObservers())
        {
            DownloadManager.downloadProgress!!.observe(viewLifecycleOwner,
                {
                    if (it.packageName.equals(packageName))
                    {
                        when (it.progress)
                        {
                            -1 ->
                            {
                                appBinding.progress.isIndeterminate = true
                                appBinding.txtDownloadStatus.text = getString(R.string.in_queue)
                            }

                            1000 ->
                            {
                                appBinding.progress.visibility = View.GONE
                                appBinding.btnCancel.visibility = View.GONE
                                appBinding.txtDownloadStatus.visibility = View.GONE
                                appBinding.linearBtn.visibility = View.VISIBLE
                            }

                            1001 ->
                            {
                                appBinding.progress.visibility = View.GONE
                                appBinding.btnCancel.visibility = View.GONE
                                appBinding.txtDownloadStatus.visibility = View.GONE
                                appBinding.linearBtn.visibility = View.VISIBLE
                            }

                            1002 ->
                            {
                                appBinding.progress.visibility = View.GONE
                                appBinding.btnCancel.visibility = View.GONE
                                appBinding.txtDownloadStatus.visibility = View.GONE
                                appBinding.linearBtn.visibility = View.VISIBLE
                            }

                            else ->
                            {
                                appBinding.progress.isIndeterminate = false
                                appBinding.progress.progress = it.progress
                                appBinding.txtDownloadStatus.text = getString(R.string.downloading) + " - ${it.progress}%"
                            }
                        }
                    } else
                    {
                        for (downloadModel:DownloadModel in DownloadManager.downloadQueue!!)
                        {
                            if (downloadModel.packageName.equals(packageName))
                            {
                                if (downloadModel.progress == -1)
                                {
                                    appBinding.progress.isIndeterminate = true
                                    appBinding.txtDownloadStatus.text = getString(R.string.in_queue)
                                }
                                break
                            }
                        }
                    }
                })
        }
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent("QUEUE_HANDLE"))
    }


    private fun openCommentSubmit()
    {
        if (commentList.size > 0 && commentList.get(0).isAccess == 1)
        {
            CommentFragment(commentList.get(0), packageName, object : Callback
            {
                override fun notify(vararg obj: Any?)
                {
                    commentInit(0,PrefManager.getAccess(),packageName)
                }
            }).show(parentFragmentManager, null)
        }else
        {
            CommentFragment(null,packageName,object : Callback
            {
                override fun notify(vararg obj: Any?)
                {
                    commentInit(0,PrefManager.getAccess(),packageName)
                }
            }).show(parentFragmentManager,null)
        }
    }


    private fun appInit()
    {
        viewModel.app(packageName)
        viewModel.appResponse
            .observe(viewLifecycleOwner,
                { t ->
                    if (t?.responseCode == 1 && t.data != null)
                    {
                        appModel = t.data
                        initInstallBtn()
                        handleProgress()

                        appBinding.recycler.adapter = AppImageAdapter(requireContext(),packageName,t.data!!.imageNum!!)

                        if (PrefManager.getLang().equals("en"))
                        {
                            appBinding.txtName.text = t.data!!.nameEn
                            appBinding.txtDev.text = "by "+t.data!!.devEn
                            appBinding.txtCategory.text = t.data!!.categoryEn
                        }else
                        {
                            appBinding.txtName.text = t.data!!.nameFa
                            appBinding.txtDev.text = "???????? "+t.data!!.devFa
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

                            (chip.layoutParams as LinearLayout.LayoutParams).marginEnd = Utilities.dpToPx(requireActivity(),8f)
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
                    viewModel.getRating(packageName)
                    commentList.clear()
                    if(it.data != null)
                    {
                        commentList.addAll(it.data!!)
                    }
                    appBinding.linearComment.removeAllViews()

                    if (it.data != null && it.data!!.size > 0)
                    {
                        if (it.data!!.size >= 3)
                        {
                            appBinding.txtMoreComment.setOnClickListener() { CommentFragment(packageName, object : Callback
                            {
                                override fun notify(vararg obj: Any?)
                                {
                                    if(obj.size > 0 && obj[0] != null && (obj[0] as String).equals("delete"))
                                    {
                                        appBinding.linearComment.removeViewAt(0)
                                        viewModel.getRating(packageName)
                                    }
                                }
                            }).show(parentFragmentManager,null) }

                            appBinding.txtMoreComment.visibility = View.VISIBLE

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


    private fun backPressed(view:View?)
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


    inner class QueueBroadCast : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?)
        {
            handleProgress()
            LocalBroadcastManager.getInstance(this@AppFragment.requireContext()).unregisterReceiver(this)
        }
    }

}