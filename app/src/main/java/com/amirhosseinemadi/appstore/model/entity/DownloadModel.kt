package com.amirhosseinemadi.appstore.model.entity

import android.os.Parcel
import android.os.Parcelable

class DownloadModel() : Parcelable{

    var packageName:String? = ""
    var progress:Int = -1
    var isCancel = false

    constructor(parcel: Parcel) : this() {
        packageName = parcel.readString()
        progress = parcel.readInt()
        isCancel = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(packageName)
        parcel.writeInt(progress)
        parcel.writeByte(if (isCancel) 1 else 0)
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