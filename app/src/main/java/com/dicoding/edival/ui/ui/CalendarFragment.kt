package com.dicoding.edival.ui.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.edival.adapter.ReminderAdapter
import com.dicoding.edival.adapter.ReminderPastAdapter
import com.dicoding.edival.data.db.Reminder
import com.dicoding.edival.databinding.FragmentCalendarBinding
import com.dicoding.edival.ui.setting.SettingActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.util.Calendar

class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var adapterFuture: ReminderAdapter
    private lateinit var adapterPast: ReminderPastAdapter
    private lateinit var reminderList: ArrayList<Reminder>
    private lateinit var reminderPastList: ArrayList<Reminder>
    private var db = Firebase.firestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        val view = binding.root
        // Mengatur click listener untuk FloatingActionButton
        binding.floatingActionButton.setOnClickListener {
            // Intent untuk beralih ke CreateActivity
            val intent = Intent(activity, CreateActivity::class.java)
            startActivity(intent)
        }
        binding.btnSetting.setOnClickListener {
            val intent = Intent(activity, SettingActivity::class.java)
            startActivity(intent)
        }
        binding.btnNotif.setOnClickListener {
            // Intent untuk beralih ke NotificationActivity
            val intent = Intent(activity, NotificationActivity::class.java)
            startActivity(intent)
        }

        // Mendapatkan tanggal bulan dan tahun saat ini
        val currentDate = Calendar.getInstance()
        val currentDay = currentDate.get(Calendar.DAY_OF_MONTH)
        val currentMonth = currentDate.get(Calendar.MONTH) + 1  // Januari dimulai dari 0
        val currentYear = currentDate.get(Calendar.YEAR)

        val userId = firebaseAuth.currentUser!!.uid

// Query untuk data yang belum dilewati
        val futureQuery = db.collection("users").document(userId)
            .collection("reminders").whereGreaterThan("exp_date", "$currentYear-$currentMonth-$currentDay")

// Query untuk data yang sudah dilewati
        val pastQuery = db.collection("users").document(userId)
            .collection("reminders").whereLessThanOrEqualTo("exp_date", "$currentYear-$currentMonth-$currentDay")

        binding.rvMain.layoutManager = LinearLayoutManager(activity)
        binding.rvMai2.layoutManager = LinearLayoutManager(activity)

         reminderList = arrayListOf()
         reminderPastList = arrayListOf()

        // Mengatur adapter untuk RecyclerView yang menampilkan data yang belum dilewati
        futureQuery.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                val reminder: Reminder = document.toObject(Reminder::class.java)!!
                if (!document.contains("status")) {
                    document.reference.update("status", "fresh")
                }
                reminderList.add(reminder)
            }
            adapterFuture = ReminderAdapter(reminderList)
            binding.rvMain.adapter = adapterFuture
        }.addOnFailureListener {
            Toast.makeText(activity, "Failed to load data", Toast.LENGTH_SHORT).show()
        }

// Mengatur adapter untuk RecyclerView yang menampilkan data yang sudah dilewati
        pastQuery.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                val reminder: Reminder = document.toObject(Reminder::class.java)!!
                document.reference.update("status", "rotten")
                reminderPastList.add(reminder)
            }
            adapterPast = ReminderPastAdapter(reminderPastList)
            binding.rvMai2.adapter = adapterPast
        }.addOnFailureListener {
            Toast.makeText(activity, "Failed to load data", Toast.LENGTH_SHORT).show()
        }
        return view
    }
}
