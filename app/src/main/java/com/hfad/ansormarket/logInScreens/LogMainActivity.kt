package com.hfad.ansormarket.logInScreens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.hfad.ansormarket.MainActivity
import com.hfad.ansormarket.R
import com.hfad.ansormarket.SharedViewModel
import com.hfad.ansormarket.databinding.ActivityLogMainBinding

class LogMainActivity : AppCompatActivity() {

    private val mSharedViewModel: SharedViewModel by viewModels()
    private lateinit var binding: ActivityLogMainBinding

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

    fun intentLog() {
        // Sign-in successful, navigate to the main activity
        Toast.makeText(this, getString(R.string.login_successful), Toast.LENGTH_SHORT)
            .show()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        finish()
    }
}