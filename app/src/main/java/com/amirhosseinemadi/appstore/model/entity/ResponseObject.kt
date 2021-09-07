package com.amirhosseinemadi.appstore.model.entity

import com.google.gson.annotations.SerializedName

open class ResponseObject<T> {

    @SerializedName("responseCode")
    var responseCode:Int? = null

    @SerializedName("message")
    var message:String? = null

    @SerializedName("data")
    var data:T? = null

}