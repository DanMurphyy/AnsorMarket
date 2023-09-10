package com.hfad.ansormarket.models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

data class Order(
    var orderStatus: Int = 0,
    var orderUser: User,
    var orderProducts: List<MyCart>,
    var totalAmount: Int = 0,
    var orderedId: String = "",
    var orderNumber: String = "",
    var date: String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(Date())

) : Parcelable {
    init {
        // Generate a random order number between 0 and 100,000 and add totalAmount
        val randomPart = ((0..100000).random().toString() + orderUser.name)
        // Add the totalAmount to the random number to create the orderNumber
        orderNumber = randomPart
        Log.d("OrderNumberLog", "Generated orderNumber: $orderNumber ")

    }

    constructor() : this(
        0,
        User(),
        emptyList(),
        0,
        "",
        "",
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(Date())
    )


    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readParcelable(User::class.java.classLoader) ?: User(),
        listOf(parcel.readParcelable(MyCart::class.java.classLoader) ?: MyCart()),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString() ?: SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(Date()),

        )

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(orderStatus)
        writeParcelable(orderUser, flags)
        writeTypedList(orderProducts) // Change here
        writeInt(totalAmount)
        writeString(orderedId)
        writeString(orderNumber)
        writeString(date)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Order> {
        override fun createFromParcel(parcel: Parcel): Order {
            return Order(parcel)
        }

        override fun newArray(size: Int): Array<Order?> {
            return arrayOfNulls(size)
        }
    }
}
