package com.hfad.ansormarket.mainFragments

import android.app.ActionBar
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.hfad.ansormarket.R
import com.hfad.ansormarket.adapters.OrderListAdapter
import com.hfad.ansormarket.databinding.ContactUsLayoutBinding
import com.hfad.ansormarket.databinding.FragmentOrdersBinding
import com.hfad.ansormarket.firebase.FirebaseViewModel
import com.hfad.ansormarket.models.ContactUs
import jp.wasabeef.recyclerview.animators.ScaleInAnimator


class OrdersFragment : Fragment() {
    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private val adapter: OrderListAdapter by lazy { OrderListAdapter() }
    private val mFirebaseViewModel: FirebaseViewModel by viewModels()
    private var mDialog: Dialog? = null
    private var contactUsInfoBinding: ContactUsLayoutBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        showRecyclerView()

        binding.refreshOrder.setOnClickListener {
            regOrNot()
        }

        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFirebaseViewModel.orderNowLiveData.observe(viewLifecycleOwner) { myOrderList ->
            Log.d("OrdersFragment", "Received ${myOrderList.size} orders")
            adapter.setMyCartData(myOrderList)
        }
        regOrNot()
        mFirebaseViewModel.getContactUs()

        binding.lifecycleOwner = this
        binding.mFirebaseViewModel = mFirebaseViewModel

    }

    private fun showRecyclerView() {
        val recyclerView = binding.recyclerViewOrderList
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.itemAnimator = ScaleInAnimator().apply {
            addDuration = 200
        }

        adapter.setDeleteMyOrderClickListener(object :
            OrderListAdapter.OnDeleteMyOrderClickListener {
            override fun onDeleteMyOrderClick(currentItem: String) {
                mFirebaseViewModel.deleteMyOrder(requireView(), currentItem)
            }

        })
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.order_menu, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.contact_uss_menuO -> {
                val contactUs =
                    mFirebaseViewModel.contactUsLiveData.value // Replace with your actual method to retrieve contact info
                if (contactUs != null) {
                    showInfoDialog(contactUs)
                } else {
                    // Handle the case where contact info is not available
                    Toast.makeText(
                        requireContext(),
                        "Contact info not available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showInfoDialog(contactUs: ContactUs) {

        mDialog = Dialog(requireContext(), R.style.Bottom_Sheet_Style)
        contactUsInfoBinding = ContactUsLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        val window: Window = mDialog!!.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent)
//        window.attributes.windowAnimations = R.style.DialogAnimation
        contactUsInfoBinding!!.marketNumber.text = contactUs.MarketMobile
        contactUsInfoBinding!!.deliveryNumber.text = contactUs.DeliveryMobile
        contactUsInfoBinding!!.appContact.text = contactUs.AppMobile
        contactUsInfoBinding!!.workingHoursFrom.text = contactUs.WorkingHoursFrom.toString()
        contactUsInfoBinding!!.workingHoursTill.text = contactUs.WorkingHoursTill.toString()

        contactUsInfoBinding!!.btnBack.setOnClickListener {
            hideInfoDialog()
        }

        contactUsInfoBinding!!.loAppMarket.setOnClickListener {
            openTelegramChat(contactUs)
        }
        contactUsInfoBinding!!.loDelivery.setOnClickListener {
            openPhoneDialerDelivery(contactUs)
        }
        contactUsInfoBinding!!.loMarket.setOnClickListener {
            openPhoneDialerMarket(contactUs)
        }

        mDialog!!.setContentView(contactUsInfoBinding!!.root)
        mDialog!!.setCancelable(true)
        mDialog!!.setCanceledOnTouchOutside(true)
        mDialog!!.show()
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

    }

    private fun openPhoneDialerDelivery(contactUs: ContactUs) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contactUs.DeliveryMobile}"))
        startActivity(intent)
    }

    private fun openPhoneDialerMarket(contactUs: ContactUs) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contactUs.MarketMobile}"))
        startActivity(intent)
    }

    private fun openTelegramChat(contactUs: ContactUs) {
        val tlgLink = contactUs.AppMobile
        try {
            val telegramUri = Uri.parse("tg://resolve?domain=$tlgLink")
            val intent = Intent(Intent.ACTION_VIEW, telegramUri)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
        }
    }

    private fun hideInfoDialog() {
        mDialog!!.dismiss()
    }

    companion object {
        @JvmStatic
        fun gif1(view: View) {
            val gifImageView = view.findViewById<ImageView>(R.id.no_order_image)
            Glide.with(view)
                .asGif()
                .load(R.drawable.no_my_order)
                .into(gifImageView)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun regOrNot() {
        val userId = mFirebaseViewModel.getCurrentUserId()

        if (userId != null && userId.isNotEmpty()) {
            mFirebaseViewModel.getMyOrders(requireView())
            binding.emptyOrderView.visibility =
                View.GONE // Hide emptyCartView when user is logged in
        } else {
            binding.emptyOrderView.visibility =
                View.VISIBLE // Show emptyCartView when user is not logged in
        }
    }

}
