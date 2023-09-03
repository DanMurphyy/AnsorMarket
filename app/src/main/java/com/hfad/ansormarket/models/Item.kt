package com.hfad.ansormarket.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class Item(
    val imageItem: String = "",
    val nameItem: String = "",
    val weight: String = "",
    val price: Int = 0,
    @get:PropertyName("category")
    @set:PropertyName("category")
    var category: ItemType,
    val createdBy: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    var documentId: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        ItemType.valueOf(parcel.readString()!!),
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(imageItem)
        writeString(nameItem)
        writeString(weight)
        writeInt(price)
        writeString(category.name)
        writeString(createdBy)
        writeStringList(assignedTo)
        writeString(documentId)
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
    @Exclude
    fun getCategoryName(): String {
        return category.name
    }

    @Exclude
    fun setCategoryName(categoryName: String) {
        category = ItemType.valueOf(categoryName)
    }
}
