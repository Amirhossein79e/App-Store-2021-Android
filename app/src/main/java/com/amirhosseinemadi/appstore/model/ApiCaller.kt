package com.amirhosseinemadi.appstore.model

import com.amirhosseinemadi.appstore.model.entity.*
import dagger.android.AndroidInjection.inject
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.Exception
import javax.inject.Inject
import kotlin.jvm.Throws

class ApiCaller @Inject constructor(retrofit: Retrofit) {

    companion object
    {
        val SLIDER_URL:String = "https://bermoodaco.ir/picture/slider/"
        val CATEGORY_URL:String = "https://bermoodaco.ir/picture/"
        val ICON_URL:String = "https://bermoodaco.ir/picture/icon/"
        val IMAGE_URL:String = "https://bermoodaco.ir/picture/image/"
    }

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


    fun initToken(uid:String, token:String, observer:SingleObserver<ResponseObject<String>>)
    {
        val jsonObject:JSONObject = JSONObject()
        jsonObject.put("uid",uid)
        jsonObject.put("token",token)

        service.initToken(INIT_TOKEN,jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    fun syncToken(uid:String, token:String, observer:SingleObserver<ResponseObject<String>>)
    {
        val jsonObject:JSONObject = JSONObject()
        jsonObject.put("uid",uid)
        jsonObject.put("token",token)

        service.syncToken(SYNC_TOKEN,jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    fun signUpUser(email:String, password:String, username:String, token:String, observer:SingleObserver<ResponseObject<UserModel>>)
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


    fun signInUser(email:String, password:String, observer:SingleObserver<ResponseObject<UserModel>>)
    {
        val jsonObject:JSONObject = JSONObject()
        jsonObject.put("mail",email)
        jsonObject.put("password",password)

        service.signInUser(SIGNIN_USER,jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    fun syncUser(access:String, token:String, observer:SingleObserver<ResponseObject<String>>)
    {
        val jsonObject:JSONObject = JSONObject()
        jsonObject.put("access",access)
        jsonObject.put("token",token)

        service.syncUser(SYNC_USER,jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    fun validateUser(access:String, observer:SingleObserver<ResponseObject<String>>)
    {
        val jsonObject:JSONObject = JSONObject()
        jsonObject.put("access",access)

        service.validateUser(VALIDATE_USER,jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    fun getHome(observer:SingleObserver<ResponseObject<HomeModel>>)
    {
        service.getHome(GET_HOME,"")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    fun getCategories(observer:SingleObserver<ResponseObject<List<CategoryModel>>>)
    {
        service.getCategories(GET_CATEGORIES,"")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    fun getApps(offset:Int, observer:SingleObserver<ResponseObject<List<AppModel>>>)
    {
        val jsonObject:JSONObject = JSONObject()
        jsonObject.put("offset",offset)

        service.getApps(GET_APPS,jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    fun getAppsByCategory(offset:Int, category:String, observer:SingleObserver<ResponseObject<List<AppModel>>>)
    {
        val jsonObject:JSONObject = JSONObject()
        jsonObject.put("offset",offset)
        jsonObject.put("category",category)

        service.getAppsByCategory(GET_APPS_BY_CATEGORY,jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    fun getApp(packageName:String, observer:SingleObserver<ResponseObject<AppModel>>)
    {
        val jsonObject:JSONObject = JSONObject()
        jsonObject.put("packageName",packageName)

        service.getApp(GET_APP,jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    fun getTitlesSearch(query:String, observer:SingleObserver<ResponseObject<List<String>>>)
    {
        val jsonObject:JSONObject = JSONObject()
        jsonObject.put("query",query)

        service.getTitlesSearch(GET_TITLES_SEARCH,jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    fun getAppsSearch(offset: Int, query:String, observer:SingleObserver<ResponseObject<List<AppModel>>>)
    {
        val jsonObject: JSONObject = JSONObject()
        jsonObject.put("offset",offset)
        jsonObject.put("query", query)

        service.getAppsSearch(GET_APPS_SEARCH, jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    fun getUpdates(offset:Int, packages:List<JSONObject>, observer:SingleObserver<ResponseObject<List<AppModel>>>)
    {
        val jsonObject:JSONObject = JSONObject()

        jsonObject.put("offset",offset)
        val jsonArray:JSONArray = JSONArray()
        packages.forEach()
        {
            jsonArray.put(it)
        }
        jsonObject.put("packages",jsonArray)

        service.getUpdates(GET_UPDATES, jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    fun getComments(access:String?, packageName:String, offset:Int, observer:SingleObserver<ResponseObject<List<CommentModel>>>)
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


    fun getRating(packageName:String, observer:SingleObserver<ResponseObject<Float>>)
    {
        val jsonObject: JSONObject = JSONObject()
        jsonObject.put("packageName", packageName)

        service.getRating(GET_RATING, jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    fun submitComment(access: String, detail:String, rate:Float, packageName:String, observer:SingleObserver<ResponseObject<String>>)
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


    fun deleteComment(access: String, packageName:String, observer:SingleObserver<ResponseObject<String>>)
    {
        val jsonObject: JSONObject = JSONObject()
        jsonObject.put("access", access)
        jsonObject.put("packageName", packageName)

        service.deleteComment(DELETE_COMMENT, jsonObject.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)
    }


    @Throws(Exception::class)
    fun download(packageName:String) : Response<ResponseBody>
    {
        val jsonObject: JSONObject = JSONObject()
        jsonObject.put("packageName", packageName)

        val resp:Response<ResponseBody> = service.download(DOWNLOAD, jsonObject.toString()).execute()

        return resp
    }


}