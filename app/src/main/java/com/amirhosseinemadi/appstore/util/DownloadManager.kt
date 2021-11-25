package com.amirhosseinemadi.appstore.util

import android.app.*
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.model.entity.DownloadModel
import com.amirhosseinemadi.appstore.view.fragment.AppFragment
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.*
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class DownloadManager : Service() {

    private val apiCaller:ApiCaller
    private var currentThread:Thread?
    private lateinit var notificationManager:NotificationManagerCompat

    companion object
    {
        var downloadQueue:MutableList<DownloadModel>? = null
        var downloadProgress:MutableLiveData<DownloadModel>? = null
    }

    init
    {
        apiCaller = Application.component.apiCaller()
        currentThread = null
    }

    override fun onBind(intent: Intent?): IBinder?
    {
        return null
    }


    override fun onCreate() {
        super.onCreate()
        downloadQueue = ArrayList()
        downloadProgress = MutableLiveData()
        notificationManager = NotificationManagerCompat.from(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            notificationManager.createNotificationChannel(NotificationChannel(("1000"),"download",NotificationManager.IMPORTANCE_DEFAULT))
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        if (intent?.extras?.getString("task").equals("start"))
        {
            val downloadModel:DownloadModel? = intent?.extras?.getParcelable("download")
            if (downloadModel != null && !downloadModel.isCancel)
            {
                if (downloadQueue!!.size == 0)
                {
                    downloadQueue!!.add(downloadModel)
                    startForeground(1000,progressNotification(downloadModel,0,true))
                    Executors.defaultThreadFactory().newThread { download(downloadQueue!!.get(0)) }.start()
                }else
                {
                    downloadQueue!!.add(downloadModel)
                }
                LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("QUEUE_RESULT"))
            }

        }else if (intent?.extras?.getString("task").equals("stop"))
        {
            val downloadModel:DownloadModel? = intent?.extras?.getParcelable("download")
            if (downloadModel != null && downloadModel.isCancel)
            {
                for (i:Int in 0 until downloadQueue!!.size)
                {
                    val download:DownloadModel = downloadQueue!!.get(i)
                    if (downloadModel.packageName.equals(download.packageName))
                    {
                        if (i == 0)
                        {
                            downloadQueue!!.get(0).isCancel = true
                        }else
                        {
                            downloadQueue!!.removeAt(i)
                        }
                    }
                }
            }
        }

        return START_NOT_STICKY
    }


    private fun progressNotification(downloadModel:DownloadModel, progress:Int, isIndeterminate:Boolean) : Notification
    {
        val notificationCompat:NotificationCompat.Builder = NotificationCompat.Builder(this,"1000")
            .setSmallIcon(R.drawable.ic_update)
            .setOnlyAlertOnce(true)
            .setContentTitle("Downloading ${downloadModel.appName}")
            .setContentText(progress.toString()+"%")
            .setProgress(100,progress,isIndeterminate)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            notificationCompat.setPriority(NotificationManager.IMPORTANCE_LOW)
        }else
        {
            notificationCompat.setPriority(Notification.PRIORITY_LOW)
        }

        return notificationCompat.build()
    }


    private fun finishNotification(downloadModel:DownloadModel) : Notification
    {
        val notificationCompat:NotificationCompat.Builder = NotificationCompat.Builder(this,"1000")
            .setSmallIcon(R.drawable.ic_update)
            .setContentTitle(getString(R.string.download_finished))
            .setContentText(downloadModel.appName + getString(R.string.downloaded))
            .setContentIntent(PendingIntent.getActivity(this,0,createIntent(downloadModel.packageName!!),0))
            .setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            notificationCompat.setPriority(NotificationManager.IMPORTANCE_DEFAULT)
        }else
        {
            notificationCompat.setPriority(Notification.PRIORITY_DEFAULT)
        }

        return notificationCompat.build()
    }


    private fun updateProgress(downloadModel:DownloadModel,progress:Int)
    {
        downloadModel.progress = progress
        downloadProgress?.postValue(downloadModel)
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
            val fileCv:ContentValues = ContentValues()
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


    private fun deleteFile(packageName:String,int: Int?)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            val where: String = MediaStore.Files.FileColumns.RELATIVE_PATH + "=? and " + MediaStore.Files.FileColumns.DISPLAY_NAME + " =?"
            val selectionArgs: Array<String> = arrayOf(Environment.DIRECTORY_DOWNLOADS + "/", packageName + ".apk")
            contentResolver.delete(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), where, selectionArgs)
        } else
        {
            val file: File = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/" + packageName + ".apk")
            if (file.exists())
            {
                file.delete()
            }
        }
    }


    private fun createIntent(packageName:String) : Intent
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            val selection: String = MediaStore.Files.FileColumns.RELATIVE_PATH + "=? and " + MediaStore.Files.FileColumns.DISPLAY_NAME + "=?"
            val selectionArgs: Array<String> = arrayOf(Environment.DIRECTORY_DOWNLOADS + "/", packageName + ".apk")
            val projectionArgs: Array<String> = arrayOf(MediaStore.Files.FileColumns._ID)
            val cursor: Cursor? = contentResolver.query(MediaStore.Files.getContentUri("external"), projectionArgs, selection, selectionArgs, null)

            cursor?.moveToFirst()

            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
            intent.setDataAndType(Uri.withAppendedPath(MediaStore.Files.getContentUri("external"), cursor?.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))), "application/vnd.android.package-archive")
            cursor?.close()

            return intent

        } else
        {
            val file: File = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/" + packageName + ".apk")

            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
            intent.setDataAndType(Uri.parse(Uri.fromFile(file).toString()),"application/vnd.android.package-archive")

            return intent
        }
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
                if (currentThread != null && !currentThread!!.isInterrupted)
                {
                    currentThread!!.interrupt()
                }
                stopForeground(true)
                stopSelf()
            }
        }else
        {
            if (currentThread != null && !currentThread!!.isInterrupted)
            {
                currentThread!!.interrupt()
            }
            stopForeground(true)
            stopSelf()
        }
    }


    @Throws(IOException::class)
    private fun writeToFile(response:Response<ResponseBody>, outputStream:OutputStream, downloadModel:DownloadModel, queueBroadCast:BroadcastReceiver)
    {
        downloadProgress?.postValue(downloadModel)

        try
        {
            val inputStream: InputStream = response.body()!!.byteStream()
            val fileSize: Long = response.headers().get("my-content-length")!!.toLong()

            var i: Int = 0
            val buffer: ByteArray = ByteArray(4096)
            var progress: Long = 0

            while ((inputStream.read(buffer).also { i = it } != -1))
            {
                if (!downloadModel.isCancel)
                {
                    outputStream.write(buffer, 0, i)
                    progress += i
                    updateProgress(downloadModel,(progress*100/fileSize).toInt())
                    val notification:Notification = progressNotification(downloadModel,(progress*100/fileSize).toInt(),false)
                    notificationManager.notify(1000,notification)
                }else
                {
                    updateProgress(downloadModel,1001)
                    break
                }
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            if (fileSize == progress)
            {
                updateProgress(downloadModel, 1000)
                val notification:Notification = finishNotification(downloadModel)
                notificationManager.notify(Random().nextInt(),notification)
                startActivity(createIntent(downloadModel.packageName!!))
            }else
            {
                updateProgress(downloadModel, 1002)
                deleteFile(downloadModel.packageName!!,null)
            }

            handleQueue(downloadModel)

        }catch (exception:Exception)
        {
            if (AppFragment.isRunning != null && AppFragment.isRunning!!)
            {
                LocalBroadcastManager.getInstance(this@DownloadManager).registerReceiver(queueBroadCast, IntentFilter("QUEUE_HANDLE"))
            } else
            {
                handleQueue(downloadModel)
            }
            updateProgress(downloadModel, 1002)
            deleteFile(downloadModel.packageName!!, null)
        }
    }


    private fun download(downloadModel:DownloadModel)
    {
        class QueueBroadCast : BroadcastReceiver() {

            override fun onReceive(context: Context?, intent: Intent?) {
                handleQueue(downloadModel)
                LocalBroadcastManager.getInstance(this@DownloadManager).unregisterReceiver(this)
            }

        }

        if (!checkFileExist(downloadModel.packageName!!))
        {
            try
            {
                val response: Response<ResponseBody> = apiCaller.download(downloadModel.packageName!!)

                if (response.body() != null && !downloadModel.isCancel)
                {
                    val outputStream: OutputStream? = createOutputStream(downloadModel.packageName!!)
                    writeToFile(response, outputStream!!, downloadModel, QueueBroadCast())
                }
            }catch (exception:java.lang.Exception)
            {
                updateProgress(downloadModel, 1002)
                deleteFile(downloadModel.packageName!!, null)

                if (AppFragment.isRunning != null && AppFragment.isRunning!!)
                {
                    LocalBroadcastManager.getInstance(this@DownloadManager).registerReceiver(QueueBroadCast(), IntentFilter("QUEUE_HANDLE"))
                } else
                {
                    handleQueue(downloadModel)
                }
            }

        }else
        {
            updateProgress(downloadModel,1000)
            startActivity(createIntent(downloadModel.packageName!!))

            if (AppFragment.isRunning != null && AppFragment.isRunning!!)
            {
                LocalBroadcastManager.getInstance(this@DownloadManager).registerReceiver(QueueBroadCast(), IntentFilter("QUEUE_HANDLE"))
            }else
            {
                handleQueue(downloadModel)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        downloadQueue = null
        downloadProgress = null
    }

}