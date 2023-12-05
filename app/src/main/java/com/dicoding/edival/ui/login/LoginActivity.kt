package com.dicoding.edival.ui.login

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.dicoding.edival.databinding.ActivityLoginBinding
import com.dicoding.edival.ui.regist.RegisterActivity
import com.dicoding.edival.ui.ui.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tvSignup = binding.signup
        val text = "Don't have account? Sign up"
        val spannableString = SpannableString(text)

        val btnSignIn = binding.btnSignin

        val startIndex = text.indexOf("Sign up")
        val colorSignUp = Color.parseColor("#FE6601")
        val colorOtherText = Color.parseColor("#21262C")

        spannableString.setSpan(
            ForegroundColorSpan(colorSignUp),
            startIndex,
            startIndex + "Sign up".length,
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
            startIndex + "Sign up".length,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tvSignup.text = spannableString

        tvSignup.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnSignIn.setOnClickListener {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
