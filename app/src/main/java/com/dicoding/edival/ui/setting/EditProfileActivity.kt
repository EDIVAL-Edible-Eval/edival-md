package com.dicoding.edival.ui.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.edival.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private val viewModel by viewModels<EditProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        viewModel.loadUserData(auth)

        // Mengamati perubahan pada LiveData dan mengupdate antarmuka pengguna
        viewModel.user.observe(this) { user ->
            if (user != null) {
                binding.editName.text = Editable.Factory.getInstance().newEditable(user.displayName)
                binding.editEmail.text =
                    Editable.Factory.getInstance().newEditable(user.email)

                // Memuat gambar pengguna ke dalam ImageView
                viewModel.loadUserImage(binding.imageUser, user)
            }
        }

        binding.backarrow.setOnClickListener{
            onBackPressed()
        }

        binding.editPass.setOnClickListener {
            val showChangePass = EditPassFragment()
            showChangePass.show(supportFragmentManager, "change pass pop up")
        }

        binding.btnSave.setOnClickListener {
            val name = binding.editName.text.toString().trim()

            viewModel.updateUserName(auth.currentUser, name)

            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
        }
    }
}
