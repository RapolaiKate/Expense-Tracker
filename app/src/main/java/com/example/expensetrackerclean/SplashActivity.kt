package com.example.expensetrackerclean

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity: AppCompatActivity() {

    private val SPLASH_TIME: Long = 2000  // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // ===== INITIALIZE LOGO =====
        val logo = findViewById<ImageView>(R.id.logoImage)

        // ===== LOAD FADE-IN ANIMATION =====
       logo.animate()
           .alpha(1f)
           .setDuration(1500)
           .start()

        // ===== GO TO MAIN ACTIVITY =====
        Handler(Looper.getMainLooper()).postDelayed( {
            startActivity(Intent(this, MainActivity::class.java))
                finish()
        }, SPLASH_TIME)
    }
}