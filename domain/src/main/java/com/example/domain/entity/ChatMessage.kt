package com.example.domain.entity

data class ChatMessage(
    val messageId: String = "",
    val messageSenderId: String = "",
    val messageReceiverId: String = "",
    val message: String = "",
    val date: String = "",
    val time:String=""
)