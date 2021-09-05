package com.amirhosseinemadi.appstore.model

import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.model.entity.ResponseObject
import com.amirhosseinemadi.appstore.model.entity.UserModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.Retrofit

class ApiCaller {

    private val INIT_TOKEN:Int = 100
    private val SYNC_TOKEN:Int = 101
    private val SIGNUP_USER:Int = 102
    private val SIGNIN_USER:Int = 103
    private val SYNC_USER:Int = 104

    private val GET_HOME:Int = 200
    private val GET_CATEGORIES:Int = 201
    private val GET_APPS:Int = 202
    private val GET_APPS_BY_CATEGORY:Int = 203
    private val GET_APP:Int = 204
    private val GET_TITLES_SEARCH:Int = 205
    private val GET_APPS_SEARCH:Int = 206
    private val GET_UPDATES:Int = 207

    private val GET_COMMENTS:Int = 300
    private val GET_RATING:Int = 301
    private val SUBMIT_COMMENT:Int = 302
    private val DELETE_COMMENT:Int = 303

    private val DOWNLOAD:Int = 500

    private lateinit var retrofit:Retrofit
    private lateinit var service: Service

    init
    {
        retrofit = Application.component.retrofit()
        service = retrofit.create(Service::class.java)
    }


    public fun initToken(uid:String, token:String, observer:SingleObserver<ResponseObject<String>>)
    {
        val jsonObject:JSONObject = JSONObject()
        jsonObject.put("uid",uid)
        jsonObject.put("token",token)

        service.initToken(INIT_TOKEN,jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    public fun syncToken(uid:String, token:String, observer:SingleObserver<ResponseObject<String>>)
    {
        val jsonObject:JSONObject = JSONObject()
        jsonObject.put("uid",uid)
        jsonObject.put("token",token)

        service.syncToken(SYNC_TOKEN,jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    public fun singUpUser(email:String, password:String, username:String, token:String, observer:SingleObserver<ResponseObject<UserModel>>)
    {
        val jsonObject:JSONObject = JSONObject()
        jsonObject.put("mail",email)
        jsonObject.put("password",password)
        jsonObject.put("username",username)
        jsonObject.put("token",token)

        service.singUpUser(SIGNUP_USER,jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    public fun signInUser(email:String, password:String, observer:SingleObserver<ResponseObject<UserModel>>)
    {
        val jsonObject:JSONObject = JSONObject()
        jsonObject.put("mail",email)
        jsonObject.put("password",password)

        service.signInUser(SIGNIN_USER,jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    public fun syncUser(access:String, token:String, observer:SingleObserver<ResponseObject<String>>)
    {
        val jsonObject:JSONObject = JSONObject()
        jsonObject.put("access",access)
        jsonObject.put("token",token)

        service.syncUser(SYNC_USER,jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


}