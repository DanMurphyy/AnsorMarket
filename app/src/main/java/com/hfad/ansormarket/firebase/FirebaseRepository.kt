package com.hfad.ansormarket.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.hfad.ansormarket.models.Constants
import com.hfad.ansormarket.models.Item
import com.hfad.ansormarket.models.User
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    private val mfireStore = FirebaseFirestore.getInstance()
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
            mfireStore.collection(Constants.USERS)
                .document(userId)
                .set(user, SetOptions.merge())
                .await()
            return true
        } catch (e: Exception) {
            Log.e("SignUpUser", "Error registering user", e)
            return false
        }
    }


    suspend fun signInUser(login: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(login, password).await()
            val loggedInUserId = getCurrentUserId()
            mfireStore.collection(Constants.USERS)
                .document(loggedInUserId)
                .get()
                .addOnSuccessListener { document ->
                    val loggedInUser = document.toObject(User::class.java)
                }
                .addOnFailureListener {

                }
            true
        } catch (e: Exception) {
            Log.e("SignInUser", "Error signing in user", e)
            false
        }
    }


    suspend fun createBoard(item: Item): Boolean {
        return try {
            mfireStore.collection(Constants.ITEMS)
                .document()
                .set(item, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error while creating a board.", e)
            false
        }
    }

    suspend fun getItemList(): List<Item> {
        return try {
            val querySnapshot = mfireStore.collection(Constants.ITEMS)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { documentSnapshot ->
                val item = documentSnapshot.toObject(Item::class.java)
                item?.documentId = documentSnapshot.id
                item
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error reading documents", e)
            emptyList()
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
