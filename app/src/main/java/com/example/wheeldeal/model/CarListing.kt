package com.example.wheeldeal.model

import com.google.firebase.Timestamp

data class CarListing(
    val id: String = "",
    val userId: String = "",
    val condition: String = "", // "New" or "Used"
    val year: Int = 0,
    val brand: String = "",
    val model: String = "",
    val transmission: String = "", // "Manual", "Automatic", etc.
    val color: String = "",
    val engineCapacity: Int = 0, // e.g. "2.0L"
    val fuelType: String = "", // Petrol, Diesel, Electric, etc.
    val avgMileage: Int = 0, // e.g. "15 km/l"
    val odometer: Int = 0, // in km
    val accidents: Int = 0,
    val seats: Int = 0,
    val lastInspection: String = "", // Date as string for now
    val ownership: String = "", // e.g. "First Owner"
    val location: String = "",
    val price: Double = 0.0,
    val negotiable: Boolean = false,
    val photos: List<String> = emptyList(), // Firebase Storage URLs
    val description: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
