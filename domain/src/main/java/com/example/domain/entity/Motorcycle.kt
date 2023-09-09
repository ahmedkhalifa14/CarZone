package com.example.domain.entity

import java.io.Serializable

data class Motorcycle(
    val enginePower: String="",
    val engineTorque: String="",
    val kerbWeight: Double=0.0
):Serializable
