package com.dicoding.edival.ui.regist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.dicoding.edival.databinding.ActivityRegisterBinding
import com.dicoding.edival.ui.login.loginActivity
import com.google.firebase.auth.FirebaseAuth

class registerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

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
            val name = binding.nameETRegist.text.toString()
            val phone = binding.phoneETRegist.text.toString()
            val email = binding.emailETRegist.text.toString()
            val pass = binding.passETRegist.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && name.isNotEmpty() && phone.isNotEmpty()) {
                binding.proBarRegist.visibility = View.VISIBLE
                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        binding.proBarRegist.visibility = View.GONE
                        Toast.makeText(this, "Account Created Successfully" , Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, loginActivity::class.java)
                        startActivity(intent)
                    } else {
                        binding.proBarRegist.visibility = View.GONE
                        Log.e("error: ", it.exception.toString())
                    }
                }

            }else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }
    }
}