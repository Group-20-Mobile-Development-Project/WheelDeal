package com.example.wheeldeal.model

import com.google.firebase.Timestamp

data class Message(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Timestamp? = null
)
