package com.hfad.ansormarket.models.utils

import androidx.recyclerview.widget.DiffUtil
import com.hfad.ansormarket.models.MyCart

class MyCartDiffUtil(private val oldList: List<MyCart>, private val newList: List<MyCart>) :
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
        return oldList[oldItemPosition].itemProd == newList[newItemPosition].itemProd
                &&
                oldList[oldItemPosition].quantity == newList[newItemPosition].quantity
                &&
                oldList[oldItemPosition].createdBy == newList[newItemPosition].createdBy
                &&
                oldList[oldItemPosition].documentId == newList[newItemPosition].documentId
    }
}