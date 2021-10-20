package com.amirhosseinemadi.appstore.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.model.entity.AppModel
import com.amirhosseinemadi.appstore.model.entity.HomeModel
import com.amirhosseinemadi.appstore.model.entity.ResponseObject
import com.amirhosseinemadi.appstore.view.callback.Callback
import com.amirhosseinemadi.appstore.view.callback.HomeCallback
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable

class HomeVm(private val homeCallback: HomeCallback) : ViewModel() {

    private val apiCaller:ApiCaller
    val error:MutableLiveData<String>
    val homeResponse:MutableLiveData<ResponseObject<HomeModel>>
    val appResponse:MutableLiveData<ResponseObject<List<AppModel>>>

    init
    {
        apiCaller = Application.component.apiCaller()
        error = MutableLiveData()
        homeResponse = MutableLiveData()
        appResponse = MutableLiveData()
    }


    public fun home()
    {
        apiCaller.getHome(object : SingleObserver<ResponseObject<HomeModel>>
        {
            override fun onSubscribe(d: Disposable?) {
                homeCallback.onShow()
            }

            override fun onSuccess(t: ResponseObject<HomeModel>?) {
                homeResponse.value = t
                //homeCallback.onHide()
            }

            override fun onError(e: Throwable?) {
                error.value = "home"
                homeCallback.onHide()
            }

        })
    }


    public fun app(category:String, callback: Callback?)
    {
        apiCaller.getAppsByCategory(0,category,object : SingleObserver<ResponseObject<List<AppModel>>>
        {
            override fun onSubscribe(d: Disposable?) {

            }

            override fun onSuccess(t: ResponseObject<List<AppModel>>?) {
                appResponse.value = t
                callback?.notify()
            }

            override fun onError(e: Throwable?) {
                error.value = category
            }

        })
    }


    public fun getHomeResponse(vararg str:String) : MutableLiveData<ResponseObject<HomeModel>>
    {
        home()
        return homeResponse
    }


    public fun getAppResponse(category:String) : MutableLiveData<ResponseObject<List<AppModel>>>
    {
        app(category,null)
        return appResponse
    }

}