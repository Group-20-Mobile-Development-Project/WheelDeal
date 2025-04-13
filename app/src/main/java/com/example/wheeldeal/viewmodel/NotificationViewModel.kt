package com.example.wheeldeal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wheeldeal.model.NotificationItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
                    NotificationItem(id = doc.id, title = title, details = details)
                } ?: emptyList()

                _notifications.value = notificationList
            }
    }
}
