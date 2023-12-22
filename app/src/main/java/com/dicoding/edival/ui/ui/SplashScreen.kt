package com.dicoding.edival.ui.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.dicoding.edival.R
import com.dicoding.edival.data.pref.ThemePreferences
import com.dicoding.edival.data.pref.dataStore
import com.dicoding.edival.databinding.ActivitySplashScreenBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SplashScreen : AppCompatActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            checkUserLoginStatus()
        }, 2000)

        val pref = ThemePreferences.getInstance(application.dataStore)
        pref.getThemeSetting().onEach { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }.launchIn(lifecycleScope)

    }
    private fun checkUserLoginStatus() {
        if (firebaseAuth.currentUser != null) {
            // Pengguna sudah login, arahkan ke MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // Pengguna belum login, arahkan ke TutorialActivity
            startActivity(Intent(this, LandingActivity::class.java))
        }
        finish()
    }
}
