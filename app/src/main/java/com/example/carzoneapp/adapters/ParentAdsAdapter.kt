package com.example.carzoneapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.carzoneapp.databinding.HomeAdsParentLayoutBinding
import com.example.domain.entity.Ad
import com.example.domain.entity.HomeAdsAdapterItem


class ParentAdsAdapter(private val homeAdsAdapterItem: List<HomeAdsAdapterItem>) :
    RecyclerView.Adapter<ParentAdsAdapter.AdsAdapterViewHolder>() {
    inner class AdsAdapterViewHolder(val binding: HomeAdsParentLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var onItemClickListener: ((HomeAdsAdapterItem, Ad) -> Unit)? = null

    // Set the click listener for the parent adapter
    fun setOnItemClickListener(listener: (HomeAdsAdapterItem, Ad) -> Unit) {
        onItemClickListener = listener
    }


    private var onSaveIconClickListener: ((HomeAdsAdapterItem, Ad) -> Unit)? = null
    fun setOnSaveIconClickListener(listener: (HomeAdsAdapterItem, Ad) -> Unit) {
        onSaveIconClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdsAdapterViewHolder {
        return AdsAdapterViewHolder(
            HomeAdsParentLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = homeAdsAdapterItem.size

    override fun onBindViewHolder(holder: AdsAdapterViewHolder, position: Int) {
        holder.binding.apply {
            val adsItem = homeAdsAdapterItem[position]
            adsParentLayoutAdsTypeTv.text = adsItem.title

            val adsAdapter = AdsAdapter(1,
                onItemClickListener = { ad -> onItemClickListener?.invoke(adsItem, ad) },
                onSaveIconClickListener = { ad -> onSaveIconClickListener?.invoke(adsItem, ad) }
            )


            adsAdapter.differ.submitList(adsItem.adsList)
            adsParentLayoutRv.adapter = adsAdapter
        }
    }
}



