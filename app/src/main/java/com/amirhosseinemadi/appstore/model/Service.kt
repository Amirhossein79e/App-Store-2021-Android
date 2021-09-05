package com.amirhosseinemadi.appstore.model

import com.amirhosseinemadi.appstore.model.entity.ResponseObject
import com.amirhosseinemadi.appstore.model.entity.UserModel
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

}