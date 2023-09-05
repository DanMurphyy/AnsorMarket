package com.hfad.ansormarket.models

import android.os.Parcel
import android.os.Parcelable

data class Item(
    val imageItem: String = "",
    val nameItem: String = "",
    val weight: String = "",
    val price: Int = 0,
    var category: String,
    val createdBy: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    var documentId: String = "",
    var quantity: Int = 1
) : Parcelable {
    constructor() : this("", "", "", 0, "", "", ArrayList(), "", 1)

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.readInt(),
    )

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(imageItem)
        writeString(nameItem)
        writeString(weight)
        writeInt(price)
        writeString(category)
        writeString(createdBy)
        writeStringList(assignedTo)
        writeString(documentId)
        writeInt(quantity)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }
}
