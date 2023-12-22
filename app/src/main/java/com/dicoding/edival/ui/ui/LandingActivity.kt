package com.dicoding.edival.ui.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.edival.databinding.ActivityLandingBinding
import com.dicoding.edival.ui.tutorial.TutorialActivity

class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener {
            startActivity(Intent(this, TutorialActivity::class.java))
        }
    }
}