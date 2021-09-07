package com.amirhosseinemadi.appstore.model.entity

import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import org.json.JSONObject

class HomeModel {

    @SerializedName("row")
    var rows:MutableList<HomeCategoryModel>? = ArrayList()

    @SerializedName("slider")
    var slider:MutableList<String>? = ArrayList()

}