package com.hfad.ansormarket

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hfad.ansormarket.databinding.ActivityMainBinding
import com.hfad.ansormarket.lanChange.MyContextWrapper
import com.hfad.ansormarket.lanChange.MyPreference

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var myPreference: MyPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        MobileAds.initialize(this) {}

        setSupportActionBar(binding.toolbar)
        setContentView(view)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val builder = AppBarConfiguration.Builder(navController.graph)
        val appBarConfiguration = builder.build()
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.bottomNav.setupWithNavController(navController)
        bottomNavigationView = findViewById(R.id.bottom_nav)

    }

    public override fun attachBaseContext(newbase: Context?) {
        myPreference = MyPreference(newbase!!)
        val lang: String? = myPreference.getLoginCount()
        super.attachBaseContext(MyContextWrapper.wrap(newbase, lang!!))
    }

    fun getBottomNavigationView(): BottomNavigationView {
        return bottomNavigationView
    }
}