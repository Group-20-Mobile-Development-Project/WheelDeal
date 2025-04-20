package com.example.wheeldeal.repository

import com.example.wheeldeal.model.Chat
import com.example.wheeldeal.model.Message
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getCurrentUserId() = auth.currentUser?.uid.orEmpty()

    suspend fun sendMessage(chatId: String, senderId: String, receiverId: String, message: String) {
        val timestamp = Timestamp.now()

        val messageData = Message(
            message = message,
            senderId = senderId,
            receiverId = receiverId,
            timestamp = timestamp
        )

        db.collection("chats").document(chatId)
            .collection("messages")
            .add(messageData)
            .await()

        db.collection("chats").document(chatId)
            .update(
                mapOf(
                    "lastMessage" to message,
                    "lastTimestamp" to timestamp
                )
            )
    }

    suspend fun createOrGetChatId(receiverId: String): String {
        val senderId = getCurrentUserId()

        val existingChats = db.collection("chats")
            .whereEqualTo("user1Id", senderId)
            .whereEqualTo("user2Id", receiverId)
            .get()
            .await()

        if (!existingChats.isEmpty) return existingChats.documents[0].id

        val reverseChats = db.collection("chats")
            .whereEqualTo("user1Id", receiverId)
            .whereEqualTo("user2Id", senderId)
            .get()
            .await()

        if (!reverseChats.isEmpty) return reverseChats.documents[0].id

        val chat = Chat(
            user1Id = senderId,
            user2Id = receiverId,
            lastMessage = "",
            lastTimestamp = Timestamp.now()
        )

        val newChat = db.collection("chats").add(chat).await()
        return newChat.id
    }

    suspend fun getMessages(chatId: String): List<Message> {
        val result = db.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .get()
            .await()

        return result.toObjects(Message::class.java)
    }

    fun listenToMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val listener = db.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                val messages = snapshot?.toObjects(Message::class.java) ?: emptyList()
                trySend(messages).isSuccess
            }

        awaitClose { listener.remove() }
    }
    /** fetch all chats involving this user */
    suspend fun getChatsForUser(userId: String): List<Chat> {
        val db = FirebaseFirestore.getInstance()
        val results = mutableListOf<Chat>()

        // Query where user1Id == you
        db.collection("chats")
            .whereEqualTo("user1Id", userId)
            .get().await()
            .documents
            .forEach { snap ->
                snap.toObject(Chat::class.java)
                    ?.copy(id = snap.id)
                    ?.let { results.add(it) }
            }

        // Query where user2Id == you
        db.collection("chats")
            .whereEqualTo("user2Id", userId)
            .get().await()
            .documents
            .forEach { snap ->
                snap.toObject(Chat::class.java)
                    ?.copy(id = snap.id)
                    ?.let { results.add(it) }
            }

        return results
    }
}
