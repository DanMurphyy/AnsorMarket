package com.hfad.ansormarket.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hfad.ansormarket.R
import com.hfad.ansormarket.databinding.OrderMycartListBinding
import com.hfad.ansormarket.models.MyCart
import com.hfad.ansormarket.models.utils.MyCartDiffUtil

class OrderMyCartListAdapter : RecyclerView.Adapter<OrderMyCartListAdapter.MyViewHolder>() {

    private var orderProductList: List<MyCart> = emptyList()

    class MyViewHolder(internal val binding: OrderMycartListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            OrderMycartListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = orderProductList[position] // Access items directly from itemList
        val binding = holder.binding

        Glide
            .with(binding.root.context)
            .load(currentItem.itemProd.imageItem)
            .centerInside()
            .placeholder(R.drawable.ic_images)
            .into(binding.infoCartItemImage)

        binding.infoCartItemName.text = currentItem.itemProd.nameItem
        binding.infoCartItemPrice.text = currentItem.itemProd.price.toString()
        binding.infoCartItemWeight.text = currentItem.itemProd.weight
        binding.infoCartItemQuantity.text = currentItem.quantity.toString()


    }

    fun setMyCartData(orderProduct: List<MyCart>) {
//        val myCartDiffUtil = MyCartDiffUtil(orderProductList, orderProduct)
//        val myCartDiffResult = DiffUtil.calculateDiff(myCartDiffUtil)
        this.orderProductList = orderProduct
//        myCartDiffResult.dispatchUpdatesTo(this)
//        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return orderProductList.size
    }
}