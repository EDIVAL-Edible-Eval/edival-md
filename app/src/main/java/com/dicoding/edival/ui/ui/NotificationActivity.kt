package com.dicoding.edival.ui.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.edival.adapter.MainReminderAdapter
import com.dicoding.edival.adapter.NotifAdapter
import com.dicoding.edival.adapter.ReminderAdapter
import com.dicoding.edival.data.db.Reminder
import com.dicoding.edival.databinding.ActivityNotificationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var reminderList: ArrayList<Reminder>
    private lateinit var adapter: NotifAdapter
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener{
            onBackPressed()
        }


        fetchReminders()

    }
    private fun fetchReminders() {
        val userId = firebaseAuth.currentUser!!.uid

        // Mendapatkan tanggal hari ini
        val currentDate = Calendar.getInstance()
        val twoDaysAgo = Calendar.getInstance()

        val startDate = currentDate.time
        twoDaysAgo.add(Calendar.DAY_OF_MONTH, 2)
        val endDate = twoDaysAgo.time

        // Membuat query untuk mengambil data dari subkoleksi 'notification' dengan kondisi exp_date berjarak 2 hari sebelumnya
        val docRef = db.collection("users")
            .document(userId)
            .collection("notification")
            .whereGreaterThanOrEqualTo("exp_date", SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(startDate))
            .whereLessThanOrEqualTo("exp_date", SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(endDate))

        binding.rvMain.layoutManager = LinearLayoutManager(this)
        reminderList = arrayListOf()

        docRef.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                val reminder: Reminder = document.toObject(Reminder::class.java)!!
                reminderList.add(reminder)
            }
            adapter = NotifAdapter(reminderList)
            binding.rvMain.adapter = adapter
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show()
        }
    }
}
