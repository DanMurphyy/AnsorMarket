package com.hfad.ansormarket.logInScreens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hfad.ansormarket.databinding.ActivityLogMainBinding

class LogMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}