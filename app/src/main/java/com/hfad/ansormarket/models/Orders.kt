package com.hfad.ansormarket.models

import android.os.Parcel
import android.os.Parcelable

data class Orders(
    var orderId: Int = 1,
    var orderStatus: Boolean = false,
    var orderUser: User,
    var orderProducts: MyCart,
    var totalAmount: Int = 0,
    var orderedBy: String = "",
) : Parcelable {
    constructor() : this(1, false, User(), MyCart(), 1)

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readParcelable(User::class.java.classLoader) ?: User(),
        parcel.readParcelable(MyCart::class.java.classLoader) ?: MyCart(),
        parcel.readInt(),
        parcel.readString()!!
    )

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(orderId)
        writeByte(if (orderStatus) 1 else 0)
        writeParcelable(orderUser, flags)
        writeParcelable(orderProducts, flags)
        writeInt(totalAmount)
        writeString(orderedBy)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Orders> {
        override fun createFromParcel(parcel: Parcel): Orders {
            return Orders(parcel)
        }

        override fun newArray(size: Int): Array<Orders?> {
            return arrayOfNulls(size)
        }
    }
}
