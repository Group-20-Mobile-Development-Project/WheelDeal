package com.example.wheeldeal.viewmodel

import androidx.lifecycle.ViewModel
import com.example.wheeldeal.model.NotificationItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotificationViewModel : ViewModel() {
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications

    private val db = FirebaseFirestore.getInstance()

    init {
        fetchNotifications()
    }

    private fun fetchNotifications() {
        db.collection("notifications")
            .addSnapshotListener { snapshot, _ ->
                val notificationList = snapshot?.documents?.mapNotNull { doc ->
                    val title = doc.getString("title") ?: return@mapNotNull null
                    val details = doc.getString("details") ?: ""
                    val isRead = doc.getBoolean("isRead") ?: false
                    NotificationItem(id = doc.id, title = title, details = details, isRead = isRead)
                } ?: emptyList()

                _notifications.value = notificationList
            }
    }

    fun markAsRead(notification: NotificationItem) {
        _notifications.value = _notifications.value.map {
            if (it.id == notification.id) it.copy(isRead = true) else it
        }

        db.collection("notifications").document(notification.id)
            .update("isRead", true)
    }

    fun deleteNotification(notification: NotificationItem) {
        _notifications.value = _notifications.value.filter { it.id != notification.id }

        db.collection("notifications").document(notification.id)
            .delete()
    }

    fun deleteAllNotifications() {
        _notifications.value = emptyList()

        db.collection("notifications").get()
            .addOnSuccessListener { snapshot ->
                snapshot.documents.forEach { it.reference.delete() }
            }
    }
}