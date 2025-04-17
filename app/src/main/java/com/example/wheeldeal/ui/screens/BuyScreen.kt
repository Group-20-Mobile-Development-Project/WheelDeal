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
import com.example.wheeldeal.viewmodel.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import androidx.navigation.NavController
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import com.example.wheeldeal.ui.navigation.Screen
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import com.example.wheeldeal.ui.components.CarCard

@Composable
fun BuyScreen(
    navController: NavController,
    viewModel: ListingViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel = viewModel(),
    filterViewModel: BuyFilterViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.listingState.collectAsState()
    val favoriteIds by favoritesViewModel.favoriteIds.collectAsState()
    val filters by filterViewModel.filters.collectAsState()

    var localBrand by remember { mutableStateOf(filters.brand) }
    var localTransmission by remember { mutableStateOf(filters.transmission) }
    var localFuelType by remember { mutableStateOf(filters.fuelType) }
    var localYear by remember { mutableStateOf(filters.year) }
    var localBudget by remember { mutableFloatStateOf(filters.budget) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
            .padding(16.dp)
    ) {
        Button(
            onClick = { filterViewModel.toggleFilterSection() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (filters.showFilters) "Hide Filters" else "Show Filters")
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filters.showFilters) {
            OutlinedTextField(
                value = localBrand,
                onValueChange = { localBrand = it },
                label = { Text("Brand") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(24.dp)
            )
            FilterDropdown("Transmission", listOf("Auto", "Manual"), localTransmission) {
                localTransmission = it
            }
            FilterDropdown("Fuel Type", listOf("Petrol", "Diesel", "Electric", "Hybrid"), localFuelType) {
                localFuelType = it
            }
            FilterDropdown("Year", (2000..2025).map { it.toString() }, localYear) {
                localYear = it
            }

            Text("Budget: $${localBudget.toInt()}")
            Slider(
                value = localBudget,
                onValueChange = { localBudget = it },
                valueRange = 1000f..1000000f,
                steps = 20
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = {
                    localBrand = ""
                    localTransmission = ""
                    localFuelType = ""
                    localYear = ""
                    localBudget = 50000f
                }) {
                    Text("Clear")
                }

                Button(onClick = {
                    filterViewModel.updateAllFilters(
                        brand = localBrand,
                        transmission = localTransmission,
                        fuelType = localFuelType,
                        year = localYear,
                        budget = localBudget
                    )
                }) {
                    Text("Apply Filters")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
        when (state) {
            is ListingState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            is ListingState.Error -> {
                val msg = (state as ListingState.Error).message
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error loading listings: $msg", color = Color.Red)
                }
            }

            is ListingState.Success -> {
                val listings = (state as ListingState.Success).listings
                val filtered = filterViewModel.filterListings(listings)

                if (filtered.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No listings found.")
                    }
                } else {
                    Column {
                        Spacer(modifier = Modifier.height(24.dp))



                        // Vertical scrolling with two cards in each horizontal row
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(filtered) { listing ->
                                CarCard(
                                    listing = listing,
                                    isFavorite = favoriteIds.contains(listing.id),
                                    onToggleFavorite = {
                                        favoritesViewModel.toggleFavorite(listing.id)
                                        Toast.makeText(
                                            context,
                                            if (favoriteIds.contains(listing.id)) "Removed from favorites" else "Added to favorites",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onClick = {
                                        val json = Gson().toJson(listing)
                                        navController.navigate(Screen.CarDetails.createRoute(json))
                                    }
                                )
                            }
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
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(24.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
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

@Composable
fun ListingCard(
    listing: CarListing,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    var posterName by remember { mutableStateOf("") }

    LaunchedEffect(listing.userId) {
        posterName = fetchUserFullName(listing.userId)
    }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
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