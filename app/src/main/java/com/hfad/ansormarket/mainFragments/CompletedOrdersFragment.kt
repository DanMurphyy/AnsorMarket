package com.hfad.ansormarket.mainFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.ansormarket.adapters.CompletedOrderListAdapter
import com.hfad.ansormarket.databinding.FragmentCompletedOrdersBinding
import com.hfad.ansormarket.firebase.FirebaseViewModel
import jp.wasabeef.recyclerview.animators.ScaleInAnimator


class CompletedOrdersFragment : Fragment() {

    private var _binding: FragmentCompletedOrdersBinding? = null
    private val binding get() = _binding!!

    private val mFirebaseViewModel: FirebaseViewModel by viewModels()
    private val adapter: CompletedOrderListAdapter by lazy { CompletedOrderListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompletedOrdersBinding.inflate(inflater, container, false)
        showRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFirebaseViewModel.moveNowLiveData.observe(viewLifecycleOwner) { movedOrderList ->
            adapter.setCompletedOrderData(movedOrderList)
        }
        mFirebaseViewModel.getCompletedOrders(requireView())
    }

    private fun showRecyclerView() {
        val recyclerView = binding.recyclerViewCompletedOrderList
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.itemAnimator = ScaleInAnimator().apply {
            addDuration = 200
        }
    }
}