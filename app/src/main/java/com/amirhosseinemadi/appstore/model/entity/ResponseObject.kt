package com.amirhosseinemadi.appstore.model.entity

import com.google.gson.annotations.SerializedName

open class ResponseObject<T> {

    @SerializedName("responseCode")
    var responseCode:Int = -3

    @SerializedName("data")
    var data:T? = null

}