package com.example.carzoneapp.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.carzoneapp.databinding.AdsItemBinding
import com.example.carzoneapp.helper.extractDateAndTime
import com.example.domain.entity.Ad

class AdsAdapter() :
    RecyclerView.Adapter<AdsAdapter.AdsAdapterViewHolder>() {

    inner class AdsAdapterViewHolder(val binding: AdsItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<Ad>() {
        override fun areItemsTheSame(
            oldItem: Ad,
            newItem: Ad
        ): Boolean {
            return oldItem.vehicle.vehicleId == newItem.vehicle.vehicleId
        }

        override fun areContentsTheSame(
            oldItem: Ad,
            newItem: Ad
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdsAdapterViewHolder {
        return AdsAdapterViewHolder(
            AdsItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AdsAdapterViewHolder, position: Int) {
        val ads = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(ads.vehicleImages[0]).into(holder.binding.carImg)
//            Glide.with(this).load(accountItem.iconTwo).into(holder.binding.accountItemIcon2)
            holder.binding.carName.text = ads.adsData.title
            holder.binding.carPrice.text = ads.adsData.price
            if (ads.adsData.negotiable) {
                holder.binding.negotiableTv.text = "Negotiable"
            } else {
                holder.binding.negotiableTv.text = "Not negotiable"
            }

            val firebaseDate = ads.adsData.date
            val dateAndTime = extractDateAndTime(firebaseDate)
            val date = dateAndTime.day + " " + dateAndTime.month
            holder.binding.listedDate.text = date
            setOnClickListener {
                onItemClickListener?.let { it(ads) }
            }

        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((Ad) -> Unit)? = null
    fun setOnItemClickListener(listener: (Ad) -> Unit) {
        onItemClickListener = listener
    }
}