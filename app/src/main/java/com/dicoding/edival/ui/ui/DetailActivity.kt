package com.dicoding.edival.ui.ui

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.edival.databinding.ActivityDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var calendar: Calendar
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.proBarUpdate.visibility = View.GONE
        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnFoodPhoto.setOnClickListener{ startGallery()}

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

        val documentId = intent.getStringExtra("REMINDER_ID")
        documentId?.let {
            loadReminderDetails(it)
        }
        binding.btnSave.setOnClickListener {
            binding.proBarUpdate.visibility = View.VISIBLE
            updateReminderDetails(documentId ?: "")
            currentImageUri?.let { uri ->
                uploadImageToFirebaseStorage(uri, documentId ?: "")
            }
        }
        binding.btnDelete.setOnClickListener {
            binding.btnDelete.setOnClickListener {
                showDeleteConfirmationDialog(documentId ?: "")
            }
        }
    }

    private fun showDeleteConfirmationDialog(documentId: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm")
        builder.setMessage("Delete This Data?")

        builder.setPositiveButton("Yes") { dialog, which ->
            deleteReminderFromFirestore(documentId)
        }

        builder.setNegativeButton("No") { dialog, which ->
            // Do nothing
        }

        builder.show()
    }
    private fun deleteReminderFromFirestore(documentId: String) {
        val userId = firebaseAuth.currentUser!!.uid
        db.collection("users").document(userId).collection("reminders").document(documentId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Data deleted successfully", Toast.LENGTH_SHORT).show()
                finish() // kembali ke activity sebelumnya atau lakukan aksi lain
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Fail to delete Data: $exception", Toast.LENGTH_SHORT).show()
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

    private fun loadReminderDetails(documentId: String) {
        val userId = firebaseAuth.currentUser!!.uid
        db.collection("users").document(userId).collection("reminders").document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("name")
                    val storage = document.getString("storage_type")
                    val store = document.getString("store_date")
                    val exp = document.getString("exp_date")
                    val type = document.getString("type")
                    val imageUrl = document.getString("img_path")

                    // Tampilkan data pada UI
                    binding.foodName3.text = name
                    binding.foodName2.setText(name)
                    binding.storage2.setText(storage)
                    binding.simpan2.text = store
                    binding.exp2.text = exp
                    binding.autoComplete.setText(type)
                    // ... set other fields
                    Glide.with(this)
                        .load(imageUrl)
                        .into(binding.btnFoodPhoto) // Gambar ditampilkan dengan Glide
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Load Failed!: $exception", Toast.LENGTH_SHORT).show()
                // Handle error
            }
    }

    private fun updateReminderDetails(documentId: String) {
        val userId = firebaseAuth.currentUser!!.uid


        val expDateString = binding.exp2.text.toString().trim()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val expDate = sdf.parse(expDateString) ?: Date()

        val today = Calendar.getInstance().time

        val value = if (expDate.before(today) || expDate == today) "rotten" else "fresh"

        val updatedData: Map<String, Any> = hashMapOf(
            "name" to binding.foodName2.text.toString(),
            "storage_type" to binding.storage2.text.toString(),
            "store_date" to binding.simpan2.text.toString(),
            "exp_date" to binding.exp2.text.toString(),
            "type" to binding.autoComplete.text.toString(),
            "status" to value
            // Tambahkan kolom lainnya jika ada
        )

        db.collection("users").document(userId).collection("reminders").document(documentId)
            .update(updatedData)
            .addOnSuccessListener {
                Toast.makeText(this, "Update Succesfully!!", Toast.LENGTH_SHORT).show()
                binding.proBarUpdate.visibility = View.GONE
                binding.foodName3.text = binding.foodName2.text.toString()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Update Failed!: $exception", Toast.LENGTH_SHORT).show()
                binding.proBarUpdate.visibility = View.GONE
            }
    }
    private fun uploadImageToFirebaseStorage(imageUri: Uri, documentId: String) {
        val userId = firebaseAuth.currentUser!!.uid
        val storageRef = FirebaseStorage.getInstance().reference.child("user_images/$userId/$documentId.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    updateImageUrlToFirestore(uri.toString(), documentId)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Fail to upload picture: $exception", Toast.LENGTH_SHORT).show()
                binding.proBarUpdate.visibility = View.GONE
            }
    }
    private fun updateImageUrlToFirestore(imageUrl: String, documentId: String) {
        val userId = firebaseAuth.currentUser!!.uid
        val updatedData: Map<String, Any> = hashMapOf(
            "img_path" to imageUrl
        )

        db.collection("users").document(userId).collection("reminders").document(documentId)
            .update(updatedData)
            .addOnSuccessListener {
                Toast.makeText(this, "Picture URL saved", Toast.LENGTH_SHORT).show()
                binding.proBarUpdate.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Fail to save URL Pict: $exception", Toast.LENGTH_SHORT).show()
                binding.proBarUpdate.visibility = View.GONE
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
