package com.example.wheeldeal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.navigation.NavHostController
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
                .background(
                    brush = Brush.verticalGradient(
                        listOf(PrimaryColor, Color(0xFFF9F7EF))
                    )
                )
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Enhanced Car Card
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.background(WhiteColor)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    ) {
                        AsyncImage(
                            model = listing.photos.firstOrNull(),
                            contentDescription = "Car Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        )
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        // Title and Price Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${listing.brand} ${listing.model}",
                                fontFamily = Poppins,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = FontIconColor
                            , )
                            Text(
                                text = "€${"%,.0f".format(listing.price)}",
                                fontFamily = Poppins,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = FontIconColor
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Specs Grid
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            SpecItem(
                                label = "Transmission",
                                value = listing.transmission,
                                icon = Icons.Default.Settings
                            )
                            SpecItem(
                                label = "Fuel Type",
                                value = listing.fuelType,
                                icon = Icons.Default.LocalGasStation
                            )
                            SpecItem(
                                label = "Location",
                                value = listing.location,
                                icon = Icons.Default.Place,
                            )
                        }
                    }
                }
            }
                    // Owner Info Section
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF003366)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Owner Icon",
                                    tint = WhiteColor
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = name,
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = FontIconColor
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = FontIconColor
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Contact Info
                    InfoField(label = "E Mail ID", value = email.ifEmpty { "N/A" })
                    InfoField(label = "Location", value = listing.location)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
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
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FontIconColor),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "Chat",
                                tint = WhiteColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Chat", fontWeight = FontWeight.Bold, color = WhiteColor)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        OutlinedButton(
                            onClick = { /* TODO: Call */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
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
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                    }
                }
    }
}

@Composable
private fun SpecItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = FontIconColor.copy(alpha = 0.8f),
            modifier = Modifier.size(20.dp))
        Text(
            text = value,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            color = FontIconColor,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp))
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 12.sp)
    }
}

@Composable
fun InfoField(
    label: String,
    value: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
        .padding(vertical = 6.dp),
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