package com.dicoding.edival.adapters

import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.edival.databinding.ItemVerticalDateBinding

class VerticalDateAdapter: RecyclerView.Adapter<VerticalDateAdapter.VerticalDateHolder>() {

    class VerticalDateHolder(val itemVerticalDateBinding: ItemVerticalDateBinding) : RecyclerView.ViewHolder(itemVerticalDateBinding.root){
        fun bind(item: VerticalDateItem){
            itemVerticalDateBinding.dateletter.text= item.dateletter
            itemVerticalDateBinding.datenumber.text = item.datenumber.toString()
        }
    }

    private val data = arrayListOf<VerticalDateItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalDateHolder {
        return VerticalDateHolder(ItemVerticalDateBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VerticalDateHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateListDate() {

        val newListDate = ArrayList<VerticalDateItem>()
        val calendar = Calendar.getInstance()
        val today = calendar.time
        calendar.add(Calendar.DAY_OF_MONTH, -3)
        while (calendar.time.before(today) || calendar.time == today) {

            newListDate.add(VerticalDateItem(
                calendar.get(Calendar.DAY_OF_WEEK).toString().first().toString(),
                calendar.get(Calendar.DAY_OF_MONTH)
            ))

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        Log.e("Debug Bang", newListDate.toString())
        data.clear()
        data.addAll(newListDate)
        notifyDataSetChanged()
    }
}