package com.hfad.ansormarket.mainFragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.hfad.ansormarket.R
import com.hfad.ansormarket.adapters.ActiveOrderListAdapter
import com.hfad.ansormarket.databinding.FragmentActiveOrdersBinding
import com.hfad.ansormarket.firebase.FirebaseViewModel
import com.hfad.ansormarket.models.Order
import jp.wasabeef.recyclerview.animators.ScaleInAnimator

class ActiveOrdersFragment : Fragment() {

    private var _binding: FragmentActiveOrdersBinding? = null
    private val binding get() = _binding!!
    private val mFirebaseViewModel: FirebaseViewModel by viewModels()
    private val adapter: ActiveOrderListAdapter by lazy { ActiveOrderListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActiveOrdersBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        showRecyclerView()

        binding.refreshActiveOrder.setOnClickListener {
            mFirebaseViewModel.getActiveOrders(requireView())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFirebaseViewModel.activeOrderNowLiveData.observe(viewLifecycleOwner) { activeOrderList ->
            Log.d("OrdersFragment", "Received ${activeOrderList.size} orders")
            adapter.setActiveMyCartData(activeOrderList)
        }
        binding.lifecycleOwner = this
        binding.mFirebaseViewModel = mFirebaseViewModel

        mFirebaseViewModel.getActiveOrders(requireView())
    }

    private fun showRecyclerView() {
        val recyclerView = binding.recyclerViewActiveOrderList
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.itemAnimator = ScaleInAnimator().apply {
            addDuration = 200
        }
        adapter.setOnSaveChangeClickListener(object :
            ActiveOrderListAdapter.OnSaveChangeClickListener {
            override fun onSaveChangeClick(order: Order, selectedPosition: Int) {
                updateStatus(order, selectedPosition)
            }

            override fun onDeleteMyOrderClick(currentItem: Order) {
                mFirebaseViewModel.moveOrder(recyclerView, currentItem)

            }

        })

    }

    private fun updateStatus(order: Order, selectedPosition: Int) {
        val updatedOrder = Order(
            orderStatus = selectedPosition,
            orderProducts = order.orderProducts,
            orderUser = order.orderUser,
            orderedId = order.orderedId
        )
        Log.d("YourTag", "Order Document ID: ${updatedOrder.orderedId}")

        mFirebaseViewModel.updateActiveOrders(requireView(), updatedOrder)
    }

    companion object {
        @JvmStatic
        fun gif(view: View) {
            val gifImageView = view.findViewById<ImageView>(R.id.no_active_order_image)
            Glide.with(view)
                .asGif()
                .load(R.drawable.no_order)
                .into(gifImageView)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.active_order_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.completed_orders_menuO -> {
                findNavController().navigate(R.id.action_activeOrdersFragment_to_completedOrdersFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}