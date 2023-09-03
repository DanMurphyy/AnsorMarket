package com.hfad.ansormarket.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hfad.ansormarket.R
import com.hfad.ansormarket.databinding.ListItemLayoutBinding
import com.hfad.ansormarket.models.Item
import com.hfad.ansormarket.models.ItemDiffUtil

class ItemListAdapter : RecyclerView.Adapter<ItemListAdapter.MyViewHolder>() {
    private var itemList: List<Item> = emptyList() // Initialize itemList with an empty list

    class MyViewHolder(internal val binding: ListItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = itemList[position] // Access items directly from itemList
        val binding = holder.binding
        Glide
            .with(binding.root.context)
            .load(currentItem.imageItem)
            .centerCrop()
            .placeholder(R.drawable.ic_images)
            .into(binding.itemListImage)
        binding.itemListPrice.text = currentItem.price.toString()
        binding.itemListName.text = currentItem.nameItem
        binding.itemListWeight.text = currentItem.weight
    }

    fun setItems(items: List<Item>) {
        val itemDiffUtil = ItemDiffUtil(itemList, items)
        val itemDiffResult = DiffUtil.calculateDiff(itemDiffUtil)
        this.itemList = items
        itemDiffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}