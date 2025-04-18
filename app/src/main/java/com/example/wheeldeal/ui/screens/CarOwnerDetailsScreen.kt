package com.example.wheeldeal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.wheeldeal.repository.ChatRepository
import com.example.wheeldeal.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CarOwnerDetailsScreen(
    listing: CarListing,
    navController: NavHostController
) {

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val isOwnListing = listing.userId == currentUserId
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("Car Owner") }

    LaunchedEffect(listing.userId) {
        try {
            val doc = FirebaseFirestore.getInstance().collection("users").document(listing.userId).get().await()
            email = doc.getString("email") ?: ""
            val first = doc.getString("firstName") ?: ""
            val last = doc.getString("lastName") ?: ""
            name = "$first $last".trim()
        } catch (_: Exception) {}
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(brush = Brush.verticalGradient(listOf(PrimaryColor, Color(0xFFF9F7EF))))
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.background(WhiteColor)) {
                    AsyncImage(
                        model = listing.photos.firstOrNull(),
                        contentDescription = "Car Image",
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "${listing.brand} ${listing.model} (${listing.year})",
                            fontFamily = Poppins,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = FontIconColor
                        )
                        Text("${listing.avgMileage} KM · ${listing.fuelType} · ${listing.location}", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row {
                            Column {
                                Text("Transmission", color = Color.Gray)
                                Text(listing.transmission, fontWeight = FontWeight.Bold, color = FontIconColor)
                            }
                            Spacer(modifier = Modifier.width(24.dp))
                            Column {
                                Text("Engine Capacity", color = Color.Gray)
                                Text("${listing.engineCapacity} cc", fontWeight = FontWeight.Bold, color = FontIconColor)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("€${listing.price}", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = FontIconColor)
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape).background(Color(0xFF003366)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Owner Icon", tint = WhiteColor)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = name, fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = FontIconColor)
                }

                Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = FontIconColor)
            }

            Spacer(modifier = Modifier.height(16.dp))

            InfoField(label = "E Mail ID", value = email.ifEmpty { "N/A" })
            InfoField(label = "Location", value = listing.location)

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    onClick = {
                        scope.launch {
                            runCatching {
                                val chatId = ChatRepository().createOrGetChatId(listing.userId)
                                navController.navigate(Screen.Chat.createRoute(chatId, listing.userId))
                            }
                        }
                    },
                    enabled = !isOwnListing,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FontIconColor),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat", tint = WhiteColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chat", fontWeight = FontWeight.Bold, color = WhiteColor)
                }

                Spacer(modifier = Modifier.width(16.dp))

                OutlinedButton(
                    onClick = { /* TODO: Call */ },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FontIconColor),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = FontIconColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Call", fontWeight = FontWeight.Bold)
                }
            }
            if (isOwnListing) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "This is your listing — you can't chat with yourself!",
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun InfoField(label: String, value: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        label = {
            Text(text = label, color = FontIconColor, fontFamily = Poppins)
        },
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = FontIconColor,
            focusedBorderColor = PrimaryColor,
            cursorColor = PrimaryColor
        )
    )
}
