package com.amirhosseinemadi.appstore.model.entity

import com.google.gson.annotations.SerializedName

class UserModel {

    @SerializedName("username")
    var userName: String? = ""

    @SerializedName("access")
    var access:String? = ""

}