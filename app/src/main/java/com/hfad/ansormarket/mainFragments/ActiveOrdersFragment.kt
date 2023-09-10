package com.hfad.ansormarket.mainFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.hfad.ansormarket.R
import com.hfad.ansormarket.adapters.ActiveOrderListAdapter
import com.hfad.ansormarket.databinding.FragmentActiveOrdersBinding
import com.hfad.ansormarket.firebase.FirebaseViewModel
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
            updateOrderVisibility(activeOrderList.isEmpty())
        }

        mFirebaseViewModel.getActiveOrders(requireView())
    }

    private fun showRecyclerView() {
        val recyclerView = binding.recyclerViewActiveOrderList
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.itemAnimator = ScaleInAnimator().apply {
            addDuration = 200
        }
    }

    private fun updateOrderVisibility(isEmpty: Boolean) {
        val recyclerView = binding.recyclerViewActiveOrderList
        val emptyOrderView = binding.emptyActiveOrderView
        if (isEmpty) {
            recyclerView.visibility = View.GONE
            emptyOrderView.visibility = View.VISIBLE
            gif()
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyOrderView.visibility = View.GONE
        }
    }

    private fun gif() {
        val gifImageView = binding.noActiveOrderImage
        Glide.with(requireContext())
            .asGif()
            .load(R.drawable.no_order) // Assuming "fire.gif" is the name of your animated GIF file
            .into(gifImageView)
    }

}