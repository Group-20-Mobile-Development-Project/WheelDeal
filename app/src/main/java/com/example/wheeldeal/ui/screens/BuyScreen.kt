package com.example.wheeldeal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.viewmodel.ListingState
import com.example.wheeldeal.viewmodel.ListingViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun BuyScreen(viewModel: ListingViewModel = viewModel()) {
    val state by viewModel.listingState.collectAsState()

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
                            ListingCard(listing = listing)
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


@Composable
fun ListingCard(listing: CarListing) {
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
                Text(
                    text = "${listing.brand} ${listing.model} (${listing.year})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
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
