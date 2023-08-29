package com.hfad.ansormarket

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.hfad.ansormarket.databinding.ActivityMainBinding
import com.hfad.ansormarket.logInScreens.LogMainActivity

class MainActivity : AppCompatActivity() {

    private val mSharedViewModel: SharedViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }


}