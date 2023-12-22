package com.dicoding.edival.ui.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.dicoding.edival.data.pref.ThemePreferences
import com.dicoding.edival.data.pref.dataStore
import com.dicoding.edival.databinding.ActivitySettingBinding
import com.dicoding.edival.ui.login.loginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var auth: FirebaseAuth
    private val viewModel by viewModels<SettingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        viewModel.loadUserData(auth)

        viewModel.user.observe(this){user ->
            if (user != null) {
                binding.tvName.text = user.displayName
                binding.tvEmail.text = user.email

                viewModel.loadUserImage(binding.imageUser, user)
            }
        }

        binding.backarrow.setOnClickListener{
            onBackPressed()
        }

        val pref = ThemePreferences.getInstance(application.dataStore)
        pref.getThemeSetting().onEach { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }.launchIn(lifecycleScope)

        binding.btnEditProfile.setOnClickListener {
            Intent(applicationContext, EditProfileActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.btnTheme.setOnClickListener {
            val showThemeSetting = ThemeSettingFragment()
            showThemeSetting.show(supportFragmentManager, "theme pop up")
        }

        binding.btnLogout.setOnClickListener{
            val intent = Intent(this, loginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            auth.signOut()
            finish()
        }
    }
}