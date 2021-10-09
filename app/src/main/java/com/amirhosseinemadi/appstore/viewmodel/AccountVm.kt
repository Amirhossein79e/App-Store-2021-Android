package com.amirhosseinemadi.appstore.viewmodel

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amirhosseinemadi.appstore.R
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
    val validateResponse:MutableLiveData<ResponseObject<String>>

    var emailStr:String = ""
    var passwordStr:String = ""
    var usernameStr:String = ""
    var passwordReStr:String = ""
    val visib:MutableLiveData<Int> = MutableLiveData<Int>().also{ View.GONE }
    val signText:MutableLiveData<String> = MutableLiveData<String>().also { it.value = Application.component.context().getString(R.string.sign_in_alt) }
    private var signIn:Boolean

    init
    {
        apiCaller = Application.component.apiCaller()
        error = MutableLiveData()
        signUpResponse = MutableLiveData()
        signInResponse = MutableLiveData()
        validateResponse = MutableLiveData()
        signIn = true
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


    public fun validateUser(access:String)
    {
        apiCaller.validateUser(access,object : SingleObserver<ResponseObject<String>>
        {
            override fun onSubscribe(d: Disposable?) {

            }

            override fun onSuccess(t: ResponseObject<String>?) {
                validateResponse.value = t
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


    public fun getValidateResponse(access: String) : MutableLiveData<ResponseObject<String>>
    {
        validateUser(access)
        return validateResponse
    }

}