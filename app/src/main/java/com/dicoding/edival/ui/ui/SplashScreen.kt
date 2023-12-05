package com.dicoding.edival.ui.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.edival.databinding.ActivitySplashScreenBinding
import com.dicoding.edival.ui.login.LoginActivity
import com.dicoding.edival.ui.tutorial.TutorialActivity

class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler().postDelayed({
            startActivity(Intent(this, TutorialActivity::class.java))
            finish()
        }, 1500)
    }
}
