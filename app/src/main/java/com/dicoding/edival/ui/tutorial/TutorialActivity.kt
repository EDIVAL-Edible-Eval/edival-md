package com.dicoding.edival.ui.tutorial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.dicoding.edival.R
import com.dicoding.edival.data.api.response.UserResponse
import com.dicoding.edival.data.api.retrofit.ApiConfig
import com.dicoding.edival.data.pref.ThemePreferences
import com.dicoding.edival.data.pref.dataStore
import com.dicoding.edival.databinding.ActivityTutorialBinding
import com.dicoding.edival.ui.login.LoginActivity
import com.dicoding.edival.ui.setting.SettingActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TutorialActivity : AppCompatActivity() {

    private lateinit var tutorialSlideAdapter: TutorialSlideAdapter
    private lateinit var binding: ActivityTutorialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = ThemePreferences.getInstance(application.dataStore)
        pref.getThemeSetting().onEach { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }.launchIn(lifecycleScope)

        tutorialSlideAdapter = TutorialSlideAdapter(
            listOf(
                TutorialSlide(
                    getString(R.string.tutor1),
                    getString(R.string.tutor1_desc),
                    R.drawable.tutorial1
                ),
                TutorialSlide(
                    getString(R.string.tutor2),
                    getString(R.string.tutor2_desc),
                    R.drawable.tutorial2
                ),
                TutorialSlide(
                    getString(R.string.tutor3),
                    getString(R.string.tutor3_desc),
                    R.drawable.tutorial3
                ),
            )
        )
        binding.tutorialSliderViewPager.adapter = tutorialSlideAdapter

        setupIndicators()
        setCurrentIndicator(0)
        binding.tutorialSliderViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        binding.buttonNext.setOnClickListener {
            if (binding.tutorialSliderViewPager.currentItem + 1 < tutorialSlideAdapter.itemCount) {
                binding.tutorialSliderViewPager.currentItem += 1
            } else {
                Intent(applicationContext, LoginActivity::class.java).also {
                    startActivity(it)
                }
            }

            val client = ApiConfig.getApiService().getUserProfile(id = "dZz4l4F91Yexz4yN2shBHvoPwzx1")
            client.enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    Log.println(Log.WARN, "Tes API", response.body().toString())
                }
                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }
            })


        }

        binding.skip.setOnClickListener {
            Intent(applicationContext, SettingActivity::class.java).also {
                startActivity(it)
            }
        }

    }

    private fun setupIndicators(){
        val indicators = arrayOfNulls<ImageView>(tutorialSlideAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8,0,8,0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            binding.indicatorContainer.addView(indicators[i])
        }
    }
    private fun setCurrentIndicator(indext: Int) {
        val childCount = binding.indicatorContainer.childCount
        for(i in 0 until childCount) {
            val imageView = binding.indicatorContainer[i] as ImageView
            if (i == indext) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }
}


