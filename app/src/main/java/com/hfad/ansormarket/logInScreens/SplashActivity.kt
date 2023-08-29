package com.hfad.ansormarket.logInScreens

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.hfad.ansormarket.MainActivity
import com.hfad.ansormarket.R
import com.hfad.ansormarket.firebase.FirebaseRepository
import com.hfad.ansormarket.firebase.FirebaseViewModel

class SplashActivity : AppCompatActivity() {

    private val mFirebaseViewModel: FirebaseViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        gif2()
        gif1()
        animateImageView2()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        Handler(Looper.getMainLooper()).postDelayed({
            val currentUserID = mFirebaseViewModel.getCurrentUserId()
            if (currentUserID.isNotEmpty()) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, LogMainActivity::class.java))
                finish()
            }
        }, 3000)

    }

    fun gif1() {
        val gifImageView: ImageView = findViewById(R.id.delivering_iv)
        Glide.with(this)
            .asGif()
            .load(R.drawable.grocery) // Assuming "fire.gif" is the name of your animated GIF file
            .into(gifImageView)
    }

    fun gif2() {
        val gifImageView: ImageView = findViewById(R.id.delivering_iv2)
        Glide.with(this)
            .asGif()
            .load(R.drawable.deliverying) // Assuming "fire.gif" is the name of your animated GIF file
            .into(gifImageView)
    }

    private fun animateImageView2() {
        val imageView2: ImageView = findViewById(R.id.delivering_iv2)

        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val distanceToMove = screenWidth + dpToPx(150) - imageView2.width
        val animator = ObjectAnimator.ofFloat(
            imageView2, "translationX", 0f, distanceToMove
        )
        animator.duration = 3500
        animator.start()
    }

    private fun dpToPx(dp: Int): Float {
        val density = resources.displayMetrics.density
        return dp * density
    }
}