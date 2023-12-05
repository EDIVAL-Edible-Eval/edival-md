package com.dicoding.edival.ui.camera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.edival.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnResult.setOnClickListener {
                val showThemeSetting = FoodResultFragment()
                showThemeSetting.show(supportFragmentManager, "theme pop up")
//            Intent(applicationContext, DetailResultActivity::class.java).also {
//                startActivity(it)
            }
        }
    }
