package com.example.wheeldeal.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wheeldeal.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.HorizontalDivider

@Composable
fun ChatScreen(
    chatId: String,
    receiverId: String,
    chatViewModel: ChatViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var messageText by remember { mutableStateOf(TextFieldValue("")) }

    val messages by chatViewModel.messages.collectAsState()

    // Listen to messages in real-time
    LaunchedEffect(chatId) {
        chatViewModel.loadChat(receiverId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .padding(12.dp)
    ) {
        Text(
            text = "Chat",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.3f))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                val isSentByMe = message.senderId == currentUserId
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = if (isSentByMe) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Surface(
                        color = if (isSentByMe) Color(0xFFDCF8C6) else Color.White,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            text = message.message,
                            modifier = Modifier.padding(10.dp),
                            color = Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                placeholder = { Text("Type a message...") },
                shape = RoundedCornerShape(24.dp)
            )
            Button(
                onClick = {
                    if (messageText.text.isNotBlank()) {
                        chatViewModel.sendMessage(
                            chatId = chatId,
                            senderId = currentUserId,
                            receiverId = receiverId,
                            message = messageText.text
                        )
                        messageText = TextFieldValue("")
                    } else {
                        Toast.makeText(context, "Enter a message", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Send")
            }
        }
    }
}
