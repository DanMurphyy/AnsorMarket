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
    val createItemLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val itemList: MutableLiveData<List<Item>> = MutableLiveData()


    init {
        repository = FirebaseRepository()
    }

    fun registerUser(view: View, name: String, login: String, password: String) {
        showProgress(view.context)
        viewModelScope.launch {
            val validation = validateForm(view, name, login, password)
            if (validation) {
                try {
                    val isSuccess = repository.registerUser(name, login, password)
                    if (isSuccess) {
                        signInUser(
                            view,
                            login,
                            password
                        )  // Automatically sign in the registered user
                        IntroFragment().success() // Call the success function for navigation
                    } else {
                        hideProgress()
                        registrationResult.value = false
                    }
                } catch (e: Exception) {
                    hideProgress()    // Handle exceptions here
                    registrationResult.value = false
                }
                hideProgress()
            }
            hideProgress()
        }
    }

    fun createItem(view: View, item: Item) {
        showProgress(view.context)
        viewModelScope.launch {
            try {
                repository.createItem(item)
                val items = repository.getAllItems()
                itemList.postValue(items)
                createItemLiveData.postValue(true) // Set the value to true when item is created successfully
                hideProgress()
            } catch (e: Exception) {
                createItemLiveData.postValue(false) // Set the value to false when item creation fails
                hideProgress()
            }
            hideProgress()
        }
    }

    fun fetchAllItems() {
        viewModelScope.launch {
            try {
                val items = repository.getAllItems()
                itemList.postValue(items)
                Log.d("FirebaseViewModel", "fetchAllItems: Items fetched successfully.")

            } catch (e: Exception) {
                // Handle the error
                Log.e("FirebaseViewModel", "Error fetching items: ${e.message}", e)

            }
        }
    }

    fun loadUserData() {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId()
                val user = repository.getUserData(userId)
                userLiveData.postValue(user)
            } catch (e: Exception) {
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

    fun updateUserProfileData(view: View, updatedUser: User) {
        showProgress(view.context)
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId()
                val user = userLiveData.value

                // Check if the user object is not null
                if (user != null) {
                    // Update the user object fields based on the updatedUser object
                    user.name = updatedUser.name
                    user.address = updatedUser.address
                    user.mobile = updatedUser.mobile
                    if (updatedUser.image.isNotEmpty() && updatedUser.image != user.image) {
                        user.image = updatedUser.image
                    }

                    repository.updateUserData(userId, user)
                    userLiveData.postValue(user!!)
                }
                hideProgress()
            } catch (e: Exception) {
                hideProgress()
            }
            hideProgress()
        }
    }

    fun uploadImage(context: Context, uri: Uri) {
        showProgress(context)
        viewModelScope.launch {
            try {
                val extension = getFileExtension(uri)
                if (extension != null) {
                    val filename = "USER_IMAGE_${System.currentTimeMillis()}.$extension"
                    val imageUrl = repository.uploadUserImage(uri, filename)
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

    fun uploadItemImage(context: Context, uri: Uri) {
        showProgress(context)
        viewModelScope.launch {
            try {
                val extension = getFileExtension(uri)
                if (extension != null) {
                    val filename = "USER_IMAGE_${System.currentTimeMillis()}.$extension"
                    val imageUrl = repository.uploadItemImage(uri, filename)
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

    fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(context.contentResolver?.getType(uri!!))
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

    private fun hideProgress() {
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

}