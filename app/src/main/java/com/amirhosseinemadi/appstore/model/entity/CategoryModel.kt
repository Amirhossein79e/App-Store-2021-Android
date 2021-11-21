package com.amirhosseinemadi.appstore.model.entity

import com.google.gson.annotations.SerializedName

class CategoryModel {

    @SerializedName("id")
    var id:Int? = null

    @SerializedName("category_fa")
    var categoryFa:String = ""

    @SerializedName("category_en")
    var categoryEn:String = ""

    @SerializedName("icon")
    var icon:String = ""

}