package com.example.domain.entity

import com.example.domain.entity.Ad

data class HomeAdsAdapterItem(
    val title: String,
    val adsList: MutableList<Ad>?,
    var onItemClick: ((Ad) -> Unit)? = null

)