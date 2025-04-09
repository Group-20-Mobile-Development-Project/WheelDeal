package com.example.wheeldeal.repository
/*
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*

class StorageRepository {

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference.child("car_images")

    suspend fun uploadImage(uri: Uri): Result<String> {
        return try {
            val imageRef = storageRef.child("${UUID.randomUUID()}.jpg")
            imageRef.putFile(uri).await()
            val downloadUrl = imageRef.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
*/