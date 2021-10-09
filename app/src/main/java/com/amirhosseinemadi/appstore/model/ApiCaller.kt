package com.amirhosseinemadi.appstore.model

import android.provider.Telephony
import com.amirhosseinemadi.appstore.common.Application
import com.amirhosseinemadi.appstore.model.entity.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class ApiCaller @Inject constructor(private val retrofit: Retrofit) {

    private val INIT_TOKEN:Int = 100
    private val SYNC_TOKEN:Int = 101
    private val SIGNUP_USER:Int = 102
    private val SIGNIN_USER:Int = 103
    private val SYNC_USER:Int = 104
    private val VALIDATE_USER:Int = 105

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

    private var service: Service

    init
    {
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


    public fun signUpUser(email:String, password:String, username:String, token:String, observer:SingleObserver<ResponseObject<UserModel>>)
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


    public fun validateUser(access:String, observer:SingleObserver<ResponseObject<String>>)
    {
        val jsonObject:JSONObject = JSONObject()
        jsonObject.put("access",access)

        service.validateUser(VALIDATE_USER,jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    public fun getHome(observer:SingleObserver<ResponseObject<HomeModel>>)
    {
        service.getHome(GET_HOME,"")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    public fun getCategories(observer:SingleObserver<ResponseObject<List<CategoryModel>>>)
    {
        service.getCategories(GET_CATEGORIES,"")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    public fun getApps(offset:Int, observer:SingleObserver<ResponseObject<List<AppModel>>>)
    {
        val jsonObject:JSONObject = JSONObject()
        jsonObject.put("offset",offset)

        service.getApps(GET_APPS,jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    public fun getAppsByCategory(offset:Int, category:String, observer:SingleObserver<ResponseObject<List<AppModel>>>)
    {
        val jsonObject:JSONObject = JSONObject()
        jsonObject.put("offset",offset)
        jsonObject.put("category",category)

        service.getAppsByCategory(GET_APPS_BY_CATEGORY,jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    public fun getApp(packageName:String, observer:SingleObserver<ResponseObject<AppModel>>)
    {
        val jsonObject:JSONObject = JSONObject()
        jsonObject.put("packageName",packageName)

        service.getApp(GET_APP,jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    public fun getTitlesSearch(query:String, observer:SingleObserver<ResponseObject<List<String>>>)
    {
        val jsonObject:JSONObject = JSONObject()
        jsonObject.put("query",query)

        service.getTitlesSearch(GET_TITLES_SEARCH,jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    public fun getAppsSearch(query:String, observer:SingleObserver<ResponseObject<List<AppModel>>>)
    {
        val jsonObject: JSONObject = JSONObject()
        jsonObject.put("query", query)

        service.getAppsSearch(GET_APPS_SEARCH, jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    public fun getUpdates(packages:List<JSONObject>, observer:SingleObserver<ResponseObject<List<AppModel>>>)
    {
        val jsonArray:JSONArray = JSONArray()
        packages.forEach()
        {
            jsonArray.put(it)
        }

        service.getUpdates(GET_UPDATES, jsonArray.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    public fun getComments(access:String, packageName:String, offset:Int, observer:SingleObserver<ResponseObject<List<CommentModel>>>)
    {
        val jsonObject: JSONObject = JSONObject()
        jsonObject.put("access", access)
        jsonObject.put("packageName", packageName)
        jsonObject.put("offset", offset)

        service.getComments(GET_COMMENTS, jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    public fun getComments(packageName:String, observer:SingleObserver<ResponseObject<String>>)
    {
        val jsonObject: JSONObject = JSONObject()
        jsonObject.put("packageName", packageName)

        service.getRating(GET_RATING, jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    public fun submitComment(access: String, detail:String, rate:Float, packageName:String, observer:SingleObserver<ResponseObject<String>>)
    {
        val jsonObject: JSONObject = JSONObject()
        jsonObject.put("access", access)
        jsonObject.put("detail", detail)
        jsonObject.put("rate", rate)
        jsonObject.put("packageName", packageName)

        service.submitComment(SUBMIT_COMMENT, jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    public fun deleteComment(access: String, packageName:String, observer:SingleObserver<ResponseObject<String>>)
    {
        val jsonObject: JSONObject = JSONObject()
        jsonObject.put("access", access)
        jsonObject.put("packageName", packageName)

        service.deleteComment(DELETE_COMMENT, jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    public fun download(packageName:String, observer:SingleObserver<Response<ResponseBody>>)
    {
        val jsonObject: JSONObject = JSONObject()
        jsonObject.put("packageName", packageName)

        service.download(DOWNLOAD, jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


}