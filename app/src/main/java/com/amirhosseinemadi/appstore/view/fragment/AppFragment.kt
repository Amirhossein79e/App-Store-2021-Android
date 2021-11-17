package com.amirhosseinemadi.appstore.view.fragment

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.ContentUris.withAppendedId
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import kotlin.random.Random

class AppFragment() : Fragment(),AppCallback {

    private var viewModel:AppVm
    private var packageName:String
    private lateinit var appBinding:FragmentAppBinding
    private lateinit var loading:Dialog
    private lateinit var metrics:DisplayMetrics
    private lateinit var commentList:MutableList<CommentModel>
    private lateinit var deleteDialog:Dialog
    private var appModel:AppModel?

    companion object
    {
        var isRunning:Boolean? = null
    }

    init
    {
        viewModel = AppVm(this)
        packageName = ""
        appModel = null
        isRunning = true
    }

    constructor(packageName:String) : this()
    {
        viewModel = AppVm(this)
        metrics = DisplayMetrics()
        this.packageName = packageName
        commentList = ArrayList()
        isRunning = true
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
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(QueueBroadCast())
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

        appBinding.recycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        when(Random.nextInt(1,5))
        {
            1 -> { appBinding.imgTop.setImageResource(R.drawable.background_top_one) }

            2 -> { appBinding.imgTop.setImageResource(R.drawable.background_top_two) }

            3 -> { appBinding.imgTop.setImageResource(R.drawable.background_top_three) }

            4 -> { appBinding.imgTop.setImageResource(R.drawable.background_top_four) }
        }

        Picasso.get().load(ApiCaller.ICON_URL+packageName+".png").into(appBinding.img)

        appBinding.btnSubmitComment.setOnClickListener(this::commentClick)
        appBinding.imgBack.setOnClickListener { backPressed() }
        appBinding.btnCancel.setOnClickListener(this::cancelClick)

        Utilities.onBackPressed(appBinding.root, object : Callback
        {
            override fun notify(vararg obj: Any?)
            {
                backPressed()
            }
        })
    }


    private fun initInstallBtn()
    {
        if (!Utilities.checkPackageInstalled(packageName))
        {
            appBinding.btnInstall.text = requireContext().getString(R.string.install)
            (appBinding.btnInstall.layoutParams as LinearLayout.LayoutParams).marginEnd = Utilities.dpToPx(requireActivity(),0f)

        }else if (Utilities.checkPackageInstalled(packageName) && requireContext().packageManager.getPackageInfo(packageName,0).versionCode < appModel?.verCode!!)
        {
            appBinding.btnInstall.text = requireContext().getString(R.string.update)
            appBinding.btnUninstall.visibility = View.VISIBLE
            (appBinding.btnInstall.layoutParams as LinearLayout.LayoutParams).marginEnd = Utilities.dpToPx(requireActivity(),4f)
            (appBinding.btnUninstall.layoutParams as LinearLayout.LayoutParams).marginStart = Utilities.dpToPx(requireActivity(),4f)
        }else
        {
            appBinding.btnInstall.text = requireContext().getString(R.string.open)
            appBinding.btnUninstall.visibility = View.VISIBLE
            (appBinding.btnInstall.layoutParams as LinearLayout.LayoutParams).marginEnd = Utilities.dpToPx(requireActivity(),4f)
            (appBinding.btnUninstall.layoutParams as LinearLayout.LayoutParams).marginStart = Utilities.dpToPx(requireActivity(),4f)
        }
        appBinding.btnInstall.setOnClickListener(this::installClick)
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
        if (DownloadManager.downloadProgress != null)
        {
            if (DownloadManager.downloadQueue != null && DownloadManager.downloadQueue!!.size > 0)
            {
                for (downloadModel:DownloadModel in DownloadManager.downloadQueue!!)
                {
                    if (downloadModel.packageName.equals(packageName))
                    {
                        DownloadManager.downloadProgress!!.observe(viewLifecycleOwner,
                            {
                                if (downloadModel.packageName.equals(packageName))
                                {
                                    when (downloadModel.progress)
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
                                            appBinding.progress.progress = downloadModel.progress
                                            appBinding.txtDownloadStatus.text = getString(R.string.downloading) + " - ${downloadModel.progress}%"
                                        }
                                    }
                                }
                            })
                        break
                    }
                }
            }
        }
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(Intent("QUEUE_HANDLE"))
    }


    private fun installClick(view:View)
    {
        if ((!Utilities.checkPackageInstalled(packageName)) ||
            (Utilities.checkPackageInstalled(packageName) && requireContext().packageManager.getPackageInfo(packageName,0).versionCode < appModel?.verCode!!))
        {
            val download:DownloadModel = DownloadModel().also {

                it.packageName = packageName
                it.isCancel = false
            }

            if (DownloadManager.downloadQueue != null)
            {
                DownloadManager.downloadQueue!!.add(download)

                appBinding.linearBtn.visibility = View.GONE
                appBinding.txtDownloadStatus.visibility = View.VISIBLE
                appBinding.progress.visibility = View.VISIBLE
                appBinding.btnCancel.visibility = View.VISIBLE
                handleProgress()
            }else
            {
                val intent = Intent(requireContext(),DownloadManager::class.java)
                intent.putExtra("download",download)
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


    private fun cancelClick(view: View)
    {
        if (DownloadManager.downloadQueue != null && DownloadManager.downloadQueue!!.size == 1)
        {
            val downloadModel = DownloadManager.downloadQueue!!.get(0)
            requireActivity().stopService(Intent(requireContext(),DownloadManager::class.java))
            deleteFile(downloadModel.packageName!!)
        }else if (DownloadManager.downloadQueue != null && DownloadManager.downloadQueue!!.size > 1)
        {
            if (DownloadManager.downloadQueue!!.get(0).packageName.equals(packageName))
            {
                DownloadManager.downloadQueue!!.get(0).isCancel = true
            }else
            {
                for (i:Int in 0 until DownloadManager.downloadQueue!!.size)
                {
                    if (DownloadManager.downloadQueue!!.get(i).packageName.equals(packageName))
                    {
                        DownloadManager.downloadQueue!!.removeAt(i)
                        break
                    }
                }
            }
        }
        appBinding.txtDownloadStatus.visibility = View.GONE
        appBinding.progress.visibility = View.GONE
        appBinding.btnCancel.visibility = View.GONE
        appBinding.linearBtn.visibility = View.VISIBLE
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


    private fun fileUri(packageName:String) : Uri
    {
        var uri: Uri? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val selection: String = MediaStore.Files.FileColumns.RELATIVE_PATH + "=? and " + MediaStore.Files.FileColumns.DISPLAY_NAME + "=?"
            val selectionArgs: Array<String> = arrayOf(Environment.DIRECTORY_DOWNLOADS + "/", packageName + ".apk")
            val projectionArgs: Array<String> = arrayOf(MediaStore.Files.FileColumns._ID)
            val cursor: Cursor? = requireContext().contentResolver.query(MediaStore.Files.getContentUri("external"), projectionArgs, selection, selectionArgs, null)
            cursor?.moveToFirst()
            uri = Uri.withAppendedPath(MediaStore.Files.getContentUri("external"),cursor?.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)))
        }else
        {
            val file:File = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath+"/"+packageName+".apk")
            uri = Uri.parse(file.absolutePath)
        }
        return uri!!
    }


    private fun deleteFile(packageName:String)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            val where:String = MediaStore.Files.FileColumns.RELATIVE_PATH+" =? and "+MediaStore.Files.FileColumns.DISPLAY_NAME+" =?"
            val selectionArgs:Array<String> = arrayOf(Environment.DIRECTORY_DOWNLOADS+"/",packageName+".apk")
            requireContext().contentResolver.delete(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),where,selectionArgs)
        }else
        {
            val file: File = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath+"/"+packageName+".apk")
            if (file.exists())
            {
                file.delete()
            }
        }
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


    inner class QueueBroadCast : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?)
        {
            handleProgress()
            Toast.makeText(context,"Broad",Toast.LENGTH_LONG).show()
            LocalBroadcastManager.getInstance(this@AppFragment.requireContext()).unregisterReceiver(this)
        }
    }


}