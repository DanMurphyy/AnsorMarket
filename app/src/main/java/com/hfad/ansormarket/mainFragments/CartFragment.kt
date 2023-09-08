package com.hfad.ansormarket.mainFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hfad.ansormarket.R
import com.hfad.ansormarket.SharedViewModel
import com.hfad.ansormarket.adapters.MyCartListAdapter
import com.hfad.ansormarket.databinding.FragmentCartBinding
import com.hfad.ansormarket.firebase.FirebaseViewModel
import com.hfad.ansormarket.models.utils.SwipeToDelete
import jp.wasabeef.recyclerview.animators.ScaleInAnimator

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val mFirebaseViewModel: FirebaseViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val adapter: MyCartListAdapter by lazy { MyCartListAdapter() }
    private var grandTotal: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showRecyclerView()
        mFirebaseViewModel.myCartsLiveData.observe(viewLifecycleOwner) { myCartList ->
            adapter.setMyCartData(myCartList)
            updateCartVisibility(myCartList.isEmpty())
            updateAmount()
            mSharedViewModel.cartItemCount(requireView(), myCartList)
        }
        showRecyclerView()
        updateAmount()
        mFirebaseViewModel.fetchMyCart()
    }

    private fun showRecyclerView() {
        val recyclerView = binding.recyclerViewCartItems
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.itemAnimator = ScaleInAnimator().apply {
            addDuration = 200
        }
        adapter.setOnClickListener(object : MyCartListAdapter.OnClickListenerCart {
            override fun onDeleteCartClick(currentItem: String) {
                mFirebaseViewModel.deleteMyCart(currentItem)
            }

            override fun onUpdateCartQuantityClick(
                currentItem: String,
                newQuantity: Int,
                newAmount: Int
            ) {
                mFirebaseViewModel.updateCartItemQuantity(currentItem, newQuantity, newAmount)
            }
        })

    }

    private fun updateAmount() {
        val totalAmount = adapter.calculateTotalAmount()
        binding.subtotalPrice.text = totalAmount.toString()

        if (totalAmount <= 99999) {
            val withDelivery = totalAmount + 10000
            binding.deliveryFeePrice.text = getString(R.string.deliver_charge_amount)
            grandTotal = withDelivery
            binding.priceTotalGrand.text = grandTotal.toString()

        } else {
            binding.deliveryFeePrice.text = "0"
            grandTotal = totalAmount
            binding.priceTotalGrand.text = grandTotal.toString()
        }

    }

    private fun updateCartVisibility(isEmpty: Boolean) {
        val recyclerView = binding.recyclerViewCartItems
        val emptyCartView = binding.emptyCartView
        val rateMethodAndBtn = binding.rateAndMethod
        if (isEmpty) {
            recyclerView.visibility = View.GONE
            rateMethodAndBtn.visibility = View.GONE
            emptyCartView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            rateMethodAndBtn.visibility = View.VISIBLE
            emptyCartView.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
