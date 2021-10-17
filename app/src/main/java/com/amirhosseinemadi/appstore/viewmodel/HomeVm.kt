package com.amirhosseinemadi.appstore.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.model.entity.HomeModel
import com.amirhosseinemadi.appstore.model.entity.ResponseObject
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable

class HomeVm : ViewModel() {

    private val apiCaller:ApiCaller
    val error:MutableLiveData<Throwable>
    val homeResponse:MutableLiveData<ResponseObject<HomeModel>>

    init
    {
        apiCaller = Application.component.apiCaller()
        error = MutableLiveData()
        homeResponse = MutableLiveData()
    }


    public fun home()
    {
        apiCaller.getHome(object : SingleObserver<ResponseObject<HomeModel>>
        {
            override fun onSubscribe(d: Disposable?) {

            }

            override fun onSuccess(t: ResponseObject<HomeModel>?) {
                homeResponse.value = t
            }

            override fun onError(e: Throwable?) {
                error.value = e
            }

        })
    }


    public fun getHomeResponse(vararg str:String) : MutableLiveData<ResponseObject<HomeModel>>
    {
        home()
        return homeResponse
    }

}