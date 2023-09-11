package com.hfad.ansormarket.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hfad.ansormarket.R
import com.hfad.ansormarket.databinding.CompletedOrderMycartListBinding
import com.hfad.ansormarket.models.MyCart

class CompletedOrderMyCartListAdapter :
    RecyclerView.Adapter<CompletedOrderMyCartListAdapter.MyViewHolder>() {

    private var orderProductList: List<MyCart> = emptyList()

    class MyViewHolder(internal val binding: CompletedOrderMycartListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            CompletedOrderMycartListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
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
            .into(binding.completedInfoCartItemImage)

        binding.completedInfoCartItemName.text = currentItem.itemProd.nameItem
        binding.completedInfoCartItemPrice.text = currentItem.itemProd.price.toString()
        binding.completedInfoCartItemWeight.text = currentItem.itemProd.weight
        binding.completedInfoCartItemQuantity.text = currentItem.quantity.toString()


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