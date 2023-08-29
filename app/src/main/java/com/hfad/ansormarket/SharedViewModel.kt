package com.hfad.ansormarket

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private var mProgressDialog: Dialog? = null


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
        snackBar.view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.snackbar_error_color))
        snackBar.animationMode = ANIMATION_MODE_SLIDE
        snackBar.show()
    }

}