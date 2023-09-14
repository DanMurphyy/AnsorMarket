package com.hfad.ansormarket.models

import android.os.Parcel
import android.os.Parcelable

data class ContactUs(
    val AppMobile: String = "",
    var MarketMobile: String = "",
    var DeliveryMobile: String = "",
    var WorkingHoursFrom: Int = 0,
    var WorkingHoursTill: Int = 0

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
    ) {
    }

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(AppMobile)
        writeString(MarketMobile)
        writeString(DeliveryMobile)
        writeInt(WorkingHoursFrom)
        writeInt(WorkingHoursTill)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<ContactUs> {
        override fun createFromParcel(parcel: Parcel): ContactUs {
            return ContactUs(parcel)
        }

        override fun newArray(size: Int): Array<ContactUs?> {
            return arrayOfNulls(size)
        }
    }

}