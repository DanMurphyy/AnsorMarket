package com.hfad.ansormarket.firebase

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.hfad.ansormarket.models.Constants
import com.hfad.ansormarket.models.Item
import com.hfad.ansormarket.models.MyCart
import com.hfad.ansormarket.models.User
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    private val mFireStore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun registerUser(name: String, login: String, password: String): Boolean {
        try {
            // Firebase Authentication
            val authResult = FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(login, password)
                .await()
            // User registration in Firestore
            val userId = authResult.user?.uid ?: ""
            val user = User(userId, name, login)
            mFireStore.collection(Constants.USERS)
                .document(userId)
                .set(user, SetOptions.merge())
                .await()

            mFireStore.collection(Constants.USERS).document(userId)
                .collection(Constants.MY_CART)

            return true
        } catch (e: Exception) {
            Log.e("SignUpUser", "Error registering user", e)
            return false
        }
    }

    suspend fun createItem(item: Item): Boolean {
        return try {
            val itemDocumentRef = mFireStore.collection(Constants.ITEMS).document()
            val itemId = itemDocumentRef.id // Get the generated document ID
            item.documentId = itemId // Assign the generated document ID to the item
            itemDocumentRef.set(item, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error while creating a board.", e)
            false
        }
    }

    suspend fun signInUser(login: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(login, password).await()
            true
        } catch (e: Exception) {
            Log.e("SignInUser", "Error signing in user", e)
            false
        }
    }

    suspend fun getUserData(userId: String): User {
        val userDocument = mFireStore.collection(Constants.USERS)
            .document(userId)
            .get()
            .await()
        return userDocument.toObject(User::class.java) ?: User()
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
            Log.d("FirebaseRepository", "getAllItems: Successful. ${itemList.size} items fetched.")
            return itemList
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error retrieving items: ${e.message}", e)
            throw e
        }
    }

    suspend fun toCart(userId: String, myCart: MyCart): Boolean {
        return try {
            val userDocumentRef = mFireStore.collection(Constants.USERS).document(userId)
            val myCartCollectionRef = userDocumentRef.collection(Constants.MY_CART)
            val myCartId =
                myCartCollectionRef.add(myCart).await().id // Get the generated document ID
            myCart.documentId = myCartId // Assign the generated document ID to the item

            // Update the document in the collection with the same ID
            val myCartDocRef = myCartCollectionRef.document(myCartId)
            myCartDocRef.set(myCart, SetOptions.merge()).await()

            true
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error while adding to cart.", e)
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
            Log.d(
                "FirebaseRepository",
                "getAllItems: Successful. ${myCartList.size} items fetched."
            )

            return myCartList
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error retrieving user cart: ${e.message}", e)
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
            Log.e("FirestoreRepository", "Error while deleting item from cart.", e)
            false
        }
    }

    suspend fun updateCartItemQuantity(
        userId: String,
        documentId: String,
        newQuantity: Int,
        newAmount: Int
    ): Boolean {
        return try {
            val userDocumentRef = mFireStore.collection(Constants.USERS).document(userId)
            val myCartCollectionRef = userDocumentRef.collection(Constants.MY_CART)

            myCartCollectionRef.document(documentId)
                .update(mapOf("quantity" to newQuantity, "amount" to newAmount))
                .await()

            true
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error while deleting item from cart.", e)
            false
        }
    }

    suspend fun updateUserData(userId: String, user: User) {
        try {
            mFireStore.collection(Constants.USERS)
                .document(userId)
                .set(user, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error updating user profile data", e)
            throw e  // Rethrow the exception to handle it in the calling code
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

    suspend fun uploadItemImage(uri: Uri, fileName: String): String {
        return try {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("itemImages/$fileName")
            val uploadTask = imageRef.putFile(uri).await()

            val imageUrlTask = imageRef.downloadUrl.await()
            imageUrlTask.toString()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error uploading itemImages", e)
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