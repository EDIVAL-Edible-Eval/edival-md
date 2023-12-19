package com.dicoding.edival.ui.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.dicoding.edival.adapters.VerticalDateAdapter
import com.dicoding.edival.databinding.FragmentHomeBinding
@RequiresApi(Build.VERSION_CODES.O)
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val adapterList by lazy { VerticalDateAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        Log.d("Masuk Home", "iya")
        adapterList.updateListDate()
        return binding.root
    }

    override fun onResume() {
        adapterList.updateListDate()
        super.onResume()
    }
}
