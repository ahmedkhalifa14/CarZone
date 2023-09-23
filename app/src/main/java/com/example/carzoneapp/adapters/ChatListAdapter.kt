package com.example.carzoneapp.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.carzoneapp.databinding.ChatListItemBinding
import com.example.carzoneapp.helper.calculateTimeStampDifference
import com.example.domain.entity.UserChat

class ChatListAdapter(userId: String) : RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>() {

    private val currentUserId = userId

    inner class ChatListViewHolder(val binding: ChatListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<UserChat>() {
        override fun areItemsTheSame(oldItem: UserChat, newItem: UserChat): Boolean {
            return oldItem.chatId == newItem.chatId
        }

        override fun areContentsTheSame(oldItem: UserChat, newItem: UserChat): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        return ChatListViewHolder(
            ChatListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        val chatItem = differ.currentList[position]
        holder.itemView.apply {
            val currentTimestamp = System.currentTimeMillis()
            val time = calculateTimeStampDifference(chatItem.timestamp, currentTimestamp)
            val lastMessageTime= if (time.size==1){
                time[0]
            }else{
                time[0] + " " + time[1]
            }
            holder.binding.chatLastMessageTimeTv.text = lastMessageTime
            if (currentUserId == chatItem.userId) {
                Glide.with(this).load(chatItem.otherUserImg).into(holder.binding.chatUserImg)
            } else {
                Glide.with(this).load(chatItem.userImg).into(holder.binding.chatUserImg)
            }
            holder.binding.chatUsernameTv.text = chatItem.otherUserName
            holder.binding.lastMessage.text = chatItem.latestMessage
            setOnClickListener {
                onItemClickListener?.let { it(chatItem) }
            }

        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((UserChat) -> Unit)? = null
    fun setOnItemClickListener(listener: (UserChat) -> Unit) {
        onItemClickListener = listener
    }
}