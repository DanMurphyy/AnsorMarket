package com.hfad.ansormarket

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar
import com.hfad.ansormarket.models.Constants
import com.hfad.ansormarket.models.MyCart

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    var doubleBackToExitPressedOnce = false
    private val context = getApplication<Application>()
    private var mProgressDialog: Dialog? = null

    fun cartItemCount(view: View,myCart: List<MyCart>) {
        val count = myCart.size
        val bottomNavigationView = (view.context as MainActivity).getBottomNavigationView()
        val badge = bottomNavigationView.getOrCreateBadge(R.id.cartFragment)
        badge.number = count
        badge.backgroundColor = view.resources.getColor(R.color.colorPrimary)
    }

    fun showProgress(context: Context) {
        mProgressDialog = Dialog(context)
        mProgressDialog?.setContentView(R.layout.dialog_progress)
        mProgressDialog?.window!!.setBackgroundDrawableResource(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
        mProgressDialog?.show()
    }

    fun hideProgress() {
        mProgressDialog!!.dismiss()
    }

    fun showErrorSnackBar(view: View) {
        val message = view.context.getString(R.string.error_message)
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAnchorView(R.id.rg_signings)
        snackBar.view.setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                R.color.snackbar_error_color
            )
        )
        snackBar.animationMode = ANIMATION_MODE_SLIDE

        snackBar.show()
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

    fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(context.contentResolver?.getType(uri!!))
    }

//    fun parseItemType(itemType: String): ItemType? {
//        return when (itemType) {
//            "Ichimliklar" -> ItemType.DRINK
//            "Qandolat mahsulotlari" -> ItemType.CANDY
//            "Sut Mahsulotlari" -> ItemType.DAIRY
//            "Tortlar va Pishiriqlar" -> ItemType.CAKE
//            "Konservalar" -> ItemType.CANNED
//            "Snaklar" -> ItemType.SNACK
//            "Mevalar" -> ItemType.FRUIT
//            "Bolalar Ovqatlari" -> ItemType.BABYFOOD
//            "Un Mahsulotlari" -> ItemType.FLOUR
//            "Souslar" -> ItemType.SAUCE
//            "Go\'sht" -> ItemType.MEAT
//            "Gigiena vositalari" -> ItemType.HYGIENE
//            "Oshxona" -> ItemType.KITCHEN
//            "Yog\'lar" -> ItemType.OIL
//            else -> ItemType.DRINK
//        }
//    }


//    fun parsePriorityToInt(priority: Priority): Int {
//        return when (priority) {
//            Priority.HIGH -> 0
//            Priority.MEDIUM -> 1
//            Priority.LOW -> 2
//        }
//    }
}