package com.example.wheeldeal.repository

import com.example.wheeldeal.model.CarListing
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FavoritesRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    private fun userFavoritesCollection() =
        firestore.collection("users").document(currentUserId).collection("favorites")

    suspend fun addToFavorites(listingId: String): Result<Void?> {
        return try {
            val favoriteData = hashMapOf("listingId" to listingId)
            userFavoritesCollection().document(listingId).set(favoriteData).await()
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFromFavorites(listingId: String): Result<Void?> {
        return try {
            userFavoritesCollection().document(listingId).delete().await()
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFavoriteIds(): Result<List<String>> {
        return try {
            val snapshot = userFavoritesCollection().get().await()
            val ids = snapshot.documents.mapNotNull { it.getString("listingId") }
            Result.success(ids)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
