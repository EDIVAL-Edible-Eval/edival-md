package com.dicoding.edival.ui.regist

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.dicoding.edival.databinding.ActivityRegisterBinding
import com.dicoding.edival.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tvSignin = binding.signin
        val text = "Have an account? Sign In"
        val spannableString = SpannableString(text)

        val startIndex = text.indexOf("Sign In")
        val colorSignUp = Color.parseColor("#FE6601")
        val colorOtherText = Color.parseColor("#21262C")

        spannableString.setSpan(
            ForegroundColorSpan(colorSignUp),
            startIndex,
            startIndex + "Sign In".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(colorOtherText),
            0,
            startIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(colorOtherText),
            startIndex + "Sign In".length,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tvSignin.text = spannableString

        tvSignin.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
