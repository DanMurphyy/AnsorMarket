package com.hfad.ansormarket.firebase

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.hfad.ansormarket.models.*
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    private val mFireStore = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun registerUser(name: String, phoneNumber: String, address: String): Boolean {
        try {

            // Save user data to Firestore
            val userId = auth.currentUser?.uid ?: ""
            val user = User(userId, name, phoneNumber, address)
            mFireStore.collection(Constants.USERS)
                .document(userId)
                .set(user, SetOptions.merge())
                .await()

            mFireStore.collection(Constants.USERS).document(userId)
                .collection(Constants.MY_CART)

            return true
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun getUserData(userId: String): User {
        val userDocument = mFireStore.collection(Constants.USERS)
            .document(userId)
            .get()
            .await()
        return userDocument.toObject(User::class.java) ?: User()
    }

    suspend fun updateUserData(userId: String, user: User) {
        try {
            mFireStore.collection(Constants.USERS)
                .document(userId)
                .set(user, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            throw e  // Rethrow the exception to handle it in the calling code
        }
    }

    suspend fun getContactUs(): ContactUs {
        try {
            val contactUsDocument = mFireStore.collection(Constants.CONTACT_US)
                .document("dJomedlYJ1O6FjouqUNP") // Replace with the actual document ID
                .get()
                .await()

            return contactUsDocument.toObject(ContactUs::class.java) ?: ContactUs()
        } catch (e: Exception) {
            // Handle the error
            Log.e("FirebaseRepository", "Error fetching contact us info: ${e.message}", e)
            throw e // Rethrow the exception to handle it in the calling code
        }
    }

    suspend fun getAllItems(): List<Item> {
        try {
            val itemsCollection = mFireStore.collection(Constants.ITEMS)
            val querySnapshot = itemsCollection.get().await()
            val itemList = mutableListOf<Item>()

            for (document in querySnapshot.documents) {
                val item = document.toObject(Item::class.java)
                item?.documentId = document.id
                item?.let { itemList.add(it) }
            }
            itemList.sortByDescending { it.nameItem }
            return itemList
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun toCart(userId: String, myCart: MyCart): Boolean {
        return try {
            val userDocumentRef = mFireStore.collection(Constants.USERS).document(userId)
            val myCartCollectionRef = userDocumentRef.collection(Constants.MY_CART)
            val myCartId = myCartCollectionRef.add(myCart).await().id
            myCart.documentId = myCartId // Assign the generated document ID to the item

            // Update the document in the collection with the same ID
            val myCartDocRef = myCartCollectionRef.document(myCartId)
            myCartDocRef.set(myCart, SetOptions.merge()).await()

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserCart(userId: String): List<MyCart> {
        try {
            val userDocumentRef = mFireStore.collection(Constants.USERS).document(userId)
                .collection(Constants.MY_CART)
            val querySnapshot = userDocumentRef.get().await()
            val myCartList = mutableListOf<MyCart>()

            for (document in querySnapshot.documents) {
                val myCart = document.toObject(MyCart::class.java)
                myCart?.documentId = document.id
                myCart?.let { myCartList.add(it) }
            }
            return myCartList
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun deleteFromCart(userId: String, documentId: String): Boolean {
        return try {
            val userDocumentRef = mFireStore.collection(Constants.USERS).document(userId)
            val myCartCollectionRef = userDocumentRef.collection(Constants.MY_CART)

            // Delete the item document from the user's cart collection
            myCartCollectionRef.document(documentId).delete().await()

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteCart(userId: String): Boolean {
        return try {
            val userDocumentRef = mFireStore.collection(Constants.USERS).document(userId)
            val myCartCollectionRef = userDocumentRef.collection(Constants.MY_CART).get().await()
            for (document in myCartCollectionRef.documents) {
                document.reference.delete().await()
            }

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateCartItemQuantity(
        userId: String, documentId: String, newQuantity: Int, newAmount: Int
    ): Boolean {
        return try {
            val userDocumentRef = mFireStore.collection(Constants.USERS).document(userId)
            val myCartCollectionRef = userDocumentRef.collection(Constants.MY_CART)

            myCartCollectionRef.document(documentId)
                .update(mapOf("quantity" to newQuantity, "amount" to newAmount))
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateCartItemPrice(
        userId: String, documentId: String, newItem: Item
    ): Boolean {
        return try {
            val userDocumentRef = mFireStore.collection(Constants.USERS).document(userId)
            val myCartCollectionRef = userDocumentRef.collection(Constants.MY_CART)
            myCartCollectionRef.document(documentId)
                .update(mapOf("itemProd" to newItem))
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun orderNow(userId: String, order: Order): Boolean {
        return try {

            val orderId = mFireStore.collection(Constants.ORDERS).document().id
            order.orderedId = orderId
            Log.d("YourTag", "Before creating orderDocumentRef: $orderId")

            val userDocumentRef = mFireStore.collection(Constants.USERS).document(userId)
            val myOrderCollectionRef = userDocumentRef.collection(Constants.MY_ORDERS)
            myOrderCollectionRef.document(orderId).set(order, SetOptions.merge()).await()

            val orderDocumentRef = mFireStore.collection(Constants.ORDERS).document(orderId)
            orderDocumentRef.set(order, SetOptions.merge()).await()

            true
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error while creating a board.", e)
            false
        }
    }

    suspend fun deleteMyOrder(userId: String, documentId: String): Boolean {
        return try {
            val userDocumentRef = mFireStore.collection(Constants.USERS).document(userId)
            val myOrderCollection = userDocumentRef.collection(Constants.MY_ORDERS)
            myOrderCollection.document(documentId).delete().await()
            true

        } catch (e: Exception) {
            false
        }
    }

    suspend fun getMyOrders(userId: String): List<Order> {
        try {
            val userDocumentRef = mFireStore.collection(Constants.USERS).document(userId)
                .collection(Constants.MY_ORDERS)
            val querySnapshot = userDocumentRef.get().await()
            val myOrderList = mutableListOf<Order>()

            for (document in querySnapshot.documents) {
                val order = document.toObject(Order::class.java)
                order?.orderedId = document.id
                order?.let { myOrderList.add(it) }
            }
            // Sort orders by date in descending order
            myOrderList.sortByDescending { it.date }
            return myOrderList
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun uploadUserImage(uri: Uri, fileName: String): String {
        return try {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("profileImages/$fileName")
            val uploadTask = imageRef.putFile(uri).await()

            val imageUrlTask = imageRef.downloadUrl.await()
            imageUrlTask.toString()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error uploading profileImages", e)
            throw e
        }
    }


    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
}