package com.hfad.ansormarket.adapters

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.hfad.ansormarket.R
import com.hfad.ansormarket.firebase.FirebaseRepository
import com.hfad.ansormarket.mainFragments.OrdersFragment
import com.hfad.ansormarket.models.MyCart
import com.hfad.ansormarket.models.Order

class BindingAdapter {

    companion object {

        @JvmStatic
        @BindingAdapter("android:imageUrl")
        fun loadImage(view: ImageView, imageUrl: String?) {
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(view.context)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(view)
            }
        }

        @JvmStatic
        @BindingAdapter("android:emptyMyOrder")
        fun emptyMyOrder(view: View, orderNowLiveData: MutableLiveData<List<Order>>?) {
            val isEmpty = orderNowLiveData?.value?.isEmpty() ?: false
            val userId = FirebaseRepository().getCurrentUserId()
            if (userId.isNotEmpty()) {
                view.visibility = if (isEmpty) {
                    OrdersFragment.gif1(view)
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
            } else {
                OrdersFragment.gif1(view)
                view.visibility = View.VISIBLE // Show emptyCartView when user is not logged in
            }
        }

        @JvmStatic
        @BindingAdapter("android:emptyMyCart")
        fun emptyMyCart(view: View, myCartsLiveData: MutableLiveData<List<MyCart>>?) {
            val isEmpty = myCartsLiveData?.value?.isEmpty() ?: false

            val userId = FirebaseRepository().getCurrentUserId()
            if (userId.isNotEmpty()) {
                view.visibility = if (isEmpty) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
            } else {
                view.visibility = View.VISIBLE // Show emptyCartView when user is not logged in
            }
        }

        @JvmStatic
        @BindingAdapter("android:oppositeEmptyMyCart")
        fun oppositeEmptyMyCart(view: View, myCartsLiveData: MutableLiveData<List<MyCart>>?) {
            val isEmpty = myCartsLiveData?.value?.isEmpty() ?: true
            view.visibility = if (!isEmpty) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
        }

    }
}