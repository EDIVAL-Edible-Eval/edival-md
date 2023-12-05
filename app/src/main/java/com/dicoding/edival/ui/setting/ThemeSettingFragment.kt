package com.dicoding.edival.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.dicoding.edival.R
import com.dicoding.edival.data.pref.ThemePreferences
import com.dicoding.edival.data.pref.dataStore
import kotlinx.coroutines.launch

class ThemeSettingFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_theme_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnDark = view.findViewById<Button>(R.id.set_dark)
        val btnLight = view.findViewById<Button>(R.id.set_light)

        btnDark.setOnClickListener{
            saveThemeSetting(true)
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            dismiss()
        }

        btnLight.setOnClickListener{
            saveThemeSetting(false)
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            dismiss()
        }
    }

    private fun saveThemeSetting(isDarkModeActive: Boolean) {
        val themePreferences = ThemePreferences.getInstance(requireContext().dataStore)
        lifecycleScope.launch {
            themePreferences.saveThemeSetting(isDarkModeActive)
        }
        dismiss()
    }
}