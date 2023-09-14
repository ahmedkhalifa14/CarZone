package com.example.carzoneapp.helper

import android.location.Address
import android.location.Geocoder

fun convertLatLongToLocation(latitude: Double, longitude: Double, geocoder: Geocoder): String {
    val addresses: MutableList<Address> =
        geocoder.getFromLocation(latitude, longitude, 1) as MutableList<Address>
    return if (addresses.isNotEmpty()) {
        val address: Address = addresses[0]
        if (address.subLocality.isNullOrEmpty()) {
            address.locality + ", " + address.countryName
        }
        else{
            address.subLocality + ", " + address.countryName
        }
    } else {
        "Unknown location"
    }
}