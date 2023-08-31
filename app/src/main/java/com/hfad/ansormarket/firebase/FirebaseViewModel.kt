package com.hfad.ansormarket.firebase

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.hfad.ansormarket.R
import com.hfad.ansormarket.logInScreens.IntroFragment
import com.hfad.ansormarket.models.Item
import com.hfad.ansormarket.models.User
import kotlinx.coroutines.launch

class FirebaseViewModel(application: Application) : AndroidViewModel(application) {
    private var mProgressDialog: Dialog? = null
    private val context = getApplication<Application>()

    private val repository: FirebaseRepository
    val registrationResult: MutableLiveData<Boolean> = MutableLiveData()
    val signInResult: MutableLiveData<Boolean> = MutableLiveData()
    val userLiveData: MutableLiveData<User> = MutableLiveData()
    val imageUploadResult: MutableLiveData<String?> = MutableLiveData()

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
                    if (isSuccess) {
                        // Automatically sign in the registered user
                        signInUser(view, login, password)
                        // Call the success function for navigation
                        IntroFragment().success()
                    } else {
                        hideProgress()
                        registrationResult.value = false
                    }
                } catch (e: Exception) {
                    // Handle exceptions here
                    hideProgress()
                    registrationResult.value = false
                }
                hideProgress()
            }
        }
    }

    fun uploadImage(context: Context, uri: Uri) {
        showProgress(context)
        viewModelScope.launch {
            try {
                val extension = getFileExtension(uri)
                if (extension != null) {
                    val filename = "USER_IMAGE_${System.currentTimeMillis()}.$extension"
                    val imageUrl = repository.uploadImage(uri, filename)
                    imageUploadResult.postValue(imageUrl)
                } else {
                    hideProgress()
                    imageUploadResult.postValue(null) // Indicate failure
                }
            } catch (e: Exception) {
                hideProgress()
                imageUploadResult.postValue(null) // Indicate failure
            } finally {
                hideProgress()
            }
        }
    }

    fun signInUser(view: View, login: String, password: String) {
        showProgress(view.context)
        viewModelScope.launch {
            val validation = validateSignInForm(view, login, password)
            if (validation) {
                try {
                    val isSuccess = repository.signInUser(login, password)
                    loadUserData()
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

    fun loadUserData() {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId()
                val user = repository.getUserData(userId)
                userLiveData.postValue(user)
                hideProgress()
            } catch (e: Exception) {
                hideProgress()
            }
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
                showErrorSnackBar(view, view.context.getString(R.string.please_enter_name))
                false
            }
            TextUtils.isEmpty(login) -> {
                showErrorSnackBar(view, view.context.getString(R.string.please_enter_login))
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar(view, view.context.getString(R.string.please_enter_password))
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
                showErrorSnackBar(view, view.context.getString(R.string.please_enter_login))
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar(view, view.context.getString(R.string.please_enter_log_password))
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
            .setAnchorView(R.id.rg_signings)
        snackBar.view.setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                R.color.snackbar_error_color
            )
        )
        //        snackBar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        snackBar.show()
    }

    fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(context.contentResolver?.getType(uri!!))
    }

}