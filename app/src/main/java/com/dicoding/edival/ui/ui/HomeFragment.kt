package com.dicoding.edival.ui.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.edival.adapter.MainReminderAdapter
import com.dicoding.edival.adapter.ReminderAdapter
import com.dicoding.edival.data.db.Reminder
import com.dicoding.edival.databinding.FragmentHomeBinding
import com.dicoding.edival.ui.setting.SettingActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var db = Firebase.firestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var adapter: MainReminderAdapter
    private lateinit var reminderList: ArrayList<Reminder>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        firebaseAuth = FirebaseAuth.getInstance()

        binding.set.setOnClickListener {
            val intent = Intent(activity, SettingActivity::class.java)
            startActivity(intent)
        }
        binding.notif.setOnClickListener {
            // Intent untuk beralih ke NotificationActivity
            val intent = Intent(activity, NotificationActivity::class.java)
            startActivity(intent)
        }
        setDateToTextViews()
        fetchAndDisplayGoodCount()
        fetchAndDisplayRottenCount()
        fetchAndDisplayVegetableCount()
        fetchAndDisplayFruitCount()
        fetchAndDisplayMeatCount()
        fetchAndDisplayOthersCount()
        fetchAndDisplayAlarmCount()

        val calendarStart = Calendar.getInstance()
        val calendarEnd = Calendar.getInstance()

        // Tanggal mulai (tanggal saat ini)
        val startDate = calendarStart.time

        // Tanggal akhir (3 hari ke depan)
        calendarEnd.add(Calendar.DATE, 7)
        val endDate = calendarEnd.time

        val userId = firebaseAuth.currentUser!!.uid
        val docRef = db.collection("users")
            .document(userId)
            .collection("reminders")
            .whereGreaterThanOrEqualTo("exp_date", SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(startDate))
            .whereLessThanOrEqualTo("exp_date", SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(endDate))

        binding.rvMain.layoutManager = LinearLayoutManager(activity)
        reminderList = arrayListOf()

        docRef.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                val reminder: Reminder = document.toObject(Reminder::class.java)!!
                reminderList.add(reminder)
            }
            adapter = MainReminderAdapter(reminderList)
            binding.rvMain.adapter = adapter
        }.addOnFailureListener {
            Toast.makeText(activity, "Failed to load data", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun fetchAndDisplayAlarmCount() {
        val userId = firebaseAuth.currentUser!!.uid
        val calendarStart = Calendar.getInstance()
        val calendarEnd = Calendar.getInstance()

        // Tanggal mulai (tanggal saat ini)
        val startDate = calendarStart.time

        // Tanggal akhir (3 hari ke depan)
        calendarEnd.add(Calendar.DATE, 3)
        val endDate = calendarEnd.time

        val docRef = db.collection("users")
            .document(userId)
            .collection("reminders")
            .whereGreaterThanOrEqualTo("exp_date", SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(startDate))
            .whereLessThanOrEqualTo("exp_date", SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(endDate))

        docRef.get().addOnSuccessListener { documents ->
            val count = documents.size()
            binding.alarm.text = count.toString()  // Menampilkan jumlah data pada TextView alarm
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting documents: ", exception)
        }
    }

    private fun fetchAndDisplayFruitCount() {
        val userId = firebaseAuth.currentUser!!.uid
        val docRef = db.collection("users")
            .document(userId)
            .collection("reminders")
            .whereEqualTo("type", "Fruit")
            .whereEqualTo("status", "fresh")

        docRef.get().addOnSuccessListener { documents ->
            val count = documents.size()
            binding.fruit.text = count.toString()  // Menampilkan jumlah data pada TextView vegetable
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting documents: ", exception)
        }
    }
    private fun fetchAndDisplayMeatCount() {
        val userId = firebaseAuth.currentUser!!.uid
        val docRef = db.collection("users")
            .document(userId)
            .collection("reminders")
            .whereEqualTo("type", "Meat")
            .whereEqualTo("status", "fresh")

        docRef.get().addOnSuccessListener { documents ->
            val count = documents.size()
            binding.meat.text = count.toString()  // Menampilkan jumlah data pada TextView vegetable
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting documents: ", exception)
        }
    }
    private fun fetchAndDisplayOthersCount() {
        val userId = firebaseAuth.currentUser!!.uid
        val docRef = db.collection("users")
            .document(userId)
            .collection("reminders")
            .whereEqualTo("type", "Others")
            .whereEqualTo("status", "fresh")

        docRef.get().addOnSuccessListener { documents ->
            val count = documents.size()
            binding.others.text = count.toString()  // Menampilkan jumlah data pada TextView vegetable
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting documents: ", exception)
        }
    }
    private fun fetchAndDisplayVegetableCount() {
        val userId = firebaseAuth.currentUser!!.uid
        val docRef = db.collection("users")
            .document(userId)
            .collection("reminders")
            .whereEqualTo("type", "Vegetable")
            .whereEqualTo("status", "fresh")

        docRef.get().addOnSuccessListener { documents ->
            val count = documents.size()
            binding.vegetable.text = count.toString()  // Menampilkan jumlah data pada TextView vegetable
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting documents: ", exception)
        }
    }
    private fun fetchAndDisplayGoodCount() {
        val userId = firebaseAuth.currentUser!!.uid
        val docRef = db.collection("users")
            .document(userId)
            .collection("reminders")
            .whereEqualTo("status", "fresh")

        docRef.get().addOnSuccessListener { documents ->
            val count = documents.size()
            binding.good.text = count.toString()
            binding.progressBar.setProgress(count)
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting documents: ", exception)
        }
    }
    private fun fetchAndDisplayRottenCount() {
        val userId = firebaseAuth.currentUser!!.uid
        val docRef = db.collection("users")
            .document(userId)
            .collection("reminders")
            .whereEqualTo("status", "rotten")

        docRef.get().addOnSuccessListener { documents ->
            val count = documents.size()
            binding.rotten.text = count.toString()  // Menampilkan jumlah data pada TextView rotten
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting documents: ", exception)
        }
    }
    private fun setDateToTextViews() {
        val calendar = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd", Locale.getDefault())
        val sdf2 = SimpleDateFormat("E", Locale.ENGLISH)

        binding.date4.text = sdf.format(calendar.time)
        binding.day4.text = sdf2.format(calendar2.time)

        calendar.add(Calendar.DATE, -1)
        calendar2.add(Calendar.DATE, -1)
        binding.date3.text = sdf.format(calendar.time)
        binding.day3.text = sdf2.format(calendar2.time)

        calendar.add(Calendar.DATE, -1)
        calendar2.add(Calendar.DATE, -1)
        binding.date2.text = sdf.format(calendar.time)
        binding.day2.text = sdf2.format(calendar2.time)

        calendar.add(Calendar.DATE, -1)
        calendar2.add(Calendar.DATE, -1)
        binding.date1.text = sdf.format(calendar.time)
        binding.day1.text = sdf2.format(calendar2.time)

        calendar.time = Calendar.getInstance().time
        calendar2.time = Calendar.getInstance().time

        calendar.add(Calendar.DATE, 1)
        calendar2.add(Calendar.DATE, 1)
        binding.date5.text = sdf.format(calendar.time)
        binding.day5.text = sdf2.format(calendar2.time)

        calendar.add(Calendar.DATE, 1)
        calendar2.add(Calendar.DATE, 1)
        binding.date6.text = sdf.format(calendar.time)
        binding.day6.text = sdf2.format(calendar2.time)

        calendar.add(Calendar.DATE, 1)
        calendar2.add(Calendar.DATE, 1)
        binding.date7.text = sdf.format(calendar.time)
        binding.day7.text = sdf2.format(calendar2.time)
    }
}
