package com.example.wheeldeal.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.ui.theme.FontIconColor
import com.example.wheeldeal.ui.theme.PrimaryColor
import com.example.wheeldeal.ui.theme.WhiteColor
import com.example.wheeldeal.viewmodel.ListingState
import com.example.wheeldeal.viewmodel.ListingViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SellScreen(viewModel: ListingViewModel = viewModel()) {
    val context = LocalContext.current
    val listingState by viewModel.listingState.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    // Toggle for showing/hiding the form
    var showForm by remember { mutableStateOf(false) }

    // Form fields (start blank)
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

    // Dropdown data
    val conditionOptions = listOf("New", "Used")
    val years = (2000..2025).map { it.toString() }
    val transmissions = listOf("Automatic", "Manual")
    val fuelTypes = listOf("Petrol", "Diesel", "Electric", "Hybrid")
    val accidentOptions = listOf("0", "1", "2", "3+")
    val seatOptions = (2..8).map { it.toString() }
    val ownerships = listOf("First Owner", "Second Owner", "Third Owner", "More")

    // Reset the form
    val resetForm = {
        condition = ""
        year = ""
        brand = ""
        model = ""
        transmission = ""
        color = ""
        engineCapacity = ""
        fuelType = ""
        avgMileage = ""
        odometer = ""
        accidents = ""
        seats = ""
        lastInspection = ""
        ownership = ""
        location = ""
        price = ""
        negotiable = false
        photoUrl = ""
        description = ""
    }

    // Main layout with lazy column
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryColor) // bright yellow
            .padding(16.dp)
    ) {
        // (1) Title & Toggle Button
        item {
            Text(
                "Create A Car Listing",
                style = MaterialTheme.typography.headlineSmall,
                color = FontIconColor
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { showForm = !showForm },
                colors = ButtonDefaults.buttonColors(containerColor = FontIconColor)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = WhiteColor)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (showForm) "Hide Form" else "Create Listing",
                    color = WhiteColor
                )
            }
        }

        // (2) The Form (toggleable)
        if (showForm) {
            item {
                Spacer(Modifier.height(12.dp))
                // White card for the form (no tint)
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Condition
                        DropdownField("Condition", conditionOptions, condition) { condition = it }
                        // Year
                        DropdownField("Year", years, year) { year = it }
                        // Brand
                        InputField("Brand", brand) { brand = it }
                        // Model
                        InputField("Model", model) { model = it }
                        // Transmission
                        DropdownField("Transmission", transmissions, transmission) { transmission = it }
                        // Color
                        InputField("Color", color) { color = it }
                        // Engine
                        InputField("Engine Capacity (cc)", engineCapacity, KeyboardType.Number) { engineCapacity = it }
                        // Fuel
                        DropdownField("Fuel Type", fuelTypes, fuelType) { fuelType = it }
                        // Mileage
                        InputField("Avg Mileage", avgMileage, KeyboardType.Number) { avgMileage = it }
                        // Odometer
                        InputField("Odometer (km)", odometer, KeyboardType.Number) { odometer = it }
                        // Accidents
                        DropdownField("Accidents", accidentOptions, accidents) { accidents = it }
                        // Seats
                        DropdownField("Seats", seatOptions, seats) { seats = it }
                        // Last Inspection
                        InputField("Last Inspection", lastInspection) { lastInspection = it }
                        // Ownership
                        DropdownField("Ownership", ownerships, ownership) { ownership = it }
                        // Location
                        InputField("Location", location) { location = it }
                        // Price
                        InputField("Price", price, KeyboardType.Number) { price = it }
                        // Negotiable
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Negotiable?", color = FontIconColor)
                            Checkbox(checked = negotiable, onCheckedChange = { negotiable = it })
                        }
                        // Photo
                        InputField("Photo URL", photoUrl) { photoUrl = it }
                        // Description
                        InputField("Description", description, singleLine = false) { description = it }

                        Spacer(Modifier.height(12.dp))

                        // Submit
                        Button(
                            onClick = {
                                val listing = CarListing(
                                    userId = currentUserId,
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
                                    Toast.makeText(context, if (success) "Listing uploaded!" else "Failed", Toast.LENGTH_SHORT).show()
                                    if (success) resetForm()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = FontIconColor)
                        ) {
                            Text("Submit", color = WhiteColor)
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        // (3) "Your Listings" section
        item {
            Text(
                "Your Listings",
                style = MaterialTheme.typography.titleMedium,
                color = FontIconColor,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items((listingState as? ListingState.Success)?.listings?.filter { it.userId == currentUserId } ?: emptyList()) { listing ->
            ListingCard(listing = listing)
        }
    }
}

// Basic single-line input field
@Composable
fun InputField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = FontIconColor) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        singleLine = singleLine,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

// Basic dropdown with trailing arrow
@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selected: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = { Text(label, color = FontIconColor) },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = FontIconColor
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = FontIconColor) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
