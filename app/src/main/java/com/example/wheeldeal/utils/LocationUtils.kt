package com.example.wheeldeal.utils


import android.content.Context
import android.location.Geocoder
import android.location.Location
import java.util.Locale

fun getAddressFromLocation(context: Context, location: Location): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
    return if (addresses?.isNotEmpty() == true) {
        addresses[0].getAddressLine(0) ?: "Unknown location"
    } else {
        "Unknown location"
    }
}

