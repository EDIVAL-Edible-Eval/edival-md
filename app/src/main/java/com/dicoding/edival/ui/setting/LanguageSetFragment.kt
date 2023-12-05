package com.dicoding.edival.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.dicoding.edival.R


class LanguageSetFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_language_set, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnEng = view.findViewById<Button>(R.id.set_eng)
        val btnInd = view.findViewById<Button>(R.id.set_ind)

        btnEng.setOnClickListener {
            Toast.makeText(requireContext(), "English", Toast.LENGTH_LONG).show()
            dismiss()
        }

        btnInd.setOnClickListener {
            Toast.makeText(requireContext(), "English", Toast.LENGTH_LONG).show()
            dismiss()
        }
    }

}