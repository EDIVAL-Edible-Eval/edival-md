package com.dicoding.edival.ui.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dicoding.edival.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val view = binding.root
        // Mengatur click listener untuk FloatingActionButton
        binding.floatingActionButton.setOnClickListener {
            // Intent untuk beralih ke CreateActivity
            val intent = Intent(activity, CreateActivity::class.java)
            startActivity(intent)
        }
        binding.btnNotif.setOnClickListener {
            // Intent untuk beralih ke NotificationActivity
            val intent = Intent(activity, NotificationActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}
