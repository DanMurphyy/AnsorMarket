package com.hfad.ansormarket.logInScreens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hfad.ansormarket.MainActivity
import com.hfad.ansormarket.databinding.ActivityLogMainBinding

class LogMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun intentLog() {
        // Sign-in successful, navigate to the main activity
        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT)
            .show()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        finish()
    }
}