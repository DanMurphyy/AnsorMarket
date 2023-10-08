package com.hfad.ansormarket.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hfad.ansormarket.R
import com.hfad.ansormarket.databinding.CartItemBinding
import com.hfad.ansormarket.models.MyCart
import com.hfad.ansormarket.models.utils.MyCartDiffUtil

class MyCartListAdapter : RecyclerView.Adapter<MyCartListAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListenerCart? = null
    private var myCartList: List<MyCart> = emptyList()

    class MyViewHolder(internal val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = myCartList[position] // Access items directly from itemList
        val binding = holder.binding
        var newQuantityMain = currentItem.quantity

        Glide
            .with(binding.root.context)
            .load(currentItem.itemProd.imageItem)
            .centerInside()
            .placeholder(R.drawable.ic_images)
            .into(binding.infoListImage)

        binding.infoListName.text = currentItem.itemProd.nameItem
        binding.infoListPrice.text = currentItem.itemProd.price.toString()
        binding.infoListWeight.text = currentItem.itemProd.weight
        binding.quantityInfoCart.text = newQuantityMain.toString()

        binding.quantityPlus.setOnClickListener {
            if (onClickListener != null) {
                newQuantityMain++
                val updateAmount = currentItem.itemProd.price * newQuantityMain
                // Update UI immediately
                binding.quantityInfoCart.text = newQuantityMain.toString()
                // Call Firebase update
                onClickListener!!.onUpdateCartQuantityClick(
                    currentItem.documentId,
                    newQuantityMain,
                    updateAmount
                )
            }
        }

        binding.quantityMinus.setOnClickListener {
            if (onClickListener != null && newQuantityMain > 1) {
                newQuantityMain--
                val updateAmount = currentItem.itemProd.price * newQuantityMain
                // Update UI immediately
                binding.quantityInfoCart.text = newQuantityMain.toString()
                // Call Firebase update
                onClickListener!!.onUpdateCartQuantityClick(
                    currentItem.documentId,
                    newQuantityMain,
                    updateAmount
                )
            } else {
                onClickListener!!.onDeleteCartClick(currentItem.documentId)
                Toast.makeText(
                    holder.itemView.context,
                    holder.itemView.context.getString(R.string.cart_removed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    fun calculateTotalAmount(): Int {
        var totalAmount = 0
        for (item in myCartList) {
            totalAmount += item.amount
        }
        return totalAmount
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
        return myCartList.size
    }

    interface OnClickListenerCart {
        fun onDeleteCartClick(currentItem: String)
        fun onUpdateCartQuantityClick(currentItem: String, newQuantity: Int, newAmount: Int)
    }

    fun setOnClickListener(onClickListener: OnClickListenerCart) {
        this.onClickListener = onClickListener
    }

}
