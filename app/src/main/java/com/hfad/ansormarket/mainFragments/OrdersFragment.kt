package com.hfad.ansormarket.mainFragments

import android.app.ActionBar
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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

        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFirebaseViewModel.orderNowLiveData.observe(viewLifecycleOwner) { myOrderList ->
            Log.d("OrdersFragment", "Received ${myOrderList.size} orders")
            adapter.setMyCartData(myOrderList)
        }
        mFirebaseViewModel.getMyOrders(requireView())
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.order_menu, menu)
    }

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
            R.id.active_orders_menuO -> navigate()
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
        contactUsInfoBinding!!.workingHours.text = contactUs.WorkingHours

        contactUsInfoBinding!!.btnBack.setOnClickListener {
            hideInfoDialog()
        }

        mDialog!!.setContentView(contactUsInfoBinding!!.root)
        mDialog!!.setCancelable(true)
        mDialog!!.setCanceledOnTouchOutside(true)
        mDialog!!.show()
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

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

    private fun navigate() {
        findNavController().navigate(R.id.action_ordersFragment_to_activeOrdersFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
