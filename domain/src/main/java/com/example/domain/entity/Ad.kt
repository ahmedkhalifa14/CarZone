package com.example.domain.entity

import java.io.Serializable

data class Ad(
    val adsData: AdData,
    val vehicle: Vehicle,
    val vehicleImages: List<String>,
    val seller: String,
    val vehicleType: VehicleData?=null
):Serializable

//data class Ads<T>(
//    val adsData: AdsData,
//    val vehicle: Vehicle,
//    val vehicleImages:List<String>,
//    val seller:User,
//    val vehicleType:T
//)