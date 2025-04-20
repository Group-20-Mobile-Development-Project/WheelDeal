package com.example.wheeldeal.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.wheeldeal.model.CarListing
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun CarCard(
    modifier: Modifier = Modifier,
    listing: CarListing,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    var firstName by remember { mutableStateOf("Car Owner") }

    LaunchedEffect(listing.userId) {
        try {
            val doc = FirebaseFirestore.getInstance().collection("users").document(listing.userId).get().await()
            firstName = doc.getString("firstName") ?: "Car Owner"
        } catch (_: Exception) {
        }
    }

    Card(
        modifier = Modifier
            .width(180.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {
            // Name + Date Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = firstName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = formatTimeAgo(listing.createdAt.toDate().time),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Car image
            AsyncImage(
                model = listing.photos.firstOrNull(),
                contentDescription = "${listing.brand} ${listing.model}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${listing.brand} ${listing.model}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.Red else Color.Gray
                        )
                    }
                }

                Text(
                    text = "${listing.odometer} KM • ${listing.fuelType} • ${listing.location}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = formatCurrency(listing.price),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D2C44)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View car", color = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

// Format currency to EUR
fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.UK)
    return format.format(amount)
}

// Format "time ago" from timestamp
fun formatTimeAgo(timestamp: Long): String {
    val diffInMillis = System.currentTimeMillis() - timestamp
    val diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis)
    val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
    val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
    val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

    return when {
        diffInSeconds < 60 -> "$diffInSeconds seconds ago"
        diffInMinutes < 60 -> "$diffInMinutes minutes ago"
        diffInHours < 24 -> "$diffInHours hours ago"
        diffInDays < 30 -> "$diffInDays days ago"
        else -> {
            val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            format.format(Date(timestamp))
        }
    }
}


