package com.hfad.ansormarket.logInScreens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.hfad.ansormarket.MainActivity
import com.hfad.ansormarket.R
import com.hfad.ansormarket.SharedViewModel
import com.hfad.ansormarket.databinding.ActivityLogMainBinding
import com.hfad.ansormarket.firebase.FirebaseViewModel

class LogMainActivity : AppCompatActivity() {

    private val mSharedViewModel: SharedViewModel by viewModels()
    private lateinit var binding: ActivityLogMainBinding
    private val mFirebaseViewModel: FirebaseViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onBackPressed() {
        if (mSharedViewModel.doubleBackToExit()) {
            super.onBackPressed()
            Handler().postDelayed({ mSharedViewModel.doubleBackToExitPressedOnce = false }, 2000)
        }
    }

    fun intentRegLog() {

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        finish()
    }

    fun intentLog() {
        val currentUserID = mFirebaseViewModel.getCurrentUserId()
        if (currentUserID.isNotEmpty()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this, LogMainActivity::class.java))
            finish()
        }
        Log.d("TAG", "being called6")
        finish()
    }
}