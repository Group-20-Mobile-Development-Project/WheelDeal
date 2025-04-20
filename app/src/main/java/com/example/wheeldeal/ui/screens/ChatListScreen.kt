package com.example.wheeldeal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.wheeldeal.model.Chat
import com.example.wheeldeal.repository.ChatRepository
import com.example.wheeldeal.ui.navigation.Screen
import com.example.wheeldeal.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ChatListScreen(navController: NavHostController) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    var chats by remember { mutableStateOf<List<Chat>>(emptyList()) }
    val scope = rememberCoroutineScope()

    // Load chat metadata once
    LaunchedEffect(currentUserId) {
        scope.launch {
            chats = ChatRepository().getChatsForUser(currentUserId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteColor)
    ) {
        // ─── Header ─────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FontIconColor)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = WhiteColor
                )
            }
            Text(
                text = "Your Chats",
                style = AppTypography.headlineLarge.copy(color = WhiteColor),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
        }
        // ─── Chat List ────
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(chats, key = { it.id }) { chat ->
                val otherId = if (chat.user1Id == currentUserId) chat.user2Id else chat.user1Id
                val displayName by produceState(initialValue = "", key1 = otherId) {
                    val doc = FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(otherId)
                        .get()
                        .await()
                    val f = doc.getString("firstName").orEmpty()
                    val l = doc.getString("lastName").orEmpty()
                    value = listOf(f, l)
                        .filter { it.isNotBlank() }
                        .joinToString(" ")
                        .ifBlank { otherId }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { navController.navigate(Screen.Chat.createRoute(chat.id, otherId)) },
                    shape = RoundedCornerShape(12.dp),
                    color = WhiteColor,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // avatar placeholder
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            color = PrimaryColor.copy(alpha = 0.2f)
                        ) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = null,
                                tint = FontIconColor,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = displayName,
                                style = AppTypography.bodyLarge.copy(
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Bold
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (chat.lastMessage.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = chat.lastMessage,
                                    style = AppTypography.bodyMedium.copy(
                                        fontFamily = Cabin,
                                        color = FontIconColor.copy(alpha = 0.7f)
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
