package com.hfad.ansormarket

import android.app.AlertDialog
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hfad.ansormarket.models.Constants
import com.hfad.ansormarket.models.MyCart

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    var doubleBackToExitPressedOnce = false
    private val context = getApplication<Application>()

    fun cartItemCount(view: View, myCart: List<MyCart>) {
        val count = myCart.size
        val bottomNavigationView = (view.context as MainActivity).getBottomNavigationView()
        val badge = bottomNavigationView.getOrCreateBadge(R.id.cartFragment)
        badge.number = count
        badge.backgroundColor = view.resources.getColor(R.color.colorPrimary)
    }

    fun doubleBackToExit(): Boolean {
        if (doubleBackToExitPressedOnce) {
            return true
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            context,
            context.resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()

        return false
    }

    fun showImageChooser(fragment: Fragment) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        fragment.startActivityForResult(galleryIntent, Constants.PICK_IMAGE_REQUEST_CODE)
    }

}