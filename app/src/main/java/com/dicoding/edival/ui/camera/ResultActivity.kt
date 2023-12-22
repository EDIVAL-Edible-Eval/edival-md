package com.dicoding.edival.ui.camera

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dicoding.edival.databinding.ActivityRecommendationBinding
import com.dicoding.edival.databinding.ActivityResultBinding
import com.dicoding.edival.ui.camera.FoodResultFragment

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    private var capturedPhotoPath: String? = null
    private var detectedFoodList: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        capturedPhotoPath = intent.getStringExtra("capturedPhotoPath")
        detectedFoodList = intent.getStringArrayListExtra("detectedFoodList")

        loadAndSetImage()

        binding.backarrow.setOnClickListener{
            onBackPressed()
        }

        binding.btnResult.setOnClickListener {
            val foodResultFragment = FoodResultFragment()
            foodResultFragment.arguments = Bundle().apply {
                putStringArrayList("detectedFoodList", detectedFoodList)
            }
            foodResultFragment.show(supportFragmentManager, "theme pop up")

            val intent = Intent(applicationContext, DetailResultActivity::class.java).apply {
                putExtra("capturedPhotoPath", capturedPhotoPath)
                putStringArrayListExtra("detectedFoodList", detectedFoodList)
            }
            startActivity(intent)
            finish() // Optional: finish the current activity if you don't need it anymore
        }
    }

    private fun loadAndSetImage() {

        // Load the image from the capturedPhotoPath and set it in binding.imageRecommen
        if (capturedPhotoPath != null) {
            Glide.with(this)
                .load(capturedPhotoPath)
                .into(binding.imageRecommen)
        }
    }
}