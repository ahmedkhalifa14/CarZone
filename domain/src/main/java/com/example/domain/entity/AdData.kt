package com.example.domain.entity

import java.io.Serializable
import java.util.Date

data class AdData(
    val title: String = "",
    val description: String = "",
    val negotiable: Boolean = false,
    val price: String = "",
    val location: String = "",
    val date:Date?= null
):Serializable
