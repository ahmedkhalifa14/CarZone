package com.example.carzoneapp.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.carzoneapp.databinding.ChatItemBinding
import com.example.domain.entity.ChatMessage

class MessageAdapter(
    messageSenderID: String,
    recyclerView: RecyclerView
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()
    val id = messageSenderID
    private val recyclerViewRef = recyclerView

    private var shouldAutoScroll = true

    fun setMessages(newMessages: List<ChatMessage>) {
        val scrollToBottom = messages.isEmpty()
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
        if (scrollToBottom) {
            scrollToBottom()

        }
    }

    private fun scrollToBottom() {
        recyclerViewRef.post {
            recyclerViewRef.smoothScrollToPosition(messages.size - 1)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                // Check if the user manually scrolled
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    shouldAutoScroll = false
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    shouldAutoScroll = true
                }
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            ChatItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        val senderID = id
        holder.bind(message, senderID)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class MessageViewHolder(val binding: ChatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage, senderID: String) {
            if (message.messageSenderId == senderID) {
                binding.textChatMessageSender.visibility = View.VISIBLE
                binding.textChatMessageReceiver.visibility = View.GONE
                binding.textChatMessageSender.text = message.message
            } else {
                binding.textChatMessageSender.visibility = View.GONE
                binding.textChatMessageReceiver.visibility = View.VISIBLE
                binding.textChatMessageReceiver.text = message.message
            }
        }
    }

    override fun onBindViewHolder(
        holder: MessageViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        if (shouldAutoScroll) {
            scrollToBottom()
        }
    }
}
