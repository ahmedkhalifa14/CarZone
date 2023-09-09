package com.example.carzoneapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.carzoneapp.databinding.AdImageItemBinding

class AdImageAdapter(private val imageUrls: List<String>) :
    RecyclerView.Adapter<AdImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            AdImageItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        holder.itemView.apply {
            Glide.with(this)
                .load(imageUrl)
                .into(holder.binding.adImageView)
        }

    }

    override fun getItemCount(): Int = imageUrls.size


    inner class ImageViewHolder(val binding: AdImageItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
