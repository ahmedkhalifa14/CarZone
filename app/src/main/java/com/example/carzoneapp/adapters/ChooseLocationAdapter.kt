package com.example.carzoneapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.carzoneapp.databinding.ChooseLocationItemBinding
import com.example.domain.entity.GeoName


class ChooseLocationAdapter() :
    RecyclerView.Adapter<ChooseLocationAdapter.ChooseLocationViewHolder>() {

    inner class ChooseLocationViewHolder(val binding: ChooseLocationItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<GeoName>() {
        override fun areItemsTheSame(oldItem: GeoName, newItem: GeoName): Boolean {
            return oldItem.adminName1 == newItem.adminName1
        }

        override fun areContentsTheSame(oldItem: GeoName, newItem: GeoName): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseLocationViewHolder {
        return ChooseLocationViewHolder(
            ChooseLocationItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )


    }

    override fun onBindViewHolder(holder: ChooseLocationViewHolder, position: Int) {
        val geoName = differ.currentList[position]
        holder.itemView.apply {
            holder.binding.regionName.text = geoName.adminName1
            setOnClickListener {
                onItemClickListener?.let { it(geoName) }
            }

        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((GeoName) -> Unit)? = null
    fun setOnItemClickListener(listener: (GeoName) -> Unit) {
        onItemClickListener = listener
    }
}