package com.amirhosseinemadi.appstore.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.model.entity.AppModel
import com.amirhosseinemadi.appstore.model.entity.CommentModel
import com.amirhosseinemadi.appstore.model.entity.ResponseObject
import com.amirhosseinemadi.appstore.view.callback.AppCallback
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable

class AppVm(private val appCallback: AppCallback) : ViewModel() {

    private val apiCaller:ApiCaller
    val error:MutableLiveData<String>
    val appResponse:MutableLiveData<ResponseObject<AppModel>>
    val commentResponse:MutableLiveData<ResponseObject<CommentModel>>
    val submitResponse:MutableLiveData<String>
    val deleteResponse:MutableLiveData<String>

    init
    {
        apiCaller = Application.component.apiCaller()
        error = MutableLiveData()
        appResponse = MutableLiveData()
        commentResponse = MutableLiveData()
        submitResponse = MutableLiveData()
        deleteResponse = MutableLiveData()
    }


    private fun app(packageName:String)
    {
        apiCaller.getApp(packageName,object : SingleObserver<ResponseObject<AppModel>>
        {
            override fun onSubscribe(d: Disposable?) {
                appCallback.onShow()
            }

            override fun onSuccess(t: ResponseObject<AppModel>?) {
                appResponse.value = t
                appCallback.onHide()
            }

            override fun onError(e: Throwable?) {
                error.value = "app"
                appCallback.onHide()
            }

        })
    }




}