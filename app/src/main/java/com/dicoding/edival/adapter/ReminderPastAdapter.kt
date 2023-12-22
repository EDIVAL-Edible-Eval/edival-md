package com.dicoding.edival.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.edival.R
import com.dicoding.edival.data.db.Reminder
import com.dicoding.edival.ui.ui.DetailActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ReminderPastAdapter(private val reminderList: ArrayList<Reminder>) : RecyclerView.Adapter<ReminderPastAdapter.MyViewHolder>() {
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val Name: TextView = itemView.findViewById(R.id.foodName)
        val Spoiled: TextView = itemView.findViewById(R.id.spoiledFood)
        val Type: TextView = itemView.findViewById(R.id.typeFood)
        val Icon: ImageView = itemView.findViewById(R.id.typeIcon)
        val imageView: ImageView = itemView.findViewById(R.id.foodPict)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return MyViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        return reminderList.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentReminder = reminderList[position]
        holder.Name.text = currentReminder.name
        holder.Spoiled.text = currentReminder.exp_date
        holder.Type.text = currentReminder.type
        Glide.with(holder.itemView.context)
            .load(currentReminder.img_path)
            .into(holder.imageView)
        when (currentReminder.type) {
            "Vegetable" -> holder.Icon.setImageResource(R.drawable.vegetable)
            "Meat" -> holder.Icon.setImageResource(R.drawable.daging)
            "Fruit" -> holder.Icon.setImageResource(R.drawable.fruit)
            "Others" -> holder.Icon.setImageResource(R.drawable.others)
            else -> holder.Icon.setImageResource(R.drawable.others) // Icon default jika typeFood tidak sesuai
        }
        val userId = firebaseAuth.currentUser!!.uid
        val mainCollectionRef = db.collection("users").document(userId).collection("reminders")
        val query = mainCollectionRef.whereEqualTo("documentId", currentReminder.documentId)
        query.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                val documentId = document.id
                holder.itemView.setOnClickListener {
                    val intent = Intent(holder.itemView.context, DetailActivity::class.java)
                    intent.putExtra("REMINDER_ID", documentId)
                    holder.itemView.context.startActivity(intent)
                }
            }
        }.addOnFailureListener { exception ->
        }
    }
}
