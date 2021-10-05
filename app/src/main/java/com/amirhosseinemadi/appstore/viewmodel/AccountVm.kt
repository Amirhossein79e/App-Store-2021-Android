package com.amirhosseinemadi.appstore.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.model.ApiCaller
import com.amirhosseinemadi.appstore.model.entity.ResponseObject
import com.amirhosseinemadi.appstore.model.entity.UserModel
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable

class AccountVm : ViewModel() {

    private var apiCaller: ApiCaller
    var error:MutableLiveData<Throwable>
    val signUpResponse:MutableLiveData<ResponseObject<UserModel>>
    val signInResponse:MutableLiveData<ResponseObject<UserModel>>

    init
    {
        apiCaller = Application.component.apiCaller()
        error = MutableLiveData()
        signUpResponse = MutableLiveData()
        signInResponse = MutableLiveData()
    }


    public fun signUp(email:String, password:String, username:String,token:String)
    {
        apiCaller.signUpUser(email,password,username,token,object : SingleObserver<ResponseObject<UserModel>>
        {
            override fun onSubscribe(d: Disposable?) {

            }

            override fun onSuccess(t: ResponseObject<UserModel>?) {
                signUpResponse.value = t
            }

            override fun onError(e: Throwable?) {
                error.value = e
            }

        })
    }


    public fun signIn(email:String, password:String)
    {
        apiCaller.signInUser(email,password,object : SingleObserver<ResponseObject<UserModel>>
        {
            override fun onSubscribe(d: Disposable?) {

            }

            override fun onSuccess(t: ResponseObject<UserModel>?) {
                signInResponse.value = t
            }

            override fun onError(e: Throwable?) {
                error.value = e
            }

        })
    }


    public fun getSignUpResponse(email:String, password:String, username:String,token:String) : MutableLiveData<ResponseObject<UserModel>>
    {
        signUp(email, password, username, token)
        return signUpResponse
    }


    public fun getSignInResponse(email:String, password:String) : MutableLiveData<ResponseObject<UserModel>>
    {
        signIn(email, password)
        return signInResponse
    }

}