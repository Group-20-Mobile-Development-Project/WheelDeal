package com.example.wheeldeal.ui.screens

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.core.view.WindowCompat
import com.example.wheeldeal.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.wheeldeal.viewmodel.ChatViewModel
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.lazy.rememberLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: String,
    receiverId: String,
    navController: NavHostController,
    chatViewModel: ChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    var draft by remember { mutableStateOf(TextFieldValue()) }
    val messages by chatViewModel.messages.collectAsState()
    val listState = rememberLazyListState()

    // 2) whenever the message list size changes, scroll to the newest
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            // with reverseLayout = true, the newest message lives at index 0
            listState.animateScrollToItem(0)
        }
    }

    LaunchedEffect(chatId) {
        if (chatId.isNotBlank()) chatViewModel.listenToMessages(chatId)
    }

    // Apply window configuration to handle system UI (status bar) overlap
    val activity = LocalActivity.current as ComponentActivity
    WindowCompat.setDecorFitsSystemWindows(activity.window, false)

    val displayName by produceState(initialValue = "", key1 = receiverId) {
        val doc = FirebaseFirestore.getInstance()
            .collection("users")
            .document(receiverId)
            .get()
            .await()
        val f = doc.getString("firstName").orEmpty()
        val l = doc.getString("lastName").orEmpty()
        value = listOf(f, l).filter(String::isNotBlank).joinToString(" ")
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(WhiteColor)
    ) {
        // Header
        Row(
            Modifier
                .fillMaxWidth()
                .background(FontIconColor)
                .padding(horizontal = 16.dp, vertical = 14.dp),
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
                displayName.ifBlank { "Chat" },
                style = AppTypography.headlineLarge.copy(
                    fontSize = 20.sp,
                    color = WhiteColor,
                    fontFamily = Poppins
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )

            IconButton(
                onClick = { /* TODO: call */ },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Filled.Call,
                    contentDescription = "Call",
                    tint = WhiteColor
                )
            }
        }

        Divider(color = Color.LightGray, thickness = 1.dp)

        // Message list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            state = listState,
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages.reversed()) { msg ->
                val mine = msg.senderId == currentUser
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        if (mine) Arrangement.End else Arrangement.Start
                ) {
                    // both sides use the same fully‑rounded shape
                    val bubbleShape = RoundedCornerShape(20.dp)
                    // mine = yellow, other = light surfaceVariant
                    val bubbleColor = if (mine)
                        PrimaryColor
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                    // ensure the text contrasts properly
                    val textColor = if (mine)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant

                    Surface(
                        shape = bubbleShape,
                        color = bubbleColor,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .widthIn(max = 280.dp)     // constrain width
                            .padding(vertical = 4.dp)  // spacing between rows
                    ) {
                        Text(
                            text = msg.message,
                            style = AppTypography.bodyMedium.copy(
                                fontFamily = if (mine) Poppins else Cabin
                            ),
                            color = textColor,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }

        Divider(color = Color.LightGray, thickness = 1.dp)

        // Input row
        Row(
            Modifier
                .fillMaxWidth()
                .background(WhiteColor)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = draft,
                onValueChange = { draft = it },
                placeholder = {
                    Text("Type a message...", style = AppTypography.bodyMedium.copy(
                        fontFamily = Cabin
                    ))
                },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 56.dp)
                    .imePadding(), // Apply imePadding to prevent keyboard overlap
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = WhiteColor,
                    unfocusedContainerColor = WhiteColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = AppTypography.bodyMedium.copy(fontFamily = Cabin),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (draft.text.isNotBlank()) {
                                chatViewModel.sendMessage(
                                    chatId = chatId,
                                    senderId = currentUser,
                                    receiverId = receiverId,
                                    message = draft.text
                                )
                                draft = TextFieldValue()
                            } else {
                                Toast.makeText(context, "Enter a message", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(PrimaryColor, shape = CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = FontIconColor
                        )
                    }
                }
            )
        }
    }
}
