package com.dicoding.edival.ui.camera

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dicoding.edival.databinding.ActivityDetailResultBinding

class DetailResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val capturedPhotoPath = intent.getStringExtra("capturedPhotoPath")
        val detectedFoodList = intent.getStringArrayListExtra("detectedFoodList")

        binding.backarrow.setOnClickListener {
            onBackPressed()
        }

        // Load the image from the capturedPhotoPath and set it in binding.imageRecommen
        Glide.with(this)
            .load(capturedPhotoPath)
            .into(binding.imageRecommen)

        // Set the detected food label in the foodName TextView
        binding.foodName.text = detectedFoodList?.getOrNull(0) ?: "Tidak Terdeteksi Makanan"

    }
}