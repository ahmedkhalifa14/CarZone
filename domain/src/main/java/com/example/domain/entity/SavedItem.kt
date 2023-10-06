package com.example.domain.entity

data class SavedItem(
    val userId: String="",
    val itemId:String="",
    val ad: Ad = Ad()
)