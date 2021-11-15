package com.amirhosseinemadi.appstore.util

import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.os.MemoryFile
import android.provider.MediaStore
import android.widget.Toast
import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.model.ApiCaller
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class DownloadManager : Service() {

    private val apiCaller:ApiCaller

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
        if (intent != null && intent.extras != null && intent.extras!!.getString("packageName") != null)
        {
            apiCaller.download(intent.extras!!.getString("packageName")!!, object : Observer<Response<ResponseBody>>
            {
                override fun onSubscribe(d: Disposable?) {

                }

                override fun onNext(t: Response<ResponseBody>?)
                {
                    if (t?.body() != null)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        {
                            if (!checkFileExist(intent.extras!!.getString("packageName")!!))
                            {
                                val uri:Uri? = createUri(intent.extras!!.getString("packageName")!!)
                                val outputStream:OutputStream? = contentResolver.openOutputStream(uri!!)
                                writeToFile(t.body()!!.byteStream(),outputStream!!)
                            }else
                            {
                                Toast.makeText(this@DownloadManager,"EXIST",Toast.LENGTH_LONG).show()
                            }
                        }else
                        {
                            if (!checkFileExist(intent.extras!!.getString("packageName")!!))
                            {
                                val file:File = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),intent.extras!!.getString("packageName")!!+".apk")
                                file.createNewFile()
                                val outputStream:FileOutputStream = FileOutputStream(file)
                                writeToFile(t.body()!!.byteStream(),outputStream)
                            }
                        }
                    }
                }

                override fun onError(e: Throwable?) {

                }

                override fun onComplete() {

                }

            })
        }
        return super.onStartCommand(intent, flags, startId)
    }


    private fun writeToFile(inputStream:InputStream, outputStream:OutputStream)
    {
        var i:Int = 0
        val buffer:ByteArray = ByteArray(2048)

        while ((inputStream.read(buffer).also { i = it } != -1))
        {
            outputStream.write(buffer,0,i)
        }

        outputStream.flush()
        outputStream.close()
    }


    private fun createUri(packageName:String) : Uri?
    {
        val fileCv:ContentValues = ContentValues()
        fileCv.put(MediaStore.Files.FileColumns.DISPLAY_NAME,packageName)
        fileCv.put(MediaStore.Files.FileColumns.MIME_TYPE,"application/vnd.android.package-archive")
        fileCv.put(MediaStore.Files.FileColumns.RELATIVE_PATH,Environment.DIRECTORY_DOWNLOADS)
        val uri:Uri? = contentResolver.insert(MediaStore.Files.getContentUri("external"),fileCv)
        return uri
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

}