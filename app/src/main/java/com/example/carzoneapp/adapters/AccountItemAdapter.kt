package com.example.carzoneapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.carzoneapp.databinding.AccountItemBinding
import com.example.domain.entity.ProfileItem

class AccountItemAdapter : RecyclerView.Adapter<AccountItemAdapter.AccountItemViewHolder>() {

    inner class AccountItemViewHolder(val binding: AccountItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<ProfileItem>() {
        override fun areItemsTheSame(oldItem: ProfileItem, newItem: ProfileItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProfileItem, newItem: ProfileItem): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountItemViewHolder {
        return AccountItemViewHolder(
            AccountItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )


    }

    override fun onBindViewHolder(holder: AccountItemViewHolder, position: Int) {
        val accountItem = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(accountItem.iconOne).into(holder.binding.accountItemIcon)
            Glide.with(this).load(accountItem.iconTwo).into(holder.binding.accountItemIcon2)
            holder.binding.accountItemTitle.text = accountItem.title
            holder.binding.accountItemSubtitle.text = accountItem.subtitle
            setOnClickListener {
                onItemClickListener?.let { it(accountItem) }
            }

        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((ProfileItem) -> Unit)? = null
    fun setOnItemClickListener(listener: (ProfileItem) -> Unit) {
        onItemClickListener = listener
    }
}