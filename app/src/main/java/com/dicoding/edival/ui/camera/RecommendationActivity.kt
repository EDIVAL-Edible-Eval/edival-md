package com.dicoding.edival.ui.camera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.edival.databinding.ActivityRecommendationBinding

class RecommendationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecommendationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backarrow.setOnClickListener {
            onBackPressed()
        }
    }
}