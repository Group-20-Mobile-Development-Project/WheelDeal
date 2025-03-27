package com.example.wheeldeal.repository

import com.example.wheeldeal.model.CarListing
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ListingRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val listingsCollection = firestore.collection("listings")

    suspend fun addListing(listing: CarListing): Result<Void?> {
        return try {
            val docRef = listingsCollection.document()
            val listingWithMeta = listing.copy(
                userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty(),
                createdAt = Timestamp.now()
            )
            docRef.set(listingWithMeta).await()
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllListings(): Result<List<CarListing>> {
        return try {
            val snapshot = listingsCollection.get().await()
            val listings = snapshot.documents.mapNotNull { it.toObject(CarListing::class.java) }
            Result.success(listings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // You can add update/delete methods later when needed
}
