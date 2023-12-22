package com.dicoding.edival.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.edival.R
import com.dicoding.edival.data.db.Reminder
import com.dicoding.edival.ui.ui.DetailActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotifAdapter(private val reminderList: List<Reminder>) : RecyclerView.Adapter<NotifAdapter.MyViewHolder>(){
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val desc: TextView = itemView.findViewById(R.id.desc)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return reminderList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentReminder = reminderList[position]
        holder.desc.text = currentReminder.desc
        val userId = firebaseAuth.currentUser!!.uid

        val mainCollectionRef = db.collection("users").document(userId).collection("notification")

        val query = mainCollectionRef.whereEqualTo("documentId", currentReminder.documentId)

        query.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                val documentId = document.id // Ini adalah UID dari dokumen
                // Lakukan sesuatu dengan documentId
                holder.itemView.setOnClickListener {
                }
            }
        }.addOnFailureListener { exception ->
            // Handle error
        }
    }
}