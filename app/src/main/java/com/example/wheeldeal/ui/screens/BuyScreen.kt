package com.example.wheeldeal.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.viewmodel.FavoritesViewModel
import com.example.wheeldeal.viewmodel.ListingState
import com.example.wheeldeal.viewmodel.ListingViewModel
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BuyScreen(
    viewModel: ListingViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.listingState.collectAsState()
    val favoriteIds by favoritesViewModel.favoriteIds.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .padding(16.dp)
    ) {
        when (state) {
            is ListingState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is ListingState.Error -> {
                val message = (state as ListingState.Error).message
                Text(
                    "Error loading listings: $message",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is ListingState.Success -> {
                val listings = (state as ListingState.Success).listings

                if (listings.isEmpty()) {
                    Text("No listings available.", modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn {
                        items(listings) { listing ->
                            ListingCard(
                                listing = listing,
                                isFavorite = favoriteIds.contains(listing.id),
                                onToggleFavorite = {
                                    favoritesViewModel.toggleFavorite(listing.id)
                                    Toast.makeText(
                                        context,
                                        if (favoriteIds.contains(listing.id)) "Removed from favorites" else "Added to favorites",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListingInfo(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "$label: ",
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF333333)
        )
        Text(text = value)
    }
}

fun formatCurrency(price: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    return formatter.format(price)
}

fun formatTimestamp(timestamp: Timestamp?): String {
    if (timestamp == null) return ""
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}

suspend fun fetchUserFullName(userId: String): String {
    return try {
        val doc = FirebaseFirestore.getInstance().collection("users").document(userId).get().await()
        val first = doc.getString("firstName") ?: ""
        val last = doc.getString("lastName") ?: ""
        "$first $last"
    } catch (e: Exception) {
        "Unknown"
    }
}

@Composable
fun ListingCard(
    listing: CarListing,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    var posterName by remember { mutableStateOf("") }

    LaunchedEffect(listing.userId) {
        posterName = fetchUserFullName(listing.userId)
    }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2ECF1)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            if (listing.photos.isNotEmpty()) {
                AsyncImage(
                    model = listing.photos.first(),
                    contentDescription = "Car Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image", color = Color.DarkGray)
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${listing.brand} ${listing.model} (${listing.year})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite Toggle",
                            tint = if (isFavorite) Color.Red else Color.Gray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Posted by $posterName · ${formatTimestamp(listing.createdAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(6.dp))

                ListingInfo(label = "Condition", value = listing.condition)
                ListingInfo(label = "Price", value = formatCurrency(listing.price))
                ListingInfo(label = "Negotiable", value = if (listing.negotiable) "Yes" else "No")
                ListingInfo(label = "Location", value = listing.location)
                ListingInfo(label = "Mileage", value = "${listing.avgMileage} km/l")
                ListingInfo(label = "Odometer", value = "${listing.odometer} km")
                ListingInfo(label = "Color", value = listing.color)
                ListingInfo(label = "Transmission", value = listing.transmission)
                ListingInfo(label = "Engine", value = "${listing.engineCapacity} cc")
                ListingInfo(label = "Fuel Type", value = listing.fuelType)
                ListingInfo(label = "Seats", value = listing.seats.toString())
                ListingInfo(label = "Accidents", value = listing.accidents.toString())
                ListingInfo(label = "Last Inspection", value = listing.lastInspection)
                ListingInfo(label = "Ownership", value = listing.ownership)

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Description: ${listing.description}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
        }
    }
}

