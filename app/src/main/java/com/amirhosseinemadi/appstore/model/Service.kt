package com.amirhosseinemadi.appstore.model

import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Service {

    @POST("exe.php")
    @FormUrlEncoded
    fun test(@Field("requestCode") requestCode:Int,@Field("data") data:String) : Call<ResponseBody>

}