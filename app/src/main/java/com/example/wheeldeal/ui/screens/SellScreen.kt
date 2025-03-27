package com.example.wheeldeal.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.viewmodel.ListingViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SellScreen(viewModel: ListingViewModel = viewModel()) {
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val listingsState by viewModel.listingState.collectAsState()
    var showForm by remember { mutableStateOf(false) }

    // Form states
    var condition by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var transmission by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var engineCapacity by remember { mutableStateOf("") }
    var fuelType by remember { mutableStateOf("") }
    var avgMileage by remember { mutableStateOf("") }
    var odometer by remember { mutableStateOf("") }
    var accidents by remember { mutableStateOf("") }
    var seats by remember { mutableStateOf("") }
    var lastInspection by remember { mutableStateOf("") }
    var ownership by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var negotiable by remember { mutableStateOf(false) }
    var photoUrl by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val resetForm = {
        condition = ""; year = ""; brand = ""; model = ""; transmission = ""; color = ""
        engineCapacity = ""; fuelType = ""; avgMileage = ""; odometer = ""; accidents = ""
        seats = ""; lastInspection = ""; ownership = ""; location = ""; price = ""
        negotiable = false; photoUrl = ""; description = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Section
        Text("List Your Car", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { showForm = !showForm },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(if (showForm) "Hide Form" else "+ Create Listing")
        }

        // Middle Section - Form
        if (showForm) {
            Spacer(Modifier.height(16.dp))
            ListingField("Condition (New/Used)", condition) { condition = it }
            ListingField("Year", year, KeyboardType.Number) { year = it }
            ListingField("Brand", brand) { brand = it }
            ListingField("Model", model) { model = it }
            ListingField("Transmission", transmission) { transmission = it }
            ListingField("Color", color) { color = it }
            ListingField("Engine Capacity (cc)", engineCapacity, KeyboardType.Number) { engineCapacity = it }
            ListingField("Fuel Type", fuelType) { fuelType = it }
            ListingField("Avg Mileage (km/l)", avgMileage, KeyboardType.Number) { avgMileage = it }
            ListingField("Odometer (km)", odometer, KeyboardType.Number) { odometer = it }
            ListingField("Accidents", accidents, KeyboardType.Number) { accidents = it }
            ListingField("Seats", seats, KeyboardType.Number) { seats = it }
            ListingField("Last Inspection", lastInspection) { lastInspection = it }
            ListingField("Ownership", ownership) { ownership = it }
            ListingField("Location", location) { location = it }
            ListingField("Price", price, KeyboardType.Number) { price = it }
            ListingField("Photo URL (optional)", photoUrl) { photoUrl = it }
            ListingField("Description", description) { description = it }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = negotiable, onCheckedChange = { negotiable = it })
                Text("Is Price Negotiable?")
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    if (
                        condition.isNotBlank() && year.isNotBlank() && brand.isNotBlank() &&
                        model.isNotBlank() && transmission.isNotBlank() && color.isNotBlank() &&
                        engineCapacity.isNotBlank() && fuelType.isNotBlank() &&
                        avgMileage.isNotBlank() && odometer.isNotBlank() &&
                        accidents.isNotBlank() && seats.isNotBlank() &&
                        lastInspection.isNotBlank() && ownership.isNotBlank() &&
                        location.isNotBlank() && price.isNotBlank()
                    ) {
                        val listing = CarListing(
                            condition = condition,
                            year = year.toIntOrNull() ?: 0,
                            brand = brand,
                            model = model,
                            transmission = transmission,
                            color = color,
                            engineCapacity = engineCapacity.toIntOrNull() ?: 0,
                            fuelType = fuelType,
                            avgMileage = avgMileage.toIntOrNull() ?: 0,
                            odometer = odometer.toIntOrNull() ?: 0,
                            accidents = accidents.toIntOrNull() ?: 0,
                            seats = seats.toIntOrNull() ?: 0,
                            lastInspection = lastInspection,
                            ownership = ownership,
                            location = location,
                            price = price.toDoubleOrNull() ?: 0.0,
                            negotiable = negotiable,
                            photos = if (photoUrl.isNotBlank()) listOf(photoUrl) else emptyList(),
                            description = description
                        )

                        viewModel.addListing(listing) { success ->
                            if (success) {
                                Toast.makeText(context, "Listing uploaded!", Toast.LENGTH_SHORT).show()
                                resetForm()
                                showForm = false
                            } else {
                                Toast.makeText(context, "Failed to upload listing", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Post Listing")
            }
        }

        // Bottom Section - User Listings
        Spacer(Modifier.height(32.dp))
        Text("Your Listings", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        val userListings = (listingsState as? com.example.wheeldeal.viewmodel.ListingState.Success)
            ?.listings
            ?.filter { it.userId == userId }

        if (userListings.isNullOrEmpty()) {
            Text("You have not listed any cars yet.")
        } else {
            userListings.forEach { listing ->
                Text("${listing.brand} ${listing.model} - ₹${listing.price}", modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun ListingField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}
