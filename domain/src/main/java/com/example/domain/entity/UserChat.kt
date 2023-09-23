package com.example.domain.entity

data class UserChat(
    val userId:String,
    val chatId: String,
    val otherUserId: String,
    val otherUserName: String,
    val userName: String,
    val userImg: String,
    val otherUserImg: String,
    val latestMessage: String,
    val timestamp: Long // Timestamp of the latest message for sorting
)