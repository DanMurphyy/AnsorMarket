package com.hfad.ansormarket.firebase

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.hfad.ansormarket.R
import com.hfad.ansormarket.models.Item
import kotlinx.coroutines.launch

class FirebaseViewModel(application: Application) : AndroidViewModel(application) {
    private var mProgressDialog: Dialog? = null

    private val repository: FirebaseRepository
    val registrationResult: MutableLiveData<Boolean> = MutableLiveData()
    val signInResult: MutableLiveData<Boolean> = MutableLiveData()

    init {
        repository = FirebaseRepository()
    }


    fun registerUser(view: View, name: String, login: String, password: String) {

        showProgress(view.context)
        viewModelScope.launch {
            val validation = validateForm(view, name, login, password)
            if (validation) {
                Log.d("FirebaseViewModel", "Validation passed")
                try {
                    val isSuccess = repository.registerUser(name, login, password)
                    hideProgress()
                    registrationResult.value = isSuccess
                } catch (e: Exception) {
                    // Handle exceptions here
                    hideProgress()
                    registrationResult.value = false
                }
            }
            hideProgress()
        }
    }

    fun signInUser(view: View, login: String, password: String) {
        showProgress(view.context)
        viewModelScope.launch {
            val validation = validateSignInForm(view, login, password)
            if (validation) {
                try {
                    val isSuccess = repository.signInUser(login, password)
                    hideProgress()
                    signInResult.value = isSuccess
                } catch (e: Exception) {
                    hideProgress()
                    signInResult.value = false
                }
            }
            hideProgress()
        }
    }


    suspend fun createBoard(item: Item): Boolean {
        return repository.createBoard(item)
    }

    suspend fun getBoardsList(): List<Item> {
        return repository.getItemList()
    }

     fun getCurrentUserId(): String {
        return repository.getCurrentUserId()
    }

    private fun validateForm(view: View, name: String, login: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar(view, "Please enter a name")
                false
            }
            TextUtils.isEmpty(login) -> {
                showErrorSnackBar(view, "Please enter an email address")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar(view, "Please enter an password")
                false
            }
            else -> {
                true
            }
        }
    }

    private fun validateSignInForm(view: View, login: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(login) -> {
                showErrorSnackBar(view, "Please enter an email address")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar(view, "Please enter an password")
                false
            }
            else -> {
                true
            }
        }
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

    fun showErrorSnackBar(view: View, message: String) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snackBar.view.setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                R.color.snackbar_error_color
            )
        )
        snackBar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        snackBar.show()
    }

}
