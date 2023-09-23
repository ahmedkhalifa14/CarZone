package com.example.domain.entity

data class ChatMessage(
    val messageSenderId: String = "",
    val messageReceiverId: String = "",
    val message: String = "",
    val date: String ?= null,
    val time:String?=null,
    val timestamp: Long?=null
)