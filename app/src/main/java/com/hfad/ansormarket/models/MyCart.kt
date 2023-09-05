package com.hfad.ansormarket.models

import android.os.Parcel
import android.os.Parcelable

data class MyCart(
    val itemList: ArrayList<Item> = ArrayList(),
    val totalAmount: Int = 0,
    val createdBy: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    var documentId: String = "",
) : Parcelable {
    constructor() : this(ArrayList(), 0, "", ArrayList(), "")


    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(Item.CREATOR)!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
    )

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeTypedList(itemList)
        writeInt(totalAmount)
        writeString(createdBy)
        writeStringList(assignedTo)
        writeString(documentId)
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