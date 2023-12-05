package com.dicoding.edival.ui.camera

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.dicoding.edival.R

class FoodResultFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnFood1 = view.findViewById<Button>(R.id.food1)
        val btnFood2 = view.findViewById<Button>(R.id.food2)
        val btnFood3 = view.findViewById<Button>(R.id.food3)

        btnFood1.setOnClickListener {
            Toast.makeText(requireContext(), "Food 1", Toast.LENGTH_LONG).show()
            dismiss()
        }

        btnFood2.setOnClickListener {
            Toast.makeText(requireContext(), "Food 2", Toast.LENGTH_LONG).show()
            dismiss()
        }

        btnFood3.setOnClickListener {
            Toast.makeText(requireContext(), "Food 3", Toast.LENGTH_LONG).show()
            dismiss()
        }
    }

}