package com.amirhosseinemadi.appstore.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.model.entity.AppModel
import com.amirhosseinemadi.appstore.model.entity.DownloadModel
import com.amirhosseinemadi.appstore.view.fragment.AppFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.*

class DownloadManager : Service() {

    private val apiCaller:ApiCaller
    private lateinit var notificationManager:NotificationManagerCompat
    private lateinit var notificationCompat:NotificationCompat.Builder

    companion object
    {
        var downloadQueue:MutableList<DownloadModel>? = null
        var downloadProgress:MutableLiveData<DownloadModel>? = null
    }

    init
    {
        apiCaller = Application.component.apiCaller()
        downloadProgress = MutableLiveData()
    }

    override fun onBind(intent: Intent?): IBinder?
    {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        downloadQueue = ArrayList()
        val downloadModel:DownloadModel? = intent?.extras?.getParcelable("download")

        if (downloadModel != null && !downloadModel.isCancel)
        {
            notificationManager = NotificationManagerCompat.from(this)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                val channel:NotificationChannel = NotificationChannel("1000","Foreground Service",NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(channel)
            }
            startForeground(1000,createNotification(downloadModel,0,true))

            downloadQueue!!.add(downloadModel)
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("QUEUE_RESULT"))
            download(downloadQueue!!.get(0))
        }

        return super.onStartCommand(intent, flags, startId)
    }


    private fun createNotification(downloadModel:DownloadModel,progress:Int,isIndeterminate:Boolean) : Notification
    {
        val notificationCompat:NotificationCompat.Builder = NotificationCompat.Builder(this,"1000")
        notificationCompat.setSmallIcon(R.drawable.ic_update)
        notificationCompat.setContentTitle("Downloading ${downloadModel.appName}")
        notificationCompat.setContentText(downloadModel.appName)
        notificationCompat.setProgress(100,progress,isIndeterminate)
        return notificationCompat.build()
    }


    private fun checkFileExist(packageName:String) : Boolean
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            val selection:String = MediaStore.Files.FileColumns.RELATIVE_PATH + "=? and " +MediaStore.Files.FileColumns.DISPLAY_NAME+ "=?"
            val selectionArgs:Array<String> = arrayOf(Environment.DIRECTORY_DOWNLOADS+"/",packageName+".apk")
            val projectionArgs:Array<String> = arrayOf(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val cursor: Cursor? = contentResolver.query(MediaStore.Files.getContentUri("external"), projectionArgs, selection, selectionArgs, null)
            val result:Boolean = cursor!!.count > 0
            cursor.close()
            return result
        }else
        {
            val file:File = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath+"/"+packageName+".apk")
            return file.exists()
        }
    }


    @Throws(NullPointerException::class,IOException::class)
    private fun createOutputStream(packageName:String) : OutputStream?
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            val fileCv: ContentValues = ContentValues()
            fileCv.put(MediaStore.Files.FileColumns.DISPLAY_NAME, packageName)
            fileCv.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/vnd.android.package-archive")
            fileCv.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            val uri: Uri? = contentResolver.insert(MediaStore.Files.getContentUri("external"), fileCv)
            return contentResolver.openOutputStream(uri!!)
        }else
        {
            val file: File = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),packageName + ".apk")
            file.createNewFile()
            return FileOutputStream(file)
        }
    }


    private fun updateProgress(downloadModel:DownloadModel,progress:Int)
    {
        downloadModel.progress = progress
        downloadProgress?.postValue(downloadModel)
        //Thread.sleep(300)
    }


    private fun handleQueue(downloadModel:DownloadModel)
    {
        if (downloadQueue != null && downloadQueue!!.size > 0)
        {
            for (i:Int in 0 until downloadQueue!!.size)
            {
                if (downloadQueue!!.get(i).packageName.equals(downloadModel.packageName))
                {
                    downloadQueue!!.removeAt(i)
                    break
                }
            }

            if (downloadQueue!!.size > 0)
            {
                download(downloadQueue!!.get(0))
            }else
            {
                notificationManager.cancel(1000)
                stopForeground(true)
                stopSelf()
            }
        }else
        {
            notificationManager.cancel(1000)
            stopForeground(true)
            stopSelf()
        }
    }


    @Throws(IOException::class)
    private fun writeToFile(response:Response<ResponseBody>, outputStream:OutputStream, downloadModel:DownloadModel) : Observable<Int>
    {
        downloadProgress?.value = downloadModel

        val observable:Observable<Int> = Observable.generate {

            try
            {
                val inputStream: InputStream = response.body()!!.byteStream()
                val fileSize: Long = response.headers().get("my-content-length")!!.toLong()

                var i: Int = 0
                val buffer: ByteArray = ByteArray(2048)
                var progress: Long = 0

                while ((inputStream.read(buffer).also { i = it } != -1))
                {
                    if (!downloadModel.isCancel)
                    {
                        outputStream.write(buffer, 0, i)
                        progress += i
                        updateProgress(downloadModel,(progress*100/fileSize).toInt())
                        val notification:Notification = createNotification(downloadModel,(progress*100/fileSize).toInt(),false)
                        notificationManager.notify(1000,notification)
                    }else
                    {
                        notificationManager.cancel(1000)
                        updateProgress(downloadModel,1001)
                        deleteFile(downloadModel.packageName!!,null)
                        it.onError(IOException())
                        break
                    }
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()

                if (fileSize == progress)
                {
                    it.onNext(1)
                }
                it.onComplete()

            }catch (exception:Exception)
            {
                it.onError(exception)
            }
        }

        return observable
    }


    private fun download(downloadModel:DownloadModel)
    {
        class QueueBroadCast : BroadcastReceiver() {

            override fun onReceive(context: Context?, intent: Intent?)
            {
                handleQueue(downloadModel)
                LocalBroadcastManager.getInstance(this@DownloadManager).unregisterReceiver(this)
            }
        }

        if (!checkFileExist(downloadModel.packageName!!))
        {
            apiCaller.download(downloadModel.packageName!!, object : Observer<Response<ResponseBody>>
            {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(t: Response<ResponseBody>?) {
                    if (t?.body() != null && !downloadModel.isCancel)
                    {
                        val outputStream: OutputStream? = createOutputStream(downloadModel.packageName!!)
                        writeToFile(t, outputStream!!,downloadModel).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(object : Observer<Int>
                            {
                                override fun onSubscribe(d: Disposable?) {

                                }

                                override fun onNext(t: Int?) {
                                    if (AppFragment.isRunning != null && AppFragment.isRunning!!)
                                    {
                                        LocalBroadcastManager.getInstance(this@DownloadManager).registerReceiver(QueueBroadCast(), IntentFilter("QUEUE_HANDLE"))
                                    }else
                                    {
                                        handleQueue(downloadModel)
                                    }
                                    updateProgress(downloadModel,1000)
                                    fileUri(downloadModel.packageName!!)
                                }

                                override fun onError(e: Throwable?) {
                                    if (AppFragment.isRunning != null && AppFragment.isRunning!!)
                                    {
                                        LocalBroadcastManager.getInstance(this@DownloadManager).registerReceiver(QueueBroadCast(), IntentFilter("QUEUE_HANDLE"))
                                    }else
                                    {
                                        handleQueue(downloadModel)
                                    }
                                    updateProgress(downloadModel,1002)
                                    deleteFile(downloadModel.packageName!!,null)
                                }

                                override fun onComplete() {

                                }

                            })
                    }
                }

                override fun onError(e: Throwable?) {
                    if (AppFragment.isRunning != null && AppFragment.isRunning!!)
                    {
                        LocalBroadcastManager.getInstance(this@DownloadManager).registerReceiver(QueueBroadCast(), IntentFilter("QUEUE_HANDLE"))
                    }else
                    {
                        handleQueue(downloadModel)
                    }
                    Toast.makeText(this@DownloadManager, R.string.request_failed,Toast.LENGTH_LONG).show()
                }

                override fun onComplete() {
                    //handleQueue(downloadModel)
                }

            })
        }else
        {
            if (AppFragment.isRunning != null && AppFragment.isRunning!!)
            {
                LocalBroadcastManager.getInstance(this@DownloadManager).registerReceiver(QueueBroadCast(), IntentFilter("QUEUE_HANDLE"))
            }else
            {
                handleQueue(downloadModel)
            }
            updateProgress(downloadModel,1000)
            fileUri(downloadModel.packageName!!)
        }
    }


    private fun fileUri(packageName:String) : Uri?
    {
        try {
            var uri: Uri? = null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val selection: String =
                    MediaStore.Files.FileColumns.RELATIVE_PATH + "=? and " + MediaStore.Files.FileColumns.DISPLAY_NAME + "=?"
                val selectionArgs: Array<String> =
                    arrayOf(Environment.DIRECTORY_DOWNLOADS + "/", packageName + ".apk")
                val projectionArgs: Array<String> = arrayOf(MediaStore.Files.FileColumns._ID)
                val cursor: Cursor? = contentResolver.query(
                    MediaStore.Files.getContentUri("external"),
                    projectionArgs,
                    selection,
                    selectionArgs,
                    null
                )
                cursor?.moveToFirst()
                uri = Uri.withAppendedPath(
                    MediaStore.Files.getContentUri("external"),
                    cursor?.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
                )
                cursor?.close()
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
                intent.setDataAndType(uri, "application/vnd.android.package-archive")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                val file: File =
                    File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/" + packageName + ".apk")
                uri = Uri.parse(file.absolutePath)
            }
            return uri!!
        }catch (exception:Exception)
        {
            return null
        }
    }


    private fun deleteFile(packageName:String,int: Int?)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            val where:String = MediaStore.Files.FileColumns.RELATIVE_PATH+" =? and "+MediaStore.Files.FileColumns.DISPLAY_NAME+" =?"
            val selectionArgs:Array<String> = arrayOf(Environment.DIRECTORY_DOWNLOADS+"/",packageName+".apk")
            contentResolver.delete(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),where,selectionArgs)
        }else
        {
            val file: File = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath+"/"+packageName+".apk")
            if (file.exists())
            {
                file.delete()
            }
        }
    }


    override fun onDestroy() {
        downloadQueue = null
        downloadProgress = null
        notificationManager.cancel(1000)
        stopForeground(true)
        super.onDestroy()
    }

}