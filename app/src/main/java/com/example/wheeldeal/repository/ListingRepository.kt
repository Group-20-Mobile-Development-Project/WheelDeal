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
                id = docRef.id,
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

    // update/delete
    suspend fun deleteListing(listingId: String): Result<Void?> {
        return try {
            FirebaseFirestore.getInstance().collection("listings")
                .document(listingId).delete().await()
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateListing(listing: CarListing): Result<Void?> {
        return try {
            FirebaseFirestore.getInstance().collection("listings")
                .document(listing.id).set(listing).await()
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun sendNewListingNotification(listing: CarListing): Result<Void?> {
        return try {
            val notificationsCollection = FirebaseFirestore.getInstance().collection("notifications")
            val notificationData = mapOf(
                "title" to "New Car Listed: ${listing.brand} ${listing.model}",
                "message" to "${listing.brand} ${listing.model} is now available for \$${listing.price}.",
                "timestamp" to Timestamp.now(),
                "carId" to listing.id
            )
            notificationsCollection.add(notificationData).await()
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


