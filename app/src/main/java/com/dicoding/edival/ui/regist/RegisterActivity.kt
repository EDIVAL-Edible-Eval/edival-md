package com.dicoding.edival.ui.regist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.dicoding.edival.databinding.ActivityRegisterBinding
import com.dicoding.edival.ui.login.loginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class registerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.proBarRegist.visibility = View.GONE
        firebaseAuth = FirebaseAuth.getInstance()

        binding.tvSignIn.setOnClickListener {
            val intent = Intent(this@registerActivity, loginActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignUp.setOnClickListener {
            val name = binding.nameETRegist.text.toString().trim()
            val phone = binding.phoneETRegist.text.toString().trim()
            val email = binding.emailETRegist.text.toString().trim()
            val pass = binding.passETRegist.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty() && name.isNotEmpty() && phone.isNotEmpty()) {
                binding.proBarRegist.visibility = View.VISIBLE

                firebaseAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { task ->
                        binding.proBarRegist.visibility = View.GONE

                        if (task.isSuccessful) {
                            val userId = firebaseAuth.currentUser!!.uid

                            val userMap = hashMapOf(
                                "name" to name,
                                "phone" to phone,
                                "email" to email
                            )

                            db.collection("users").document(userId).set(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Account Created Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this, loginActivity::class.java)
                                    startActivity(intent)
                                }.addOnFailureListener {
                                Toast.makeText(
                                    this,
                                    "Failed to save user data to Firestore!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            Toast.makeText(
                                this,
                                "Account creation failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}