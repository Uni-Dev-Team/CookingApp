package com.unidevteam.cookingapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

@Suppress("DEPRECATION")
class SplashScreenActivity : AppCompatActivity() {
    private var SPLASH_SCREEN_TIME : Long = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.myLooper()!!).postDelayed({
            startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
            finish()
        }, SPLASH_SCREEN_TIME)
    }

}