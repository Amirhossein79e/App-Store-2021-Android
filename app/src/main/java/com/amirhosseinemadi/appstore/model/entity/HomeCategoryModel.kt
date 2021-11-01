package com.amirhosseinemadi.appstore.model.entity

import com.google.gson.annotations.SerializedName

class HomeCategoryModel {

    @SerializedName("category")
    var category:String? = ""

    @SerializedName("categoryName")
    var categoryName:String? = ""

    @SerializedName("categoryNameEn")
    var categoryNameEn:String? = ""

}