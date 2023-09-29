package com.hfad.ansormarket.mainFragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.gson.Gson
import com.hfad.ansormarket.R
import com.hfad.ansormarket.SharedViewModel
import com.hfad.ansormarket.adapters.MyCartListAdapter
import com.hfad.ansormarket.databinding.FragmentCartBinding
import com.hfad.ansormarket.firebase.FirebaseViewModel
import com.hfad.ansormarket.firebase.retrofit.NotificationData
import com.hfad.ansormarket.firebase.retrofit.PushNotification
import com.hfad.ansormarket.firebase.retrofit.RetrofitInstance
import com.hfad.ansormarket.models.Constants.TOPIC
import com.hfad.ansormarket.models.MyCart
import com.hfad.ansormarket.models.Order
import jp.wasabeef.recyclerview.animators.ScaleInAnimator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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
        showRecyclerView()
        val adRequest = AdRequest.Builder().build()
        binding.adView2.loadAd(adRequest)
        mFirebaseViewModel.fetchAllItems()
        mFirebaseViewModel.fetchMyCart()
        mFirebaseViewModel.loadUserData(requireContext())
        updateAmount()
        mFirebaseViewModel.getContactUsOrder()

        binding.btnMakeOrder.setOnClickListener {
            checkTime()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFirebaseViewModel.myCartsLiveData.observe(viewLifecycleOwner) { myCartList ->
            adapter.setMyCartData(myCartList)
            updateAmount()
            mSharedViewModel.cartItemCount(requireView(), myCartList)
            updateItemPriceDifference(myCartList)
        }
        mFirebaseViewModel.fetchMyCart()
        mFirebaseViewModel.loadUserDataForOrder()
        binding.lifecycleOwner = this
        binding.mFirebaseViewModel = mFirebaseViewModel
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
        if (totalAmount.toString().isNotEmpty() && totalAmount <= 99999) {
            binding.feeInfo.visibility = View.VISIBLE
        } else {
            binding.feeInfo.visibility = View.GONE
        }
    }

    private fun checkTime() {
        val workingHoursFrom = mFirebaseViewModel.contactUsLiveData.value?.WorkingHoursFrom
        val workingHoursTill = mFirebaseViewModel.contactUsLiveData.value?.WorkingHoursTill
        val address = mFirebaseViewModel.userLiveData.value?.address

        if (address.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.insert_address),
                Toast.LENGTH_LONG
            ).show()
        } else {
            if (workingHoursFrom != null && workingHoursTill != null) {
                val currentTime: String = SimpleDateFormat("HH", Locale.ENGLISH).format(
                    Date()
                )
                if (currentTime.toInt() in (workingHoursFrom) until workingHoursTill) {
                    orderDialog()
                } else {
                    offTimeDialog()
                }
            } else {
                orderDialog()
            }
        }

    }

    private fun offTimeDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.off_work_time))
        builder.setPositiveButton(getString(R.string.proceed_off_work_time)) { _, _ ->
            orderDialog()
        }
        builder.setNegativeButton(getString(R.string.close_dialog_perm)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun orderDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.payment_method))
        builder.setPositiveButton(getString(R.string.order_off_work_time)) { _, _ ->
            orderNow()
        }
        builder.setNegativeButton(getString(R.string.close_dialog_perm)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun orderNow() {
        // Ensure that userLiveData and myCartsLiveData are not null
        val user = mFirebaseViewModel.userLiveData.value
        val myCartList = mFirebaseViewModel.myCartsLiveData.value

        if (user != null && myCartList != null) {
            // Create an Order object with valid user and cart data
            val order = Order(
                orderStatus = 0,
                orderUser = user,
                orderProducts = myCartList,
                totalAmount = grandTotal
            )
            // Call the orderNow function in FirebaseViewModel
            mFirebaseViewModel.orderNow(requireView(), order)

            mFirebaseViewModel.orderNowResult.observe(viewLifecycleOwner) { isSuccess ->
                if (isSuccess != null) { // Check for null before showing the Toast
                    if (isSuccess) {
                        updateAfterDelete()
                        Toast.makeText(
                            requireContext(), getString(R.string.ordered_placed), Toast.LENGTH_SHORT
                        ).show()
                        sendPushNow(order.orderProducts.size.toString() + order.orderUser.address)
                        // Reset orderNowResult to its neutral state
                        mFirebaseViewModel.resetOrderNowResult()
                    } else {
                        Toast.makeText(
                            requireContext(), getString(R.string.ordering_error), Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        } else {
            // If user or myCartList is null, display a message
            Toast.makeText(
                requireContext(), "User or Cart data is null", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateAfterDelete() {
        mFirebaseViewModel.myCartsLiveData.observe(viewLifecycleOwner) { myCartList ->
            adapter.setMyCartData(myCartList)
            mSharedViewModel.cartItemCount(requireView(), myCartList)
        }
        updateAmount()
        mFirebaseViewModel.fetchMyCart()
    }

    private fun updateItemPriceDifference(listOfCart: List<MyCart>) {

        val items = mFirebaseViewModel.itemList.value

        if (items != null) {
            for (i in items) {
                for (j in listOfCart)
                    if (j.itemProd.documentId == i.documentId) {
                        if (j.itemProd != i)
                            Log.d("CartFragment", "cart: $j")
                        Log.d("CartFragment", "cart: $i")
                        mFirebaseViewModel.updateCartItemPrice(j.documentId, i)
                        Log.d("CartFragment", "cart: $j.documentId and $i.price")
                    }
            }
        }
    }

    private fun sendPushNow(message: String) {
        val mTitle = getString(R.string.new_Order)
        if (mTitle.isNotEmpty() && message.isNotEmpty()) {
            PushNotification(NotificationData(mTitle, message), TOPIC).also {
                sendNotification(it)
            }
        } else {
            Log.w(TAG, "onCreate: Title and/or message is empty")
        }
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                    Log.d(
                        TAG, "sendNotification: Notification sent successfully. Response: ${
                            Gson().toJson(response)
                        }"
                    )
                } else {
                    Log.e(
                        TAG, "sendNotification: Error sending notification. Response: ${
                            response.errorBody().toString()
                        }"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "sendNotification: Exception occurred: $e")

            }
        }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

private const val TAG = "SendNotification"
