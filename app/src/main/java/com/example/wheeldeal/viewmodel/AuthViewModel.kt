package com.example.wheeldeal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wheeldeal.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData

    init {
        loadUserData()
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = repository.login(email, password)
            if (result.isSuccess) {
                loadUserData()
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun signUp(email: String, password: String, firstName: String, lastName: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = repository.signUp(email, password)
            if (result.isSuccess) {
                val user = result.getOrNull()
                user?.uid?.let { uid ->
                    val userMap = hashMapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "email" to email
                    )
                    FirebaseFirestore.getInstance().collection("users").document(uid).set(userMap)
                        .addOnSuccessListener { loadUserData() }
                }
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun loadUserData() {
        val user = repository.currentUser
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        _userData.value = UserData(
                            uid = user.uid,
                            email = doc.getString("email") ?: "",
                            firstName = doc.getString("firstName") ?: "",
                            lastName = doc.getString("lastName") ?: "",
                            profilePicUrl = doc.getString("profilePicUrl")
                        )
                    }
                }
        } else {
            _userData.value = null
        }
    }

    fun logout() {
        repository.logout()
        _userData.value = null
        _authState.value = AuthState.Idle
    }

    fun deleteAccount() {
        val user = repository.currentUser
        user?.let {
            FirebaseFirestore.getInstance().collection("users").document(user.uid).delete()
            user.delete()
            logout()
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun updateUserData(firstName: String, lastName: String) {
        val currentUser = repository.currentUser
        currentUser?.let { user ->
            FirebaseFirestore.getInstance().collection("users").document(user.uid)
                .update(
                    mapOf(
                        "firstName" to firstName,
                        "lastName" to lastName
                    )
                )
                .addOnSuccessListener {
                    loadUserData()
                }
                .addOnFailureListener { e ->
                    // Handle error if needed
                }
        }
    }
}

sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    object Success : AuthState
    data class Error(val message: String) : AuthState
}

data class UserData(
    val uid: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val profilePicUrl: String? = null
)