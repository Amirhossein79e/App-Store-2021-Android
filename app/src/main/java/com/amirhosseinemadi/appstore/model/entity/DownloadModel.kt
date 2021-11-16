package com.amirhosseinemadi.appstore.model.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.MutableLiveData

class DownloadModel() : Parcelable{

    var packageName:String = ""
    var progress:MutableLiveData<Int>? = null
    var isFinish = false

    constructor(parcel: Parcel) : this() {
        packageName = parcel.readString()!!
        isFinish = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(packageName)
        parcel.writeByte(if (isFinish) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DownloadModel> {
        override fun createFromParcel(parcel: Parcel): DownloadModel {
            return DownloadModel(parcel)
        }

        override fun newArray(size: Int): Array<DownloadModel?> {
            return arrayOfNulls(size)
        }
    }

}