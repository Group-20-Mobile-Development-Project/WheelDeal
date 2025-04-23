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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.ui.theme.FontIconColor
import com.example.wheeldeal.ui.theme.WhiteColor
import com.example.wheeldeal.ui.theme.Poppins
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.wheeldeal.viewmodel.ListingState
import com.example.wheeldeal.viewmodel.ListingViewModel
import com.google.firebase.auth.FirebaseAuth
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.wheeldeal.ui.theme.BackgroundWrapper
import com.example.wheeldeal.ui.theme.Cabin


@Composable
fun SellScreen(viewModel: ListingViewModel = viewModel()) {
    val context = LocalContext.current
    var location by remember { mutableStateOf("") }
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Check if location is enabled
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getUserLocation(fusedLocationClient, context) { address ->
                    location = extractCityFromAddress(address)
                    Toast.makeText(context, "Detected city: $location", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(
                    context,
                    "Please enable GPS location services",
                    Toast.LENGTH_LONG
                ).show()
                // Optionally open location settings
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else {
            Toast.makeText(
                context,
                "Location permission is required to detect your location",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    val listingState by viewModel.listingState.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    var attemptedSubmit by remember { mutableStateOf(false) }

    // Toggle for showing/hiding the form
    var showForm by remember { mutableStateOf(false) }

    var editMode by remember { mutableStateOf(false) }
    var editingListingId by remember { mutableStateOf("") }

    // Form fields (start blank)
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var transmission by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var engineCapacity by remember { mutableStateOf("") }
    var fuelType by remember { mutableStateOf("") }
    var avgMileage by remember { mutableStateOf("") }
    var odometer by remember { mutableStateOf("") }
    var accidents by remember { mutableStateOf("") }
    var seats by remember { mutableStateOf("") }
    var lastInspection by remember { mutableStateOf("") }
    var ownership by remember { mutableStateOf("") }

    var price by remember { mutableStateOf("") }
    var negotiable by remember { mutableStateOf(false) }
    var photoUrl by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Dropdown data
    val conditionOptions = listOf("New", "Used")
    val years = (2000..2025).map { it.toString() }
    val categoryOptions = listOf(
        "Sedan",
        "Hatchback",
        "SUV",
        "Coupe",
        "Convertible",
        "Luxury",
        "Van",
        "Truck"
    )
    val transmissions = listOf("Automatic", "Manual")
    val fuelTypes = listOf("Petrol", "Diesel", "Electric", "Hybrid")
    val accidentOptions = listOf("0", "1", "2", "3+")
    val seatOptions = (2..8).map { it.toString() }
    val ownerships = listOf("First Owner", "Second Owner", "Third Owner", "More")

    // Reset the form
    val resetForm = {
        brand = ""
        model = ""
        year = ""
        transmission = ""
        condition = ""
        category = ""
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
        attemptedSubmit = false
    }
    // 🔧 handleSubmit used correctly now
    val handleSubmit = let@{
        attemptedSubmit = true
        if (
            brand.isBlank() || model.isBlank() || year.isBlank() || condition.isBlank() || category.isBlank() ||
            transmission.isBlank() || price.isBlank() || color.isBlank() || engineCapacity.isBlank() ||
            fuelType.isBlank() || avgMileage.isBlank() || odometer.isBlank() || accidents.isBlank() ||
            seats.isBlank() || lastInspection.isBlank() || ownership.isBlank() || location.isBlank() ||
            photoUrl.isBlank() || description.isBlank()
        ) {
            Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return@let
        }
        val listing = CarListing(
            id = if (editMode) editingListingId else "",
            userId = currentUserId,
            brand = brand,
            model = model,
            year = year.toIntOrNull() ?: 0,
            category = category,
            condition = condition,
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
            description = description,
            city = location
        )

        if (editMode) {
            viewModel.updateListing(listing) { success ->
                Toast.makeText(context, if (success) "Listing updated!" else "Update failed", Toast.LENGTH_SHORT).show()
                if (success) resetForm()
            }
        } else {
            viewModel.addListing(listing) { success ->
                Toast.makeText(context, if (success) "Listing uploaded!" else "Failed", Toast.LENGTH_SHORT).show()
                if (success) resetForm()
            }
        }
    }

    // Main layout with lazy column
    BackgroundWrapper {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // (1) Title & Toggle Button
        item {
            Text(
                "Sell a Car",
                style = TextStyle(
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = FontIconColor
                )
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { showForm = !showForm },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FontIconColor)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = WhiteColor)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (showForm) "Hide Form" else "Create Listing",
                    color = WhiteColor,
                    style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Bold)
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        // (2) The Form (toggleable)
        if (showForm) {
            item {
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.cardColors(containerColor = WhiteColor)
                ) {
                    Column(
                        modifier            = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // — Vehicle Info —
                        Text("Vehicle Info", style = MaterialTheme.typography.titleLarge)
                        InputField("Brand", brand) { brand = it }
                        InputField("Model", model) { model = it }
                        DropdownField("Year", years, year) { year = it }
                        DropdownField("Category", categoryOptions, category) { category = it }

                        // — Specifications —
                        Text("Specifications", style = MaterialTheme.typography.titleLarge)
                        DropdownField("Condition", conditionOptions, condition) { condition = it }
                        DropdownField("Transmission", transmissions, transmission) { transmission = it }
                        InputField("Engine Capacity (cc)", engineCapacity, KeyboardType.Number) { engineCapacity = it }
                        DropdownField("Fuel Type", fuelTypes, fuelType) { fuelType = it }
                        InputField("Avg Mileage", avgMileage, KeyboardType.Number) { avgMileage = it }
                        InputField("Odometer (km)", odometer, KeyboardType.Number) { odometer = it }
                        DropdownField("Accidents", accidentOptions, accidents) { accidents = it }
                        DropdownField("Seats", seatOptions, seats) { seats = it }
                        InputField("Last Inspection", lastInspection) { lastInspection = it }
                        DropdownField("Ownership", ownerships, ownership) { ownership = it }

                        // — Location & Pricing —
                        Text("Location & Pricing", style = MaterialTheme.typography.titleLarge)
                        InputField("City", location) { location = it }
                        OutlinedButton(
                            onClick = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Place, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Detect My City")
                        }
                        InputField("Price", price, KeyboardType.Number) { price = it }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = negotiable, onCheckedChange = { negotiable = it })
                            Spacer(Modifier.width(4.dp))
                            Text("Negotiable?", color = FontIconColor)
                        }

                        // — Media & Description —
                        Text("Media & Description", style = MaterialTheme.typography.titleLarge)
                        InputField("Photo URL", photoUrl) { photoUrl = it }
                        InputField("Description", description, singleLine = false) { description = it }

                        // Submit button
                        Button(
                            onClick = handleSubmit,
                            modifier = Modifier.fillMaxWidth(),
                            colors   = ButtonDefaults.buttonColors(containerColor = FontIconColor)
                        ) {
                            Text("Submit", color = WhiteColor)
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        item {
            Text(
                text = "Your Listings",
                style = TextStyle(
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = FontIconColor
                ),
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 12.dp)
            )
        }
        val userListings = (listingState as? ListingState.Success)
            ?.listings
            ?.filter { it.userId == currentUserId }
            ?: emptyList()

        items(userListings) { listing ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    // 1) Photo + location overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        AsyncImage(
                            model = listing.photos.firstOrNull()
                                ?: "https://via.placeholder.com/400x200.png?text=No+Image",
                            contentDescription = "${listing.brand} ${listing.model}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )

                        // gradient for contrast
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                        startY = 120f
                                    )
                                )
                        )

                        // location pill
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                                .background(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Location",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = listing.location,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // 2) White body with Brand·Model, Year·Price, Edit/Delete all on one line
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(WhiteColor)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Left: Brand+Model then Year·Price
                            Column {
                                Text(
                                    text = "${listing.brand} ${listing.model}",
                                    style = TextStyle(
                                        fontFamily = Poppins,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp
                                    ),
                                    color = FontIconColor
                                )
                                Text(
                                    text = "Year: ${listing.year} | Price: $${"%,.2f".format(listing.price)}",
                                    style = TextStyle(
                                        fontFamily = Cabin,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    color = FontIconColor
                                )
                            }

                            // Right: Edit + Delete
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(onClick = {
                                    // populate form for edit
                                    brand = listing.brand
                                    model = listing.model
                                    year = listing.year.toString()
                                    category = listing.category
                                    condition = listing.condition
                                    transmission = listing.transmission
                                    color = listing.color
                                    engineCapacity = listing.engineCapacity.toString()
                                    fuelType = listing.fuelType
                                    avgMileage = listing.avgMileage.toString()
                                    odometer = listing.odometer.toString()
                                    accidents = listing.accidents.toString()
                                    seats = listing.seats.toString()
                                    lastInspection = listing.lastInspection
                                    ownership = listing.ownership
                                    location = listing.location
                                    price = listing.price.toString()
                                    negotiable = listing.negotiable
                                    photoUrl = listing.photos.firstOrNull() ?: ""
                                    description = listing.description
                                    editingListingId = listing.id
                                    showForm = true
                                    editMode = true
                                }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = FontIconColor
                                    )
                                }
                                IconButton(onClick = {
                                    viewModel.deleteListing(listing.id) { success ->
                                        Toast
                                            .makeText(
                                                context,
                                                if (success) "Deleted" else "Delete failed",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }



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
    isError: Boolean = false,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = FontIconColor) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        singleLine = singleLine,
        isError = isError,
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

@SuppressLint("MissingPermission")
fun getUserLocation(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    onAddressFound: (String) -> Unit
) {
    // Create location request for fresh location
    val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
        priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 10000
        fastestInterval = 5000
        numUpdates = 1
    }

    val locationCallback = object : com.google.android.gms.location.LocationCallback() {
        override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
            fusedLocationClient.removeLocationUpdates(this)
            locationResult.lastLocation?.let { location ->
                getAddressFromLocation(context, location, onAddressFound)
            } ?: run {
                onAddressFound("Unable to get current location")
            }
        }
    }

    // First try to get last location quickly
    fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
        if (lastLocation != null && isLocationFresh(lastLocation)) {
            getAddressFromLocation(context, lastLocation, onAddressFound)
        } else {
            // If no recent last location, request fresh updates
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                android.os.Looper.getMainLooper()
            )
        }
    }.addOnFailureListener {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            android.os.Looper.getMainLooper()
        )
    }
}

private fun isLocationFresh(location: Location): Boolean {
    return System.currentTimeMillis() - location.time < 1000 * 60 * 2 // 2 minutes old
}

fun getAddressFromLocation(context: Context, location: Location, onAddressFound: (String) -> Unit) {
    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            // Build string with just city and country
            val cityAndCountry = buildString {
                address.locality?.let { append(it) }  // City
                if (address.locality != null && address.countryName != null) {
                    append(", ")
                }
                address.countryName?.let { append(it) }  // Country
            }
            onAddressFound(cityAndCountry.ifEmpty { "Unknown location" })
        } else {
            onAddressFound("Unknown location")
        }
    } catch (e: Exception) {
        onAddressFound("Could not get address")
    }
}

private fun extractCityFromAddress(fullAddress: String): String {
    return fullAddress.split(",")
        .firstOrNull()  // Takes the part before first comma (e.g., "New York" from "New York, USA")
        ?.trim()        // Removes whitespace
        ?: fullAddress  // Fallback if no comma found
}
