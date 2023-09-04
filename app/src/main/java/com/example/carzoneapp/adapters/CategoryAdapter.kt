package com.example.carzoneapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.carzoneapp.databinding.CategoryItemBinding
import com.example.carzoneapp.databinding.OfferingCategoriesLayoutBinding
import com.example.domain.entity.VehiclesCategories

class CategoryAdapter(private val type: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    enum class ViewType {
        TYPE_ONE,
        TYPE_TWO
    }
    inner class HomeViewHolder(val binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class SellViewHolder(val sellBinding: OfferingCategoriesLayoutBinding) :
        RecyclerView.ViewHolder(sellBinding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<VehiclesCategories>() {
        override fun areItemsTheSame(
            oldItem: VehiclesCategories,
            newItem: VehiclesCategories
        ): Boolean {
            return oldItem.categoryID == newItem.categoryID
        }

        override fun areContentsTheSame(
            oldItem: VehiclesCategories,
            newItem: VehiclesCategories
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder  {
        return when (viewType) {
            ViewType.TYPE_ONE.ordinal -> {
                HomeViewHolder(
                    CategoryItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )

            }

            ViewType.TYPE_TWO.ordinal -> {
                SellViewHolder(
                    OfferingCategoriesLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val category = differ.currentList[position]
        when (holder.itemViewType) {
            ViewType.TYPE_ONE.ordinal -> {
                val homeViewHolder = holder as HomeViewHolder
                homeViewHolder.itemView.apply {
                    Glide.with(this).load(category.categoryImage)
                        .into(homeViewHolder.binding.categoryImg)
                    homeViewHolder.binding.categoryName.text = category.categoryName
                    setOnClickListener {
                        onItemClickListener?.let { it(category) }
                    }
                }
            }

            ViewType.TYPE_TWO.ordinal -> {
                val sellViewHolder = holder as SellViewHolder
                sellViewHolder.itemView.apply {
                    Glide.with(this).load(category.categoryImage)
                        .into(sellViewHolder.sellBinding.sellFragmentRvImg)
                    sellViewHolder.sellBinding.sellFragmentRvTxt.text = category.categoryName
                    setOnClickListener {
                        onItemClickListener?.let { it(category) }
                    }
                }

            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((VehiclesCategories) -> Unit)? = null
    fun setOnItemClickListener(listener: (VehiclesCategories) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemViewType(position: Int): Int {
        //return if (position % 2 == 0) TYPE_VIEWHOLDER_ONE else TYPE_VIEWHOLDER_TWO
        return when (type) {
            1 -> {
                ViewType.TYPE_ONE.ordinal
            }

            2 -> {
                ViewType.TYPE_TWO.ordinal
            }

            else -> throw IllegalArgumentException("Invalid value")

        }
    }

}