package com.amirhosseinemadi.appstore.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.model.entity.ResponseObject
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable

class SplashVm : ViewModel() {

    private var apiCaller:ApiCaller
    var initResponse:MutableLiveData<ResponseObject<String>>
    var syncResponse:MutableLiveData<ResponseObject<String>>
    var error:MutableLiveData<Throwable>


    init
    {
        apiCaller = Application.component.apiCaller()
        initResponse = MutableLiveData()
        syncResponse = MutableLiveData()
        error = MutableLiveData()
    }


    public fun init(uid:String, token:String)
    {
        apiCaller.initToken(uid,token,object : SingleObserver<ResponseObject<String>>
        {
            override fun onSubscribe(d: Disposable?) {

            }

            override fun onSuccess(t: ResponseObject<String>?) {
                initResponse.value = t
            }

            override fun onError(e: Throwable?) {
                error.value = e
            }
        })
    }


    public fun sync(uid:String, token:String)
    {
        apiCaller.syncToken(uid,token,object : SingleObserver<ResponseObject<String>>
        {
            override fun onSubscribe(d: Disposable?) {

            }

            override fun onSuccess(t: ResponseObject<String>?) {
                syncResponse.value = t
            }

            override fun onError(e: Throwable?) {
                error.value = e
            }
        })
    }


    public fun getInitResponse(uid:String,token:String) : MutableLiveData<ResponseObject<String>>
    {
        init(uid,token)
        return initResponse
    }


    public fun getSyncResponse(uid:String,token:String) : MutableLiveData<ResponseObject<String>>
    {
        sync(uid,token)
        return syncResponse
    }

}