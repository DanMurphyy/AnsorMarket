package com.hfad.ansormarket.firebase

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.hfad.ansormarket.models.Constants
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
            true
        } catch (e: Exception) {
            Log.e("SignInUser", "Error signing in user", e)
            false
        }
    }

    suspend fun getUserData(userId: String): User {
        val userDocument = mfireStore.collection(Constants.USERS)
            .document(userId)
            .get()
            .await()
        return userDocument.toObject(User::class.java) ?: User()
    }

    suspend fun updateUserData(userId: String, user: User) {
        try {
            mfireStore.collection(Constants.USERS)
                .document(userId)
                .set(user, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error updating user profile data", e)
            throw e  // Rethrow the exception to handle it in the calling code
        }
    }

    suspend fun uploadImage(uri: Uri, fileName: String): String {
        return try {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("images/$fileName")
            val uploadTask = imageRef.putFile(uri).await()

            val imageUrlTask = imageRef.downloadUrl.await()
            imageUrlTask.toString()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error uploading image", e)
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