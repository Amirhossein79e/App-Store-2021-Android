package com.amirhosseinemadi.appstore.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.model.entity.AppModel
import com.amirhosseinemadi.appstore.model.entity.ResponseObject
import com.amirhosseinemadi.appstore.view.callback.SearchCallback
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable

class SearchVm(private val searchCallback:SearchCallback) : ViewModel() {

    private val apiCaller:ApiCaller
    val error:MutableLiveData<String>
    val titleResponse:MutableLiveData<ResponseObject<List<String>>>
    val appResponse:MutableLiveData<ResponseObject<List<AppModel>>>

    init
    {
        apiCaller = Application.component.apiCaller()
        error = MutableLiveData()
        titleResponse = MutableLiveData()
        appResponse = MutableLiveData()
    }


    fun title(query:String)
    {
        apiCaller.getTitlesSearch(query,object : SingleObserver<ResponseObject<List<String>>>
        {
            override fun onSubscribe(d: Disposable?) {
                searchCallback.onShow()
            }

            override fun onSuccess(t: ResponseObject<List<String>>?) {
                titleResponse.value = t
                searchCallback.onHide()
            }

            override fun onError(e: Throwable?) {
                error.value = "title"
                searchCallback.onHide()
            }

        })
    }


    fun app(offset:Int, query:String)
    {
        apiCaller.getAppsSearch(offset,query,object : SingleObserver<ResponseObject<List<AppModel>>>
        {
            override fun onSubscribe(d: Disposable?) {
                searchCallback.onShow()
            }

            override fun onSuccess(t: ResponseObject<List<AppModel>>?) {
                appResponse.value = t
                searchCallback.onHide()
            }

            override fun onError(e: Throwable?) {
                error.value = "app"
                searchCallback.onHide()
            }

        })
    }


    fun getTitleResponse(query:String) : MutableLiveData<ResponseObject<List<String>>>
    {
        title(query)
        return titleResponse
    }


    fun getAppResponse(offset:Int, query: String) : MutableLiveData<ResponseObject<List<AppModel>>>
    {
        app(offset,query)
        return appResponse
    }

}