package com.amirhosseinemadi.appstore.model.entity

import com.google.gson.annotations.SerializedName

class CommentModel {

    @SerializedName("username")
    var username:String = ""

    @SerializedName("detail")
    var detail:String = ""

    @SerializedName("rate")
    var rate:Float? = null

    @SerializedName("package_name")
    var packageName:String = ""

    @SerializedName("isAccess")
    var isAccess:Int? = null

}