package com.amirhosseinemadi.appstore.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.model.entity.AppModel
import com.amirhosseinemadi.appstore.model.entity.CommentModel
import com.amirhosseinemadi.appstore.model.entity.ResponseObject
import com.amirhosseinemadi.appstore.util.PrefManager
import com.amirhosseinemadi.appstore.view.callback.AppCallback
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable

class AppVm(private val appCallback: AppCallback) : ViewModel() {

    private val apiCaller:ApiCaller

    val error:MutableLiveData<String>
    val appResponse:MutableLiveData<ResponseObject<AppModel>>
    val commentResponse:MutableLiveData<ResponseObject<List<CommentModel>>>
    val submitResponse:MutableLiveData<ResponseObject<String>>
    val deleteResponse:MutableLiveData<ResponseObject<String>>
    val ratingResponse:MutableLiveData<ResponseObject<Float>>

    val rate:MutableLiveData<Float>
    val comment:MutableLiveData<String>

    init
    {
        apiCaller = Application.component.apiCaller()
        error = MutableLiveData()
        appResponse = MutableLiveData()
        commentResponse = MutableLiveData()
        submitResponse = MutableLiveData()
        deleteResponse = MutableLiveData()
        ratingResponse = MutableLiveData()
        rate = MutableLiveData<Float>().also{ it.value = 1f }
        comment = MutableLiveData<String>().also { it.value = "" }
    }


    fun app(packageName:String)
    {
        apiCaller.getApp(packageName,object : SingleObserver<ResponseObject<AppModel>>
        {
            override fun onSubscribe(d: Disposable?) {
                appCallback.onShow()
            }

            override fun onSuccess(t: ResponseObject<AppModel>?) {
                appResponse.value = t
                if (t?.data?.rate != null)
                {
                    val responseObjectRating:ResponseObject<Float> = ResponseObject()
                    responseObjectRating.data = String.format("%.1f",t.data!!.rate).toFloat()
                    ratingResponse.value = responseObjectRating
                }
                //appCallback.onHide()
            }

            override fun onError(e: Throwable?) {
                error.value = "app"
                appCallback.onHide()
            }

        })
    }


    fun comment(offset:Int, access:String?, packageName:String)
    {
        apiCaller.getComments(access,packageName,offset,object : SingleObserver<ResponseObject<List<CommentModel>>>
        {
            override fun onSubscribe(d: Disposable?) {
                //appCallback.onShow()
            }

            override fun onSuccess(t: ResponseObject<List<CommentModel>>?) {
                commentResponse.value = t
                appCallback.onHide()
            }

            override fun onError(e: Throwable?) {
                error.value = "comment"
                appCallback.onHide()
            }

        })
    }


    fun submitComment(access:String, detail:String, rate:Float, packageName:String)
    {
        apiCaller.submitComment(access, detail, rate, packageName, object : SingleObserver<ResponseObject<String>>
        {
            override fun onSubscribe(d: Disposable?) {
                appCallback.onShow()
            }

            override fun onSuccess(t: ResponseObject<String>?) {
                submitResponse.value = t
                appCallback.onHide()
            }

            override fun onError(e: Throwable?) {
                error.value = "submit"
                appCallback.onHide()
            }

        })
    }


    fun deleteComment(access:String, packageName:String)
    {
        apiCaller.deleteComment(access, packageName, object : SingleObserver<ResponseObject<String>>
        {
            override fun onSubscribe(d: Disposable?) {
                appCallback.onShow()
            }

            override fun onSuccess(t: ResponseObject<String>?) {
                deleteResponse.value = t
                appCallback.onHide()
            }

            override fun onError(e: Throwable?) {
                error.value = "delete"
                appCallback.onHide()
            }

        })
    }


    fun getRating(packageName:String)
    {
        apiCaller.getRating(packageName, object : SingleObserver<ResponseObject<Float>>
        {
            override fun onSubscribe(d: Disposable?) {
                appCallback.onShow()
            }

            override fun onSuccess(t: ResponseObject<Float>?) {
                ratingResponse.value = t
                if (t != null)
                {
                    if (t.data == null)
                    {
                        ratingResponse.value?.data = 0f;
                    }
                }
                appCallback.onHide()
            }

            override fun onError(e: Throwable?) {
                error.value = "rating"
                appCallback.onHide()
            }

        })
    }

}