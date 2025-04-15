package com.example.wheeldeal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wheeldeal.model.Message
import com.example.wheeldeal.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val repository = ChatRepository()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    var currentChatId: String? = null
        private set

    fun loadChat(receiverId: String) {
        viewModelScope.launch {
            val chatId = repository.createOrGetChatId(receiverId)
            currentChatId = chatId
            _messages.value = repository.getMessages(chatId)
        }
    }


    fun sendMessage(chatId: String, senderId: String, receiverId: String, message: String) {
        viewModelScope.launch {
            repository.sendMessage(chatId, senderId, receiverId, message)
            _messages.value = repository.getMessages(chatId)
        }
    }
    fun listenToMessages(chatId: String) {
        viewModelScope.launch {
            repository.listenToMessages(chatId).collect {
                _messages.value = it
            }
        }
    }

    fun refreshMessages() {
        currentChatId?.let { chatId ->
            viewModelScope.launch {
                _messages.value = repository.getMessages(chatId)
            }
        }
    }
}
