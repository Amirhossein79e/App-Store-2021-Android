package com.amirhosseinemadi.appstore.model.entity

import com.google.gson.annotations.SerializedName

class AppModel {

    @SerializedName("package_name")
    var packageName:String = ""

    @SerializedName("name_fa")
    var nameFa:String = ""

    @SerializedName("name_en")
    var nameEn:String = ""

    @SerializedName("dev_fa")
    var devFa:String = ""

    @SerializedName("dev_en")
    var devEn:String = ""

    @SerializedName("ver_code")
    var verCode:Long? = null

    @SerializedName("ver_name")
    var verName:String = ""

    @SerializedName("category")
    var category:String = ""

    @SerializedName("category_icon")
    var categoryIcon:String = ""

    @SerializedName("category_en")
    var categoryEn:String = ""

    @SerializedName("category_fa")
    var categoryFa:String = ""

    @SerializedName("tag")
    var tag:String = ""

    @SerializedName("detail")
    var detail:String = ""

    @SerializedName("image_num")
    var imageNum:Int = 0

    @SerializedName("link")
    var link:String? = ""

    @SerializedName("size")
    var size:String = ""

    @SerializedName("rate")
    var rate:Float? = null

}