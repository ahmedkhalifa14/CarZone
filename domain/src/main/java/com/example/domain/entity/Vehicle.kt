package com.example.domain.entity

import java.io.Serializable

data class Vehicle(
    val vehicleModel: String = "",
    val manufacturer: String = "",
    val vehicleName: String = "",
    val vehicleEngine: String = "",
    val vehicleFuelType: String = "",
    val vehicleMileage: String = "",
    val seatingCapacity: Int = 0,
    val vehicleType: String = ""
):Serializable
