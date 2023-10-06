package com.example.domain.entity

import java.io.Serializable

data class Ad(
    val adId: String="",
    val adsData: AdData= AdData(),
    val vehicle: Vehicle= Vehicle(),
    val vehicleImages: List<String>? = null,
    val seller: String="",
    val vehicleType: VehicleData? = null,
    val isInSavedItems: Boolean? = false
) : Serializable