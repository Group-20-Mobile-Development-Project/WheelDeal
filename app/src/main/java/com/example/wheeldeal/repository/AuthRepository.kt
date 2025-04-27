package com.example.wheeldeal.repository

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun changePassword(currentPw: String, newPw: String) = runCatching {
        val user = auth.currentUser
            ?: throw Exception("Not logged in")
        val email = user.email
            ?: throw Exception("No email on account")

        // Reauthenticate
        val credential = EmailAuthProvider.getCredential(email, currentPw)
        user.reauthenticateAndRetrieveData(credential).await()

        // Update password
        user.updatePassword(newPw).await()
    }


}
