package com.example.domain.entity

sealed class VehicleData
data class CarData(val car: Car) : VehicleData()
data class VanData(val van: Van) : VehicleData()
data class TruckData(val truck: Truck):VehicleData()
data class MotorCycleData(val motorcycle: Motorcycle):VehicleData()