package com.dicoding.edival.ui.tutorial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.edival.R

class TutorialSlideAdapter(private val tutorialSlide: List<TutorialSlide>) : RecyclerView.Adapter<TutorialSlideAdapter.TutorialSlideViewHolder>() {

    inner class TutorialSlideViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        private val textTitle = view.findViewById<TextView>(R.id.tutor_title)
        private val textDescription = view.findViewById<TextView>(R.id.tutor_desc)
        private val imagePicture = view.findViewById<ImageView>(R.id.image_tutor)

        fun bind(tutorialSlide: TutorialSlide) {
            textTitle.text = tutorialSlide.title
            textDescription.text = tutorialSlide.description
            imagePicture.setImageResource(tutorialSlide.picture)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorialSlideViewHolder {
       return TutorialSlideViewHolder(
           LayoutInflater.from(parent.context).inflate(
               R.layout.slide_item_container,
               parent,
               false
           )
       )
    }

    override fun getItemCount(): Int {
        return tutorialSlide.size
    }

    override fun onBindViewHolder(holder: TutorialSlideViewHolder, position: Int) {
        holder.bind(tutorialSlide[position])
    }
}