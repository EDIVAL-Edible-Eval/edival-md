package com.dicoding.edival.ui.setting

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.dicoding.edival.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

// SettingViewModel.kt
class SettingViewModel : ViewModel() {

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> get() = _user

    // Fungsi ini akan memuat data pengguna ke dalam LiveData
    fun loadUserData(auth: FirebaseAuth) {
        _user.value = auth.currentUser
    }

    // Fungsi ini dapat digunakan untuk memuat gambar pengguna ke dalam ImageView
    fun loadUserImage(imageView: ImageView, user: FirebaseUser?) {
        if (user != null && user.photoUrl != null) {
            Glide.with(imageView.context)
                .load(user.photoUrl)
                .apply(RequestOptions.circleCropTransform())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
        } else {
            // Jika user tidak memiliki foto, gunakan foto default dari drawable
            Glide.with(imageView.context)
                .load(R.drawable.user_image)
                .apply(RequestOptions.circleCropTransform())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
        }
    }
}
