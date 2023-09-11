package com.hfad.ansormarket.mainFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
        showRecyclerView()

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

        })

    }

    private fun updateStatus(order: Order, selectedPosition: Int) {
        mFirebaseViewModel.updateActiveOrders(
            binding.root.rootView,
            order.orderedId,
            selectedPosition
        )


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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}