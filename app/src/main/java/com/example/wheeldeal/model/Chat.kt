package com.example.wheeldeal.model

import com.google.firebase.Timestamp

data class Chat(
    val id: String = "",
    val user1Id: String = "",
    val user2Id: String = "",
    val lastMessage: String = "",
    val lastTimestamp: Timestamp? = null
)
