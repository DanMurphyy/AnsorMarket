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
import com.hfad.ansormarket.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseViewModel(application: Application) : AndroidViewModel(application) {
    private var mProgressDialog: Dialog? = null
    private val context = getApplication<Application>()

    private val repository: FirebaseRepository
    val registrationResult: MutableLiveData<Boolean> = MutableLiveData()
    val userLiveData: MutableLiveData<User> = MutableLiveData()
    val contactUsLiveData: MutableLiveData<ContactUs> = MutableLiveData()
    val imageUploadLive: MutableLiveData<String?> = MutableLiveData()
    val imageUploadResult: MutableLiveData<Boolean?> = MutableLiveData()
    val itemList: MutableLiveData<List<Item>> = MutableLiveData()
    var isListShuffled: Boolean = false // Add a flag to track whether the list is shuffled
    val itemListForCart: MutableLiveData<List<Item>> = MutableLiveData()
    val toCartResult: MutableLiveData<Boolean> = MutableLiveData()
    val myCartsLiveData: MutableLiveData<List<MyCart>> = MutableLiveData()
    val orderNowResult: MutableLiveData<Boolean?> = MutableLiveData()
    val orderNowLiveData: MutableLiveData<List<Order>> = MutableLiveData()


    init {
        repository = FirebaseRepository()
    }

    fun resetImageUploadResult() {
        imageUploadResult.value = null
    }

    fun resetOrderNowResult() {
        orderNowResult.value = null
    }

    fun registerUser(view: View, name: String, phoneNumber: String, address: String) {
        showProgress(view.context)
        viewModelScope.launch {
            try {
                Log.d("TAG", "being called3")
                val isSuccess = repository.registerUser(name, phoneNumber, address)
                if (isSuccess) {
                    IntroFragment().success() // Call the success function for navigation
                    Log.d("TAG", "being called4")

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


    fun loadUserData(context: Context) {
        showProgress(context)
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId()
                val user = repository.getUserData(userId)
                userLiveData.postValue(user)
                hideProgress()
            } catch (e: Exception) {
                hideProgress()
            }
            hideProgress()
        }
    }

    fun updateUserProfileData(view: View, updatedUser: User) {
        showProgress(view.context)
        viewModelScope.launch {
            val validation = validateForm(
                view,
                updatedUser.name,
                updatedUser.address,
                updatedUser.mobile
            )
            if (validation) {
                try {
                    val userId = getCurrentUserId()
                    val user = userLiveData.value
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
                } finally {
                    hideProgress()
                }
            }
            hideProgress()
        }
    }

    fun loadUserDataForOrder() {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId()
                val user = repository.getUserData(userId)
                userLiveData.postValue(user)
            } catch (e: Exception) {
            }
        }
    }

    fun getContactUs() {
        viewModelScope.launch {
            try {
                val contact = repository.getContactUs()
                contactUsLiveData.postValue(contact)
            } catch (e: Exception) {
            }
        }
    }

    fun getContactUsOrder() {
        viewModelScope.launch {
            try {
                val contact = repository.getContactUs()
                contactUsLiveData.postValue(contact)
            } catch (e: Exception) {
            }
        }
    }

    fun fetchAllItems() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val items = repository.getAllItems().shuffled()
                itemList.postValue(items)
                isListShuffled = true
            } catch (e: Exception) {
                // Handle the error
                Log.e("FirebaseViewModel", "Error fetching items: ${e.message}", e)
            }
        }
    }

    fun fetchAllItemsForCart() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val items = repository.getAllItems()
                itemListForCart.postValue(items)
            } catch (e: Exception) {
                // Handle the error
                Log.e("FirebaseViewModel", "Error fetching items: ${e.message}", e)
            }
        }
    }

    fun toCart(view: View, myCart: MyCart) {
        showProgress(view.context)
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId()
                // Update the user's cart in Firestore
                repository.toCart(userId, myCart)
                toCartResult.postValue(true) // Set the value to true when item is added to the cart successfully
                hideProgress()
            } catch (e: Exception) {
                // Handle the error
                toCartResult.postValue(false) // Set the value to false when item addition to the cart fails
                hideProgress()
            }
        }
    }

    fun fetchMyCart() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = getCurrentUserId()
                Log.d("FirebaseViewModel", "fetchMyCart: Fetching cart for user ID: $userId")
                val userCart = repository.getUserCart(userId)
                myCartsLiveData.postValue(userCart)
                Log.d("FirebaseViewModel", "fetchMyCart: ${userCart.size} items fetched.")
            } catch (e: Exception) {
                // Handle the error
                Log.e("FirebaseViewModel", "Error fetching cart: ${e.message}", e)
            }
        }
    }

    fun deleteMyCart(documentId: String) {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId()
                repository.deleteFromCart(userId, documentId)
                val userCart = repository.getUserCart(userId)
                myCartsLiveData.postValue(userCart)
            } catch (e: Exception) {
                // Handle the error
            }
        }
    }

    fun updateCartItemQuantity(documentId: String, newQuantity: Int, newAmount: Int) {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId()
                repository.updateCartItemQuantity(userId, documentId, newQuantity, newAmount)
                val userCart = repository.getUserCart(userId)
                myCartsLiveData.postValue(userCart)
            } catch (e: Exception) {
                // Handle the error
            }
        }
    }

    fun updateCartItemPrice(documentId: String, newItem: Item) {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId()
                repository.updateCartItemPrice(userId, documentId, newItem)
                val userCart = repository.getUserCart(userId)
                myCartsLiveData.postValue(userCart)
            } catch (e: Exception) {
                // Handle the error
            }
        }
    }

    fun orderNow(view: View, order: Order) {
        showProgress(view.context)
        viewModelScope.launch() {
            try {
                val userId = getCurrentUserId()
                repository.orderNow(userId, order)
                orderNowResult.postValue(true) // Set the value to true when item is created successfully
                repository.deleteCart(userId)
                val userCart = repository.getUserCart(userId)
                myCartsLiveData.postValue(userCart)
                hideProgress()
            } catch (e: Exception) {
                orderNowResult.postValue(false) // Set the value to false when item creation fails
                hideProgress()
            }

        }
    }

    fun getMyOrders(view: View) {
        showProgress(view.context)
        viewModelScope.launch {
            try {
                getCurrentUserId()
                val userId = getCurrentUserId()
                val myOrders = repository.getMyOrders(userId)

                Log.d("FirebaseViewModel", "getMyOrders: ${myOrders.size} items fetched.")
                orderNowLiveData.postValue(myOrders)
                hideProgress()

            } catch (e: Exception) {
                // Handle the error
                Log.e("FirebaseViewModel", "Error fetching myOrder: ${e.message}", e)
                hideProgress()

            }
        }
    }

    fun deleteMyOrder(view: View, documentId: String) {
        showProgress(view.context)
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId()
                repository.deleteMyOrder(userId, documentId)
                val myOrders = repository.getMyOrders(userId)
                orderNowLiveData.postValue(myOrders)
                hideProgress()
            } catch (e: Exception) {
                // Handle the error
            }
        }
    }

    fun uploadImage(context: Context, uri: Uri) {
        showProgress(context)
        viewModelScope.launch {
            try {
                val extension = getFileExtension(uri)
                if (extension != null && extension.isNotEmpty()) {
                    val filename = "USER_IMAGE_${System.currentTimeMillis()}.$extension"
                    val imageUrl = repository.uploadUserImage(uri, filename)
                    imageUploadLive.postValue(imageUrl)
                    imageUploadResult.postValue(true)
                    hideProgress()
                }
            } catch (e: Exception) {
                hideProgress()
                imageUploadLive.postValue(null) // Indicate failure
                imageUploadResult.postValue(false)

            }
        }
    }

    fun getCurrentUserId(): String {
        return repository.getCurrentUserId()
    }

    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(context.contentResolver?.getType(uri!!))
    }


    fun showProgress(context: Context) {
        mProgressDialog = Dialog(context)
        mProgressDialog?.setContentView(R.layout.dialog_progress)
        mProgressDialog?.window!!.setBackgroundDrawableResource(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
        mProgressDialog?.show()
    }

    fun validateForm(view: View, name: String, address: String, number: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar(view, view.context.getString(R.string.please_enter_name))
                false
            }
            TextUtils.isEmpty(address) -> {
                showErrorSnackBar(view, view.context.getString(R.string.please_enter_address))
                false
            }
            TextUtils.isEmpty(number) -> {
                showErrorSnackBar(view, view.context.getString(R.string.please_enter_number))
                false
            }
            else -> {
                true
            }
        }
    }

    private fun showErrorSnackBar(view: View, message: String) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAnchorView(R.id.lo_radio)
        snackBar.view.setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                R.color.snackbar_error_color
            )
        )
        //        snackBar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        snackBar.show()
    }

    fun hideProgress() {
        mProgressDialog!!.dismiss()
    }

}