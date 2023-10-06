package com.example.domain.entity

import java.io.Serializable

data class User(
    val userId: String = "",
    val userName: String = "",
    val location: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val image: String = "",
    val joinedAt: Long = 0
) : Serializable
