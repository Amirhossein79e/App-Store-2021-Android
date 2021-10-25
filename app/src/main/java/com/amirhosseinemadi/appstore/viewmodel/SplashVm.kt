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

    private val apiCaller:ApiCaller
    val error:MutableLiveData<String>
    val initResponse:MutableLiveData<ResponseObject<String>>
    val syncResponse:MutableLiveData<ResponseObject<String>>
    val syncUserResponse:MutableLiveData<ResponseObject<String>>

    init
    {
        apiCaller = Application.component.apiCaller()
        error = MutableLiveData()
        initResponse = MutableLiveData()
        syncResponse = MutableLiveData()
        syncUserResponse = MutableLiveData()
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
                error.value = "init"
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
                error.value = "sync"
            }
        })
    }


    public fun syncUser(access:String, token:String)
    {
        apiCaller.syncUser(access,token,object : SingleObserver<ResponseObject<String>>
        {
            override fun onSubscribe(d: Disposable?) {

            }

            override fun onSuccess(t: ResponseObject<String>?) {
                syncUserResponse.value = t
            }

            override fun onError(e: Throwable?) {
                error.value = "syncUser"
            }

        })
    }


    public fun getInitResponse(uid:String, token:String) : MutableLiveData<ResponseObject<String>>
    {
        init(uid,token)
        return initResponse
    }


    public fun getSyncResponse(uid:String, token:String) : MutableLiveData<ResponseObject<String>>
    {
        sync(uid,token)
        return syncResponse
    }


    public fun getSyncUserResponse(access:String, token:String) : MutableLiveData<ResponseObject<String>>
    {
        syncUser(access,token)
        return syncUserResponse
    }

}