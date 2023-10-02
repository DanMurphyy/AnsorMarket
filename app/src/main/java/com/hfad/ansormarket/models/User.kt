package com.hfad.ansormarket.models

import android.os.Parcel
import android.os.Parcelable

data class User(
    val id: String = "",
    var name: String = "",
    var mobile: String = "",
    var address: String = "",
    var image: String = "",
    val fcmToken: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeString(mobile)
        writeString(address)
        writeString(image)
        writeString(fcmToken)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}
