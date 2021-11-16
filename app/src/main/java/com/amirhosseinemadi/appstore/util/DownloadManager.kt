package com.amirhosseinemadi.appstore.util

import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.provider.MediaStore
import android.widget.Toast
import com.amirhosseinemadi.appstore.R
import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.model.entity.DownloadModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.*
import java.lang.NullPointerException
import kotlin.jvm.Throws

class DownloadManager : Service() {

    private val apiCaller:ApiCaller

    companion object
    {
        var downloadQueue:MutableList<DownloadModel>? = null
    }

    init
    {
        apiCaller = Application.component.apiCaller()
    }

    override fun onBind(intent: Intent?): IBinder?
    {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        downloadQueue = ArrayList()
        val downloadModel:DownloadModel? = intent?.extras?.getParcelable("download")

        if (downloadModel != null)
        {
            if (!checkFileExist(downloadModel.packageName))
            {
                download(downloadModel)
            }else
            {
                Toast.makeText(this,"EXISTS",Toast.LENGTH_LONG).show()
                downloadModel.progress?.value = 1001
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
                        stopSelf()
                    }
                }else
                {
                    stopSelf()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
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


    @Throws(IOException::class)
    private fun writeToFile(response:Response<ResponseBody>, outputStream:OutputStream, downloadModel:DownloadModel) : Observable<Int>
    {
        val observable: Observable<Int> = Observable.generate {

            try
            {
                val inputStream: InputStream = response.body()!!.byteStream()
                val fileSize: Long = response.headers().get("my-content-length")!!.toLong()

                var i: Int = 0
                val buffer: ByteArray = ByteArray(2048)
                var progress: Long = 0

                while ((inputStream.read(buffer).also { i = it } != -1))
                {
                    outputStream.write(buffer, 0, i)
                    progress += i
                    downloadModel.progress!!.postValue((progress*100/fileSize).toInt())
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()

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
        apiCaller.download(downloadModel.packageName, object : Observer<Response<ResponseBody>>
        {
            override fun onSubscribe(d: Disposable?) {

            }

            override fun onNext(t: Response<ResponseBody>?) {
                if (t?.body() != null)
                {
                    val outputStream: OutputStream? = createOutputStream(downloadModel.packageName)
                    writeToFile(t, outputStream!!,downloadModel).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : Observer<Int>
                        {
                            override fun onSubscribe(d: Disposable?) {

                            }

                            override fun onNext(t: Int?) {

                            }

                            override fun onError(e: Throwable?) {
                                downloadModel.progress?.value = 1000
                                Toast.makeText(this@DownloadManager, R.string.request_failed,Toast.LENGTH_LONG).show()
                                Toast.makeText(this@DownloadManager, e?.message,Toast.LENGTH_LONG).show()
                            }

                            override fun onComplete() {
                                downloadModel.progress?.value = 1001
                            }

                        })
                }
            }

            override fun onError(e: Throwable?) {
                Toast.makeText(this@DownloadManager, R.string.request_failed,Toast.LENGTH_LONG).show()
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
                        stopSelf()
                    }
                }else
                {
                    stopSelf()
                }
            }

            override fun onComplete() {
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
                        stopSelf()
                    }
                }else
                {
                    stopSelf()
                }
            }

        })
    }

}