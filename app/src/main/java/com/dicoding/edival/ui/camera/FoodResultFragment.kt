package com.dicoding.edival.ui.camera

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        val recyclerView: RecyclerView = view.findViewById(R.id.rv_main)
        val detectedFoodList = arguments?.getStringArrayList("detectedFoodList") ?: emptyList()

        val foodResultAdapter = FoodResultAdapter(detectedFoodList) { selectedFood ->
            Toast.makeText(requireContext(), selectedFood, Toast.LENGTH_LONG).show()
            dismiss()
        }

        recyclerView.adapter = foodResultAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

}