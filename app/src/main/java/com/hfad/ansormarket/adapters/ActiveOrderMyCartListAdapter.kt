package com.hfad.ansormarket.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hfad.ansormarket.R
import com.hfad.ansormarket.databinding.ActiveOrderMycartListBinding
import com.hfad.ansormarket.models.MyCart

class ActiveOrderMyCartListAdapter :
    RecyclerView.Adapter<ActiveOrderMyCartListAdapter.MyViewHolder>() {

    private var activeOrderProductList: List<MyCart> = emptyList()

    class MyViewHolder(internal val binding: ActiveOrderMycartListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ActiveOrderMycartListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = activeOrderProductList[position] // Access items directly from itemList
        val binding = holder.binding

        Glide
            .with(binding.root.context)
            .load(currentItem.itemProd.imageItem)
            .centerInside()
            .placeholder(R.drawable.ic_images)
            .into(binding.activeInfoCartItemImage)

        binding.activeInfoCartItemName.text = currentItem.itemProd.nameItem
        binding.activeInfoCartItemPrice.text = currentItem.itemProd.price.toString()
        binding.activeInfoCartItemWeight.text = currentItem.itemProd.weight
        binding.activeInfoCartItemQuantity.text = currentItem.quantity.toString()


    }

    fun setActMyCartData(activeOrderProduct: List<MyCart>) {
//        val myCartDiffUtil = MyCartDiffUtil(orderProductList, orderProduct)
//        val myCartDiffResult = DiffUtil.calculateDiff(myCartDiffUtil)
        this.activeOrderProductList = activeOrderProduct
//        myCartDiffResult.dispatchUpdatesTo(this)
//        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return activeOrderProductList.size
    }
}