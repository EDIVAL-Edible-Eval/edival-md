package com.dicoding.edival.ui.camera

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.edival.databinding.ActivityDetailResultBinding

class DetailResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvReview.setOnClickListener{
            Intent(applicationContext, RecommendationActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}