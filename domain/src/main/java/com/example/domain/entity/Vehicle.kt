package com.example.domain.entity

data class Vehicle(
    val vehicleId: String = "",
    val vehicleModel: String = "",
    val manufacturer: String = "",
    val vehicleName: String = "",
    val vehicleEngine: String = "",
    val vehicleFuelType: String = "",
    val vehicleMileage: String = "",
    val seatingCapacity: Int = 0,
    val vehicleType: String = ""
)
