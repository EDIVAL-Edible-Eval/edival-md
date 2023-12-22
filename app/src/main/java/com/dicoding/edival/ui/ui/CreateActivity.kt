package com.dicoding.edival.ui.ui

import android.app.DatePickerDialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.dicoding.edival.R
import com.dicoding.edival.databinding.ActivityCreateBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class CreateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateBinding
    private lateinit var calendar: Calendar
    private var currentImageUri: Uri? = null
    private var db = Firebase.firestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.proBarCreate.visibility = View.GONE

        firebaseAuth = FirebaseAuth.getInstance()


        calendar = Calendar.getInstance()

        binding.btnFoodPhoto.setOnClickListener{ startGallery()}

        binding.simpan2.setOnClickListener {
            showDatePickerDialog(binding.simpan2)
        }

        binding.exp2.setOnClickListener {
            showDatePickerDialog(binding.exp2)
        }

        binding.btnBack.setOnClickListener{
            onBackPressed()
        }

        val type = resources.getStringArray(R.array.foodType)
        val arrayAdapter = ArrayAdapter(this, R.layout.type_item, type)
        val autocompleteTV = binding.autoComplete
        autocompleteTV.setAdapter(arrayAdapter)

        binding.btnSave.setOnClickListener {
            binding.proBarCreate.visibility = View.VISIBLE
            uploadImageToFirebaseStorage()
        }


    }

    private fun uploadImageToFirebaseStorage() {
        if (currentImageUri != null) {
            val filename = UUID.randomUUID().toString() // Nama unik untuk file gambar
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

            ref.putFile(currentImageUri!!)
                .addOnSuccessListener {
                    Log.d("CreateActivity", "Successfully uploaded image: ${it.metadata?.path}")
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        saveDataToFirestore(uri.toString()) // Simpan URL gambar ke Firestore
                    }
                }
                .addOnFailureListener {
                    Log.e("CreateActivity", "Failed to upload image", it)
                }
        }
    }
    private fun saveDataToFirestore(imageUrl: String) {
        binding.proBarCreate.visibility = View.VISIBLE

        val fName = binding.foodName2.text.toString().trim()
        val fStorage = binding.storage2.text.toString().trim()
        val fStore = binding.simpan2.text.toString().trim()
        val fExp = binding.exp2.text.toString().trim()
        val fType = binding.autoComplete.text.toString().trim()

        val today = Calendar.getInstance()
        val expDate = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        expDate.time = sdf.parse(fExp) ?: Date()



        db.collection("users").document(firebaseAuth.currentUser!!.uid).collection("reminders")
            .orderBy("documentId", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                var newId = 1 // Default ID jika koleksi masih kosong

                if (!documents.isEmpty) {
                    val lastDocument = documents.documents[0]
                    newId = lastDocument.getLong("documentId")?.toInt() ?: 0
                    newId++ // Increment ID untuk dokumen baru
                }
                val value = if (expDate.before(today) || expDate == today) "rotten" else "fresh"
                val foodMap = hashMapOf(
                    "documentId" to newId,
                    "name" to fName,
                    "storage_type" to fStorage,
                    "store_date" to fStore,
                    "exp_date" to fExp,
                    "type" to fType,
                    "status" to value,
                    "img_path" to imageUrl
                )
                val userId = firebaseAuth.currentUser!!.uid

                db.collection("users").document(userId).collection("reminders").add(foodMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Successfully Added!", Toast.LENGTH_SHORT).show()
                        binding.foodName2.text.clear()
                        binding.storage2.text.clear()
                        binding.autoComplete.text.clear()
                        binding.exp2.text = ""
                        binding.simpan2.text = ""
                        binding.btnFoodPhoto.setImageResource(R.drawable.plus)
                        binding.proBarCreate.visibility = View.GONE
                    }.addOnFailureListener {
                        Toast.makeText(this, "Failed!!!", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { exception ->
                        Log.w("Firestore", "Error getting documents: ", exception)
                        Toast.makeText(this, "Failed to retrieve ID!", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                Toast.makeText(this, "Failed to upload data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.btnFoodPhoto.setImageURI(it)
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
