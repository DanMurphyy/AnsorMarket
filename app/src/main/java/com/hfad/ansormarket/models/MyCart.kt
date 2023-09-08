package com.hfad.ansormarket.models

import android.os.Parcel
import android.os.Parcelable

data class MyCart(
    var itemProd: Item,
    var quantity: Int = 1,
    var createdBy: String = "",
    var documentId: String = "",
    var amount: Int = 0,
) : Parcelable {
    constructor() : this(Item(), 1, "","",1)
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Item::class.java.classLoader) ?: Item(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
    )

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(itemProd, flags)
        writeInt(quantity)
        writeString(createdBy)
        writeString(documentId)
        writeInt(amount)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<MyCart> {
        override fun createFromParcel(parcel: Parcel): MyCart {
            return MyCart(parcel)
        }

        override fun newArray(size: Int): Array<MyCart?> {
            return arrayOfNulls(size)
        }
    }
}