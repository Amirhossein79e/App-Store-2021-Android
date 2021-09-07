package com.amirhosseinemadi.appstore.model

import com.amirhosseinemadi.appstore.model.entity.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Service {

    @POST("exe.php")
    @FormUrlEncoded
    fun initToken(@Field("requestCode") requestCode:Int,@Field("data") data:String) : Single<ResponseObject<String>>


    @POST("exe.php")
    @FormUrlEncoded
    fun syncToken(@Field("requestCode") requestCode:Int,@Field("data") data:String) : Single<ResponseObject<String>>


    @POST("exe.php")
    @FormUrlEncoded
    fun singUpUser(@Field("requestCode") requestCode:Int,@Field("data") data:String) : Single<ResponseObject<UserModel>>


    @POST("exe.php")
    @FormUrlEncoded
    fun signInUser(@Field("requestCode") requestCode:Int,@Field("data") data:String) : Single<ResponseObject<UserModel>>


    @POST("exe.php")
    @FormUrlEncoded
    fun syncUser(@Field("requestCode") requestCode:Int,@Field("data") data:String) : Single<ResponseObject<String>>


    @POST("exe.php")
    @FormUrlEncoded
    fun getHome(@Field("requestCode") requestCode:Int,@Field("data") data:String) : Single<ResponseObject<HomeModel>>


    @POST("exe.php")
    @FormUrlEncoded
    fun getCategories(@Field("requestCode") requestCode:Int,@Field("data") data:String) : Single<ResponseObject<List<CategoryModel>>>


    @POST("exe.php")
    @FormUrlEncoded
    fun getApps(@Field("requestCode") requestCode:Int,@Field("data") data:String) : Single<ResponseObject<List<AppModel>>>


    @POST("exe.php")
    @FormUrlEncoded
    fun getAppsByCategory(@Field("requestCode") requestCode:Int,@Field("data") data:String) : Single<ResponseObject<List<AppModel>>>


    @POST("exe.php")
    @FormUrlEncoded
    fun getApp(@Field("requestCode") requestCode:Int,@Field("data") data:String) : Single<ResponseObject<AppModel>>




}