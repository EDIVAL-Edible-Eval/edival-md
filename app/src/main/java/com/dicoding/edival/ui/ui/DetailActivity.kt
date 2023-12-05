package com.dicoding.edival.ui.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.edival.databinding.ActivityDetailsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        calendar = Calendar.getInstance()

        binding.simpan2.setOnClickListener {
            showDatePickerDialog(binding.simpan2)
        }

        binding.exp2.setOnClickListener {
            showDatePickerDialog(binding.exp2)
        }

        binding.btnBack.setOnClickListener{
            onBackPressed()
        }
    }

    private fun showDatePickerDialog(selectedButton: Button) {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            updateDateInView(selectedButton)
        }

        val datePickerDialog = DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun updateDateInView(selectedButton: Button) {
        val myFormat = "dd/MM/yyyy" // customize the format as you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        selectedButton.text = sdf.format(calendar.time)
    }
}
