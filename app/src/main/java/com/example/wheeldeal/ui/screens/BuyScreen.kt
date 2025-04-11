package com.example.wheeldeal.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.viewmodel.FavoritesViewModel
import com.example.wheeldeal.viewmodel.ListingState
import com.example.wheeldeal.viewmodel.ListingViewModel
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

    var showFilters by remember { mutableStateOf(false) }
    var brand by remember { mutableStateOf("") }
    var transmission by remember { mutableStateOf("") }
    var fuelType by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var budget by remember { mutableFloatStateOf(50000f) }
    var appliedFilters by remember {
        mutableStateOf(Triple("", "", ""))
    }
    var appliedYear by remember { mutableStateOf("") }
    var appliedBudget by remember { mutableFloatStateOf(50000f) }
    var filtersApplied by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
            .padding(16.dp)
    ) {
        Button(
            onClick = { showFilters = !showFilters },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (showFilters) "Hide Filters" else "Show Filters")
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (showFilters) {
            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text("Brand") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(24.dp)
            )

            FilterDropdown("Transmission", listOf("Auto", "Manual"), transmission) {
                transmission = it
            }

            FilterDropdown("Fuel Type", listOf("Petrol", "Diesel", "Electric", "Hybrid"), fuelType) {
                fuelType = it
            }

            FilterDropdown("Year", (2000..2025).map { it.toString() }, year) {
                year = it
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Budget: $${budget.toInt()}")
            Slider(
                value = budget,
                onValueChange = { budget = it },
                valueRange = 1000f..1000000f,
                steps = 20
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    onClick = {
                        appliedFilters = Triple(brand.trim(), transmission.trim(), fuelType.trim())
                        appliedYear = year
                        appliedBudget = budget
                        filtersApplied = true
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Apply Filters")
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = {
                        brand = ""
                        transmission = ""
                        fuelType = ""
                        year = ""
                        budget = 50000f
                        appliedFilters = Triple("", "", "")
                        appliedYear = ""
                        appliedBudget = 50000f
                        filtersApplied = false
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Clear")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        when (state) {
            is ListingState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            is ListingState.Error -> {
                val message = (state as ListingState.Error).message
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: $message", color = Color.Red)
                }
            }

            is ListingState.Success -> {
                val listings = (state as ListingState.Success).listings

                val filtered = if (filtersApplied) {
                    listings.filter {
                        (appliedFilters.first.isBlank() || it.brand.equals(appliedFilters.first, ignoreCase = true)) &&
                                (appliedFilters.second.isBlank() || it.transmission == appliedFilters.second) &&
                                (appliedFilters.third.isBlank() || it.fuelType == appliedFilters.third) &&
                                (appliedYear.isBlank() || it.year.toString() == appliedYear) &&
                                (it.price <= appliedBudget)
                    }
                } else {
                    listings
                }

                if (filtered.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No listings found.")
                    }
                } else {
                    LazyColumn {
                        items(filtered) { listing ->
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Gray,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
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

                ListingInfo("Condition", listing.condition)
                ListingInfo("Price", formatCurrency(listing.price))
                ListingInfo("Negotiable", if (listing.negotiable) "Yes" else "No")
                ListingInfo("Location", listing.location)
                ListingInfo("Mileage", "${listing.avgMileage} km/l")
                ListingInfo("Odometer", "${listing.odometer} km")
                ListingInfo("Color", listing.color)
                ListingInfo("Transmission", listing.transmission)
                ListingInfo("Engine", "${listing.engineCapacity} cc")
                ListingInfo("Fuel Type", listing.fuelType)
                ListingInfo("Seats", listing.seats.toString())
                ListingInfo("Accidents", listing.accidents.toString())
                ListingInfo("Last Inspection", listing.lastInspection)
                ListingInfo("Ownership", listing.ownership)

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

@Composable
fun ListingInfo(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text("$label: ", fontWeight = FontWeight.SemiBold, color = Color(0xFF333333))
        Text(value)
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
    } catch (_: Exception) {
        "Unknown"
    }
}
