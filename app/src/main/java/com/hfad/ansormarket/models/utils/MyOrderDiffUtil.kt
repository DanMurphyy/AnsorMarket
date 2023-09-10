package com.hfad.ansormarket.models.utils

import androidx.recyclerview.widget.DiffUtil
import com.hfad.ansormarket.models.Order

class MyOrderDiffUtil(private val oldList: List<Order>, private val newList: List<Order>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].orderNumber == newList[newItemPosition].orderNumber
                &&
                oldList[oldItemPosition].orderUser == newList[newItemPosition].orderUser
                &&
                oldList[oldItemPosition].orderProducts == newList[newItemPosition].orderProducts
                &&
                oldList[oldItemPosition].orderStatus == newList[newItemPosition].orderStatus

                &&
                oldList[oldItemPosition].orderedId == newList[newItemPosition].orderedId
                &&
                oldList[oldItemPosition].date == newList[newItemPosition].date
    }
}