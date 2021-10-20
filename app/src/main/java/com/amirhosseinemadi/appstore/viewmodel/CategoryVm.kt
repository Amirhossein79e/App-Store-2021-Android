package com.amirhosseinemadi.appstore.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.model.entity.CategoryModel
import com.amirhosseinemadi.appstore.model.entity.ResponseObject
import com.amirhosseinemadi.appstore.view.callback.CategoryCallback
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable

class CategoryVm(private val categoryCallback:CategoryCallback) : ViewModel() {

    private val apiCaller:ApiCaller
    val error:MutableLiveData<String>
    val categoryResponse:MutableLiveData<ResponseObject<List<CategoryModel>>>

    init
    {
        apiCaller = Application.component.apiCaller()
        error = MutableLiveData()
        categoryResponse = MutableLiveData()
    }


    public fun category()
    {
        apiCaller.getCategories(object : SingleObserver<ResponseObject<List<CategoryModel>>>
        {
            override fun onSubscribe(d: Disposable?) {
                categoryCallback.onShow()
            }

            override fun onSuccess(t: ResponseObject<List<CategoryModel>>?) {
                categoryResponse.value = t
                categoryCallback.onHide()
            }

            override fun onError(e: Throwable?) {
                error.value = "category"
                categoryCallback.onHide()
            }

        })
    }


    public fun getCategoryResponse(vararg obj:String) : MutableLiveData<ResponseObject<List<CategoryModel>>>
    {
        category()
        return categoryResponse
    }


}