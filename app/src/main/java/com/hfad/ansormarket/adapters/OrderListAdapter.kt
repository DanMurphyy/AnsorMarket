package com.hfad.ansormarket.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hfad.ansormarket.R
import com.hfad.ansormarket.databinding.MyOrderLayoutBinding
import com.hfad.ansormarket.models.Order
import com.hfad.ansormarket.models.utils.MyOrderDiffUtil

class OrderListAdapter : RecyclerView.Adapter<OrderListAdapter.MyViewHolder>() {

    private var myOrderList: List<Order> = emptyList()

    class MyViewHolder(internal val binding: MyOrderLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            MyOrderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = myOrderList[position] // Access items directly from itemList
        val binding = holder.binding
        binding.orderNumber.text = currentItem.orderNumber
        binding.orderAmount.text = currentItem.totalAmount.toString()
        binding.orderDate.text = currentItem.date
        binding.orderAddress.text = currentItem.orderUser.address
        binding.orderPhone.text = currentItem.orderUser.mobile.toString()

        when (currentItem.orderStatus) {
            0 -> statusPending(binding)
            1 -> statusAccepted(binding)
            2 -> statusSend(binding)
            3 -> statusDelivered(binding)
            4 -> statusCancelled(binding)
        }

        binding.infoArrowDown.setOnClickListener {
            makeVisRecyclerView(binding)
        }

        binding.infoArrowUp.setOnClickListener {
            makeGoneRecyclerView(binding)
        }

        val innerAdapter: OrderMyCartListAdapter by lazy { OrderMyCartListAdapter() }
        innerAdapter.setMyCartData(currentItem.orderProducts)

        binding.recyclerViewOrderMycart.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = innerAdapter
        }
    }

    private fun statusPending(binding: MyOrderLayoutBinding) {
        binding.progressBar.visibility = View.VISIBLE
        binding.orderStatusImage.visibility = View.GONE
        binding.orderStatusText.text = binding.root.context.getString(R.string.order_in_process)

    }

    private fun statusAccepted(binding: MyOrderLayoutBinding) {
        binding.progressBar.visibility = View.GONE
        binding.orderStatusImage.visibility = View.VISIBLE
        binding.orderStatusText.text = binding.root.context.getString(R.string.order_getting_ready)

        val gifImageView = binding.orderStatusImage
        Glide.with(binding.root.context)
            .asGif()
            .load(R.drawable.grocery) // Assuming "fire.gif" is the name of your animated GIF file
            .into(gifImageView)

    }

    private fun statusSend(binding: MyOrderLayoutBinding) {
        binding.progressBar.visibility = View.GONE
        binding.orderStatusImage.visibility = View.VISIBLE
        binding.orderStatusText.text = binding.root.context.getString(R.string.order_delivering)


        val gifImageView = binding.orderStatusImage
        Glide.with(binding.root.context)
            .asGif()
            .load(R.drawable.delivering) // Assuming "fire.gif" is the name of your animated GIF file
            .into(gifImageView)

    }

    private fun statusDelivered(binding: MyOrderLayoutBinding) {
        binding.progressBar.visibility = View.GONE
        binding.orderStatusImage.visibility = View.VISIBLE
        binding.orderStatusImage.setImageResource(R.drawable.ic_delivered)
        binding.orderStatusText.text = binding.root.context.getString(R.string.order_delivered)


    }

    private fun statusCancelled(binding: MyOrderLayoutBinding) {
        binding.progressBar.visibility = View.GONE
        binding.orderStatusImage.visibility = View.VISIBLE
        binding.orderStatusImage.setImageResource(R.drawable.ic_cancelled)
        binding.orderStatusText.text = binding.root.context.getString(R.string.order_cancelled)


    }


    private fun makeGoneRecyclerView(binding: MyOrderLayoutBinding) {
        binding.orderDetails.visibility = View.GONE
        binding.infoArrowUp.visibility = View.GONE
        binding.infoArrowDown.visibility = View.VISIBLE
    }

    private fun makeVisRecyclerView(binding: MyOrderLayoutBinding) {
        binding.orderDetails.visibility = View.VISIBLE
        binding.infoArrowUp.visibility = View.VISIBLE
        binding.infoArrowDown.visibility = View.GONE
    }

    fun setMyCartData(myOrder: List<Order>) {
        val myOrderDiffUtil = MyOrderDiffUtil(myOrderList, myOrder)
        val myOrderDiffResult = DiffUtil.calculateDiff(myOrderDiffUtil)
        this.myOrderList = myOrder
        myOrderDiffResult.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return myOrderList.size
    }
}

