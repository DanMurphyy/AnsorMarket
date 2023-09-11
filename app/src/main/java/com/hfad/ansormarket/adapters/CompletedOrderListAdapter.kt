package com.hfad.ansormarket.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hfad.ansormarket.R
import com.hfad.ansormarket.databinding.CompletedOrderLayoutBinding
import com.hfad.ansormarket.models.Order
import com.hfad.ansormarket.models.utils.MyOrderDiffUtil

class CompletedOrderListAdapter : RecyclerView.Adapter<CompletedOrderListAdapter.MyViewHolder>() {

    private var completedOrderList: List<Order> = emptyList()

    class MyViewHolder(internal val binding: CompletedOrderLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            CompletedOrderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = completedOrderList[position] // Access items directly from itemList
        val binding = holder.binding
        binding.completedOrderNumber.text = currentItem.orderNumber
        binding.completedOrderAmount.text = currentItem.totalAmount.toString()
        binding.completedOrderDate.text = currentItem.date
        binding.completedOrderAddress.text = currentItem.orderUser.address
        binding.completedOrderPhone.text = currentItem.orderUser.mobile.toString()

        when (currentItem.orderStatus) {
            0 -> statusPending(binding)
            1 -> statusAccepted(binding)
            2 -> statusSend(binding)
            3 -> statusDelivered(binding)
            4 -> statusCancelled(binding)
        }

        binding.completedInfoArrowDown.setOnClickListener {
            makeVisRecyclerView(binding)
        }

        binding.completedInfoArrowUp.setOnClickListener {
            makeGoneRecyclerView(binding)
        }

        val innerAdapter: OrderMyCartListAdapter by lazy { OrderMyCartListAdapter() }
        innerAdapter.setMyCartData(currentItem.orderProducts)

        binding.recyclerViewCompletedOrderMycart.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = innerAdapter
        }
    }

    private fun statusPending(binding: CompletedOrderLayoutBinding) {
        binding.completedProgressBar.visibility = View.VISIBLE
        binding.completedOrderStatusImage.visibility = View.GONE
        binding.completedOrderStatusText.text =
            binding.root.context.getString(R.string.order_in_process)


    }

    private fun statusAccepted(binding: CompletedOrderLayoutBinding) {
        binding.completedProgressBar.visibility = View.GONE
        binding.completedOrderStatusImage.visibility = View.VISIBLE
        binding.completedOrderStatusText.text =
            binding.root.context.getString(R.string.order_getting_ready)
        val gifImageView = binding.completedOrderStatusImage
        Glide.with(binding.root.context)
            .asGif()
            .load(R.drawable.grocery) // Assuming "fire.gif" is the name of your animated GIF file
            .into(gifImageView)

    }

    private fun statusSend(binding: CompletedOrderLayoutBinding) {
        binding.completedProgressBar.visibility = View.GONE
        binding.completedOrderStatusImage.visibility = View.VISIBLE
        binding.completedOrderStatusText.text =
            binding.root.context.getString(R.string.order_delivering)
        val gifImageView = binding.completedOrderStatusImage
        Glide.with(binding.root.context)
            .asGif()
            .load(R.drawable.delivering) // Assuming "fire.gif" is the name of your animated GIF file
            .into(gifImageView)

    }

    private fun statusDelivered(binding: CompletedOrderLayoutBinding) {
        binding.completedProgressBar.visibility = View.GONE
        binding.completedOrderStatusImage.visibility = View.VISIBLE
        binding.completedOrderStatusImage.setImageResource(R.drawable.ic_delivered)
        binding.completedOrderStatusText.text =
            binding.root.context.getString(R.string.order_delivered)

    }

    private fun statusCancelled(binding: CompletedOrderLayoutBinding) {
        binding.completedProgressBar.visibility = View.GONE
        binding.completedOrderStatusImage.visibility = View.VISIBLE
        binding.completedOrderStatusImage.setImageResource(R.drawable.ic_cancelled)
        binding.completedOrderStatusText.text =
            binding.root.context.getString(R.string.order_cancelled)

    }


    private fun makeGoneRecyclerView(binding: CompletedOrderLayoutBinding) {
        binding.completedOrderDetails.visibility = View.GONE
        binding.completedInfoArrowUp.visibility = View.GONE
        binding.completedInfoArrowDown.visibility = View.VISIBLE
    }

    private fun makeVisRecyclerView(binding: CompletedOrderLayoutBinding) {
        binding.completedOrderDetails.visibility = View.VISIBLE
        binding.completedInfoArrowUp.visibility = View.VISIBLE
        binding.completedInfoArrowDown.visibility = View.GONE
    }

    fun setCompletedOrderData(completedOrder: List<Order>) {
        val myOrderDiffUtil = MyOrderDiffUtil(completedOrderList, completedOrder)
        val myOrderDiffResult = DiffUtil.calculateDiff(myOrderDiffUtil)
        this.completedOrderList = completedOrder
        myOrderDiffResult.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return completedOrderList.size
    }

}

