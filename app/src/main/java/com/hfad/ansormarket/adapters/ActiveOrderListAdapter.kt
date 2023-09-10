package com.hfad.ansormarket.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hfad.ansormarket.R
import com.hfad.ansormarket.databinding.ActiveOrderLayoutBinding
import com.hfad.ansormarket.models.Order
import com.hfad.ansormarket.models.utils.MyOrderDiffUtil

class ActiveOrderListAdapter : RecyclerView.Adapter<ActiveOrderListAdapter.MyViewHolder>() {

    private var activeOrderList: List<Order> = emptyList()

    class MyViewHolder(internal val binding: ActiveOrderLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ActiveOrderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = activeOrderList[position] // Access items directly from itemList
        val binding = holder.binding
        binding.activeOrderNumber.text = currentItem.orderNumber
        binding.activeOrderAmount.text = currentItem.totalAmount.toString()
        binding.activeOrderDate.text = currentItem.date
        binding.activeOrderAddress.text = currentItem.orderUser.address
        binding.activeOrderPhone.text = currentItem.orderUser.mobile.toString()

        when (currentItem.orderStatus) {
            0 -> statusPending(binding)
            1 -> statusAccepted(binding)
            2 -> statusSend(binding)
            3 -> statusDelivered(binding)
            4 -> statusCancelled(binding)
        }

        binding.activeInfoArrowDown.setOnClickListener {
            makeVisRecyclerView(binding)
        }

        binding.activeInfoArrowUp.setOnClickListener {
            makeGoneRecyclerView(binding)
        }

        val innerAdapter: ActiveOrderMyCartListAdapter by lazy { ActiveOrderMyCartListAdapter() }
        innerAdapter.setActMyCartData(currentItem.orderProducts)

        binding.recyclerViewActiveOrderMycart.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = innerAdapter
        }
    }

    private fun statusPending(binding: ActiveOrderLayoutBinding) {
        binding.activeProgressBar.visibility = View.VISIBLE
        binding.activeOrderStatusImage.visibility = View.GONE
        binding.activeOrderStatusText.text =
            binding.root.context.getString(R.string.order_in_process)

    }

    private fun statusAccepted(binding: ActiveOrderLayoutBinding) {
        binding.activeProgressBar.visibility = View.GONE
        binding.activeOrderStatusImage.visibility = View.VISIBLE
        binding.activeOrderStatusText.text =
            binding.root.context.getString(R.string.order_getting_ready)

        val gifImageView = binding.activeOrderStatusImage
        Glide.with(binding.root.context)
            .asGif()
            .load(R.drawable.grocery) // Assuming "fire.gif" is the name of your animated GIF file
            .into(gifImageView)

    }

    private fun statusSend(binding: ActiveOrderLayoutBinding) {
        binding.activeProgressBar.visibility = View.GONE
        binding.activeOrderStatusImage.visibility = View.VISIBLE
        binding.activeOrderStatusText.text =
            binding.root.context.getString(R.string.order_delivering)


        val gifImageView = binding.activeOrderStatusImage
        Glide.with(binding.root.context)
            .asGif()
            .load(R.drawable.delivering) // Assuming "fire.gif" is the name of your animated GIF file
            .into(gifImageView)

    }

    private fun statusDelivered(binding: ActiveOrderLayoutBinding) {
        binding.activeProgressBar.visibility = View.GONE
        binding.activeOrderStatusImage.visibility = View.VISIBLE
        binding.activeOrderStatusImage.setImageResource(R.drawable.ic_delivered)
        binding.activeOrderStatusText.text =
            binding.root.context.getString(R.string.order_delivered)


    }

    private fun statusCancelled(binding: ActiveOrderLayoutBinding) {
        binding.activeProgressBar.visibility = View.GONE
        binding.activeOrderStatusImage.visibility = View.VISIBLE
        binding.activeOrderStatusImage.setImageResource(R.drawable.ic_cancelled)
        binding.activeOrderStatusText.text =
            binding.root.context.getString(R.string.order_cancelled)


    }


    private fun makeGoneRecyclerView(binding: ActiveOrderLayoutBinding) {
        binding.activeOrderDetails.visibility = View.GONE
        binding.activeInfoArrowUp.visibility = View.GONE
        binding.activeInfoArrowDown.visibility = View.VISIBLE
    }

    private fun makeVisRecyclerView(binding: ActiveOrderLayoutBinding) {
        binding.activeOrderDetails.visibility = View.VISIBLE
        binding.activeInfoArrowUp.visibility = View.VISIBLE
        binding.activeInfoArrowDown.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setActiveMyCartData(activeOrder: List<Order>) {
        val myOrderDiffUtil = MyOrderDiffUtil(activeOrder, activeOrder)
        val myOrderDiffResult = DiffUtil.calculateDiff(myOrderDiffUtil)
        this.activeOrderList = activeOrder
        myOrderDiffResult.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return activeOrderList.size
    }
}

