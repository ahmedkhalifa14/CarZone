package com.example.carzoneapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.carzoneapp.R
import com.example.carzoneapp.databinding.AdsItemBinding
import com.example.carzoneapp.helper.extractDateAndTime
import com.example.domain.entity.SavedItem

class SavedItemAdapter : RecyclerView.Adapter<SavedItemAdapter.SavedItemViewHolder>() {

    inner class SavedItemViewHolder(val binding: AdsItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<SavedItem>() {
        override fun areItemsTheSame(oldItem: SavedItem, newItem: SavedItem): Boolean {
            return oldItem.itemId == newItem.itemId
        }

        override fun areContentsTheSame(oldItem: SavedItem, newItem: SavedItem): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedItemViewHolder {
        return SavedItemViewHolder(
            AdsItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )


    }

    override fun onBindViewHolder(holder: SavedItemViewHolder, position: Int) {
        val savedItem = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(savedItem.ad.vehicleImages?.get(0)).into(holder.binding.carImg)
            holder.binding.carName.text = savedItem.ad.vehicle.vehicleName
            holder.binding.carPrice.text = savedItem.ad.adsData.price
            val firebaseDate = savedItem.ad.adsData.date
            val dateAndTime = extractDateAndTime(firebaseDate!!)
            val date = dateAndTime.day + " " + dateAndTime.month
            holder.binding.listedDate.text = date
            holder.binding.sellerLocationTv.text = savedItem.ad.adsData.location
            if (savedItem.ad.adsData.negotiable) {
                holder.binding.negotiableTv.text = context.getString(R.string.negotiable)
            } else {
                holder.binding.negotiableTv.text = context.getString(R.string.not_negotiable)
            }
            setOnClickListener {
                onItemClickListener?.let { it(savedItem) }
            }
            holder.binding.saveIcon.setOnClickListener {
                onSaveIconItemClickListener?.let { item ->
                    item(savedItem)
                }
            }

        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((SavedItem) -> Unit)? = null
    fun setOnItemClickListener(listener: (SavedItem) -> Unit) {
        onItemClickListener = listener
    }


    private var onSaveIconItemClickListener: ((SavedItem) -> Unit)? = null
    fun setOnSaveItemClickListener(listener: (SavedItem) -> Unit) {
        onSaveIconItemClickListener = listener
    }

}