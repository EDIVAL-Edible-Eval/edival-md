package com.dicoding.edival.ui.camera

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.edival.R

class FoodResultAdapter(private val foodList: List<String>, private val onItemClick: (String) -> Unit) :
    RecyclerView.Adapter<FoodResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_predict_name, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val foodItem = foodList[position]
        holder.bind(foodItem)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val btnFood: Button = itemView.findViewById(R.id.food1)

        fun bind(foodItem: String) {
            btnFood.text = foodItem
            btnFood.setOnClickListener { onItemClick(foodItem) }
        }
    }
}
