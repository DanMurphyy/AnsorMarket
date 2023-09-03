package com.hfad.ansormarket.models

import androidx.recyclerview.widget.DiffUtil

class ItemDiffUtil(private val oldList: List<Item>, private val newList: List<Item>) :
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
        return oldList[oldItemPosition].imageItem == newList[newItemPosition].imageItem
                &&
                oldList[oldItemPosition].nameItem == newList[newItemPosition].nameItem
                &&
                oldList[oldItemPosition].weight == newList[newItemPosition].weight
                &&
                oldList[oldItemPosition].category == newList[newItemPosition].category
                &&
                oldList[oldItemPosition].price == newList[newItemPosition].price
    }
}