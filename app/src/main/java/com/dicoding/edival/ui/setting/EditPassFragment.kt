package com.dicoding.edival.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.dicoding.edival.R
import com.dicoding.edival.databinding.FragmentEditPassBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class EditPassFragment : DialogFragment() {

    private var _binding: FragmentEditPassBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditPassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        val pass = binding.inputPassword
        val newPass = binding.editPass
        val newPassConfirm = binding.editConfrirmPass
        val layoutAuth = binding.layoutPassword
        val layoutEdit = binding.layoutEmail

        layoutAuth.visibility = View.VISIBLE
        layoutEdit.visibility = View.GONE

        binding.btnAuth.setOnClickListener {
            val password = pass.text.toString().trim()

            if (password.isEmpty()){
                pass.error = getString(R.string.pass_empty_warn)
                pass.requestFocus()
                return@setOnClickListener
            }

            user?.let { it ->
                val userCredential = EmailAuthProvider.getCredential(it.email!!, password)
                it.reauthenticate(userCredential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        layoutAuth.visibility = View.GONE
                        layoutEdit.visibility = View.VISIBLE
                    }else if (it.exception is FirebaseAuthInvalidCredentialsException){
                        pass.error = getString(R.string.pass_wrong)
                        pass.requestFocus()
                    }else {
                        Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            binding.btnUpdate.setOnClickListener {
                val newPassword = newPass.text.toString().trim()
                val newPasswordConfirm = newPassConfirm.text.toString().trim()

                if (newPassword.length < 6){
                    newPass.error = getString(R.string.pass_too_short)
                    newPass.requestFocus()
                    return@setOnClickListener
                }
                if (newPassword.isEmpty() || newPasswordConfirm.isEmpty()) {
                    newPassConfirm.error = getString(R.string.pass_empty_warn)
                    newPassConfirm.requestFocus()
                    newPass.error = getString(R.string.pass_empty_warn)
                    newPass.requestFocus()
                    return@setOnClickListener
                }
                if (newPassword != newPasswordConfirm) {
                    newPassConfirm.error = getString(R.string.pass_not_match)
                    newPassConfirm.requestFocus()
                    return@setOnClickListener
                }

                user?.let {
                    user.updatePassword(newPassword).addOnCompleteListener {
                        if (it.isSuccessful){
                            val intent = Intent(context, EditProfileActivity::class.java)
                            startActivity(intent)
                            Toast.makeText(activity,
                                getString(R.string.pass_updated), Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }
    }
}