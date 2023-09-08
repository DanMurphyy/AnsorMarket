package com.hfad.ansormarket.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hfad.ansormarket.R
import com.hfad.ansormarket.databinding.ListItemLayoutBinding
import com.hfad.ansormarket.models.Item
import com.hfad.ansormarket.models.MyCart
import com.hfad.ansormarket.models.utils.ItemDiffUtil
import com.hfad.ansormarket.models.utils.MyCartDiffUtil

class ItemListAdapter : RecyclerView.Adapter<ItemListAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    private var itemList: List<Item> = emptyList()
    private var myCartList: List<MyCart> = emptyList()

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
        var quantityItemCart = 1
        var isItemInCart = false // Flag to check if the item is in the cart

        for (i in myCartList) {
            if (currentItem.documentId == i.itemProd.documentId) {
                quantityItemCart = i.quantity
                isItemInCart = true
                break
            }
        }

// Update visibility based on whether the item is in the cart
        if (isItemInCart) {
            binding.addToCart.visibility = View.GONE
            binding.iconAdded.visibility = View.VISIBLE
        } else {
            binding.addToCart.visibility = View.VISIBLE
            binding.iconAdded.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, currentItem, quantityItemCart)
            }
        }

        binding.addToCart.setOnClickListener {
            // You can add your toCart logic here
            val quantity = 1 // Change this to the desired quantity
            if (onClickListener != null) {
                onClickListener!!.onAddToCartClick(currentItem, quantity)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Item>) {
        val itemDiffUtil = ItemDiffUtil(itemList, items)
        val itemDiffResult = DiffUtil.calculateDiff(itemDiffUtil)
        this.itemList = items
        itemDiffResult.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setMyCartData(myCart: List<MyCart>) {
        val myCartDiffUtil = MyCartDiffUtil(myCartList, myCart)
        val myCartDiffResult = DiffUtil.calculateDiff(myCartDiffUtil)
        this.myCartList = myCart
        myCartDiffResult.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    interface OnClickListener {
        fun onClick(position: Int, currentItem: Item, quantity: Int)
        fun onAddToCartClick(currentItem: Item, quantity: Int)

    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

}