package com.example.wheeldeal.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.ui.theme.BackgroundWrapper
import com.example.wheeldeal.ui.theme.Cabin
import com.example.wheeldeal.ui.theme.FontIconColor
import com.example.wheeldeal.ui.theme.Poppins
import com.example.wheeldeal.ui.theme.WhiteColor
import com.example.wheeldeal.viewmodel.ListingState
import com.example.wheeldeal.viewmodel.ListingViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import android.app.DatePickerDialog
import android.widget.DatePicker                  // ← add this
import java.util.Calendar
import java.util.Locale

@SuppressLint("MissingPermission")
@OptIn(ExperimentalAnimationApi::class)
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
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getUserLocation(fusedLocationClient, context) { address ->
                    location = extractCityFromAddress(address)
                    Toast.makeText(context, "Detected city: $location", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Please enable GPS location services", Toast.LENGTH_LONG).show()
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else {
            Toast.makeText(context, "Location permission is required to detect your location", Toast.LENGTH_LONG).show()
        }
    }

    val listingState by viewModel.listingState.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    var attemptedSubmit by remember { mutableStateOf(false) }
    var showForm by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }
    var editingListingId by remember { mutableStateOf("") }

    // Form fields
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

    val calendar = Calendar.getInstance()

    val datePicker = remember {
        DatePickerDialog(
            context,
            { _, y, m, d ->
                lastInspection = "$d/${m+1}/$y"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var deletingListingId by remember { mutableStateOf<String?>(null) }


    // Dropdown data
    val conditionOptions = listOf("New", "Used")
    val years = (2000..2025).map { it.toString() }
    val categoryOptions = listOf("Sedan", "Hatchback", "SUV", "Coupe", "Convertible", "Luxury", "Van", "Truck")
    val transmissions = listOf("Automatic", "Manual")
    val fuelTypes = listOf("Petrol", "Diesel", "Electric", "Hybrid")
    val accidentOptions = listOf("0", "1", "2", "3+")
    val seatOptions = (2..8).map { it.toString() }
    val ownerships = listOf("First Owner", "Second Owner", "Third Owner", "More")

    // Reset form
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

    BackgroundWrapper {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // (1) Title & Toggle Button (Original Design)
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

            // (2) The Form (Updated to match the screenshot)
            if (showForm) {
                item {
                    Spacer(Modifier.height(12.dp))
                    AnimatedVisibility(
                        visible = showForm,
                        enter = expandVertically(animationSpec = tween(durationMillis = 300, easing = EaseOutCubic)) + fadeIn(animationSpec = tween(300)),
                        exit = shrinkVertically(animationSpec = tween(durationMillis = 300, easing = EaseInCubic)) + fadeOut(animationSpec = tween(300))
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(24.dp),
                                    spotColor = Color.Black.copy(alpha = 0.1f)
                                ),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = WhiteColor)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Form header
                                FormSectionHeader(
                                    title = if (editMode) "Edit Your Listing" else "Create New Listing",
                                    icon = Icons.Default.Edit
                                )

                                // Tabbed layout
                                var selectedTabIndex by remember { mutableIntStateOf(0) }
                                TabRow(
                                    selectedTabIndex = selectedTabIndex,
                                    containerColor = Color.Transparent,
                                    contentColor = FontIconColor,
                                    divider = {},
                                    indicator = { tabPositions ->
                                        Box(
                                            modifier = Modifier
                                                .tabIndicatorOffset(tabPositions[selectedTabIndex])
                                                .height(3.dp)
                                                .background(
                                                    color = FontIconColor,
                                                    shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                                                )
                                        )
                                    }
                                ) {
                                    Tab(
                                        selected = selectedTabIndex == 0,
                                        onClick = { selectedTabIndex = 0 },
                                        text = { Text("Vehicle", fontFamily = Poppins, fontWeight = FontWeight.SemiBold) }
                                    )
                                    Tab(
                                        selected = selectedTabIndex == 1,
                                        onClick = { selectedTabIndex = 1 },
                                        text = { Text("Specs", fontFamily = Poppins, fontWeight = FontWeight.SemiBold) }
                                    )
                                    Tab(
                                        selected = selectedTabIndex == 2,
                                        onClick = { selectedTabIndex = 2 },
                                        text = { Text("Pricing", fontFamily = Poppins, fontWeight = FontWeight.SemiBold) }
                                    )
                                    Tab(
                                        selected = selectedTabIndex == 3,
                                        onClick = { selectedTabIndex = 3 },
                                        text = { Text("Media", fontFamily = Poppins, fontWeight = FontWeight.SemiBold) }
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Tab content
                                when (selectedTabIndex) {
                                    0 -> {
                                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                            Text(
                                                "Vehicle Info",
                                                style = TextStyle(
                                                    fontFamily = Poppins,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 20.sp,
                                                    color = FontIconColor
                                                )
                                            )
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                InputField(
                                                    label = "Brand",
                                                    value = brand,
                                                    onValueChange = { brand = it },
                                                    isError = attemptedSubmit && brand.isBlank(),
                                                    modifier = Modifier.weight(1f)
                                                )
                                                InputField(
                                                    label = "Model",
                                                    value = model,
                                                    onValueChange = { model = it },
                                                    isError = attemptedSubmit && model.isBlank(),
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                DropdownField(
                                                    label = "Year",
                                                    options = years,
                                                    selected = year,
                                                    onValueChange = { year = it },
                                                    isError = attemptedSubmit && year.isBlank(),
                                                    modifier = Modifier.weight(1f)
                                                )
                                                InputField(
                                                    label = "Color",
                                                    value = color,
                                                    onValueChange = { color = it },
                                                    isError = attemptedSubmit && color.isBlank(),
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                            DropdownField(
                                                label = "Category",
                                                options = categoryOptions,
                                                selected = category,
                                                onValueChange = { category = it },
                                                isError = attemptedSubmit && category.isBlank()
                                            )
                                        }
                                    }
                                    1 -> {
                                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                            Text(
                                                "Specifications",
                                                style = TextStyle(
                                                    fontFamily = Poppins,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 20.sp,
                                                    color = FontIconColor
                                                )
                                            )
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                DropdownField(
                                                    label = "Condition",
                                                    options = conditionOptions,
                                                    selected = condition,
                                                    onValueChange = { condition = it },
                                                    isError = attemptedSubmit && condition.isBlank(),
                                                    modifier = Modifier.weight(1f)
                                                )
                                                DropdownField(
                                                    label = "Transmission",
                                                    options = transmissions,
                                                    selected = transmission,
                                                    onValueChange = { transmission = it },
                                                    isError = attemptedSubmit && transmission.isBlank(),
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                InputField(
                                                    label = "Engine Capacity",
                                                    value = engineCapacity,
                                                    keyboardType = KeyboardType.Number,
                                                    onValueChange = { engineCapacity = it },
                                                    isError = attemptedSubmit && engineCapacity.isBlank(),
                                                    modifier = Modifier.weight(1f)
                                                )
                                                DropdownField(
                                                    label = "Fuel Type",
                                                    options = fuelTypes,
                                                    selected = fuelType,
                                                    onValueChange = { fuelType = it },
                                                    isError = attemptedSubmit && fuelType.isBlank(),
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                InputField(
                                                    label = "Avg Mileage",
                                                    value = avgMileage,
                                                    keyboardType = KeyboardType.Number,
                                                    onValueChange = { avgMileage = it },
                                                    isError = attemptedSubmit && avgMileage.isBlank(),
                                                    modifier = Modifier.weight(1f)
                                                )
                                                InputField(
                                                    label = "Odometer (km)",
                                                    value = odometer,
                                                    keyboardType = KeyboardType.Number,
                                                    onValueChange = { odometer = it },
                                                    isError = attemptedSubmit && odometer.isBlank(),
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                DropdownField(
                                                    label = "Accidents",
                                                    options = accidentOptions,
                                                    selected = accidents,
                                                    onValueChange = { accidents = it },
                                                    isError = attemptedSubmit && accidents.isBlank(),
                                                    modifier = Modifier.weight(1f)
                                                )
                                                DropdownField(
                                                    label = "Seats",
                                                    options = seatOptions,
                                                    selected = seats,
                                                    onValueChange = { seats = it },
                                                    isError = attemptedSubmit && seats.isBlank(),
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                OutlinedTextField(
                                                    value = lastInspection,
                                                    onValueChange = { },
                                                    readOnly = true,
                                                    isError = attemptedSubmit && lastInspection.isBlank(),
                                                    label = { Text("Inspection") },
                                                    modifier = Modifier.weight(1f),
                                                    trailingIcon = {
                                                        IconButton(onClick = { datePicker.show() }) {
                                                            Icon(Icons.Default.DateRange, contentDescription = null)
                                                        }
                                                    },
                                                    shape = RoundedCornerShape(12.dp),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedTextColor     = FontIconColor,
                                                        unfocusedTextColor   = FontIconColor,
                                                        focusedLabelColor    = FontIconColor,
                                                        unfocusedLabelColor  = FontIconColor,
                                                        focusedBorderColor   = FontIconColor,
                                                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                                                        errorBorderColor     = MaterialTheme.colorScheme.error,
                                                        focusedContainerColor   = Color.White,
                                                        unfocusedContainerColor = Color.White,
                                                        cursorColor         = FontIconColor,
                                                        errorCursorColor    = MaterialTheme.colorScheme.error
                                                    ),
                                                    textStyle = TextStyle(fontFamily = Cabin, fontSize = 16.sp)
                                                )


                                                DropdownField(
                                                    label = "Ownership",
                                                    options = ownerships,
                                                    selected = ownership,
                                                    onValueChange = { ownership = it },
                                                    isError = attemptedSubmit && ownership.isBlank(),
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    }
                                    2 -> {
                                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                            Text(
                                                "Location & Pricing",
                                                style = TextStyle(
                                                    fontFamily = Poppins,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 20.sp,
                                                    color = FontIconColor
                                                )
                                            )
                                            InputField(
                                                label = "City",
                                                value = location,
                                                onValueChange = { location = it },
                                                isError = attemptedSubmit && location.isBlank()
                                            )
                                            OutlinedButton(
                                                onClick = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(56.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.dp, FontIconColor),
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    contentColor = FontIconColor
                                                )
                                            ) {
                                                Icon(Icons.Default.Place, contentDescription = null)
                                                Spacer(Modifier.width(4.dp))
                                                Text("Detect My City", color = FontIconColor)
                                            }
                                            InputField(
                                                label = "Price",
                                                value = price,
                                                keyboardType = KeyboardType.Number,
                                                onValueChange = { price = it },
                                                isError = attemptedSubmit && price.isBlank(),
                                                prefix = "€"
                                            )
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp)
                                                    .background(Color(0xFFF5F7F9), shape = RoundedCornerShape(12.dp))
                                                    .padding(12.dp)
                                            ) {
                                                Checkbox(
                                                    checked = negotiable,
                                                    onCheckedChange = { negotiable = it }
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Text(
                                                    "Negotiable?",
                                                    color = FontIconColor,
                                                    style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Medium)
                                                )
                                            }
                                        }
                                    }
                                    3 -> {
                                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                            Text(
                                                "Media & Description",
                                                style = TextStyle(
                                                    fontFamily = Poppins,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 20.sp,
                                                    color = FontIconColor
                                                )
                                            )
                                            InputField(
                                                label = "Photo URL",
                                                value = photoUrl,
                                                onValueChange = { photoUrl = it },
                                                isError = attemptedSubmit && photoUrl.isBlank()
                                            )
                                            InputField(
                                                label = "Description",
                                                value = description,
                                                onValueChange = { description = it },
                                                singleLine = false,
                                                isError = attemptedSubmit && description.isBlank(),
                                                minLines = 4
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Submit button
                                Button(
                                    onClick = handleSubmit,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = FontIconColor),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
                                ) {
                                    Text(
                                        if (editMode) "Update Listing" else "Create Listing",
                                        style = TextStyle(
                                            fontFamily = Poppins,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = WhiteColor
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }

            // (3) Your Listings Section (Original Design)
            item {
                Text(
                    text = "Your Listings",
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = FontIconColor
                    ),
                    modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
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
                                        text = "Year: ${listing.year} | Price: €${"%,.2f".format(listing.price)}",
                                        style = TextStyle(
                                            fontFamily = Poppins,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 16.sp
                                        ),
                                        color = FontIconColor
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(onClick = {
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
                                        // instead of calling deleteListing() immediately:
                                        deletingListingId = listing.id
                                        showDeleteDialog     = true
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
        // 3️⃣ Add this *after* your LazyColumn, but still inside BackgroundWrapper
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = {
                    Text("Delete Listing?", fontWeight = FontWeight.Bold)
                },
                text = {
                    Text("This listing will be permanently removed and cannot be undone.")
                },
                confirmButton = {
                    TextButton(onClick = {
                        // actually delete
                        deletingListingId?.let { id ->
                            viewModel.deleteListing(id) { success ->
                                Toast.makeText(
                                    context,
                                    if (success) "Listing deleted" else "Delete failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        showDeleteDialog = false
                    }) {
                        Text("Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}



@Composable
fun FormSectionHeader(
    title: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = FontIconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = FontIconColor
            )
        )
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    isError: Boolean = false,
    minLines: Int = 1,
    prefix: String = "",
    modifier: Modifier = Modifier.fillMaxWidth(),
    onValueChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                color = if (isError) MaterialTheme.colorScheme.error else FontIconColor,
                fontFamily = Poppins,
                fontSize = 14.sp
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        singleLine = singleLine,
        isError = isError,
        minLines = minLines,
        modifier = modifier
            .padding(vertical = 4.dp)
            .focusRequester(focusRequester),
        textStyle = TextStyle(fontFamily = Cabin, fontSize = 16.sp),
        prefix = if (prefix.isNotEmpty()) {
            { Text(text = prefix, fontWeight = FontWeight.Bold) }
        } else null,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FontIconColor,
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedContainerColor = Color(0xFFFFFFFF),
            unfocusedContainerColor = Color(0xFFFFFFFF)
        )
    )
}

@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selected: String,
    isError: Boolean = false,
    modifier: Modifier = Modifier.fillMaxWidth(),
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = {
                Text(
                    label,
                    color = if (isError) MaterialTheme.colorScheme.error else FontIconColor,
                    fontFamily = Poppins,
                    fontSize = 14.sp
                )
            },
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
            isError = isError,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            textStyle = TextStyle(fontFamily = Cabin, fontSize = 16.sp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FontIconColor,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedContainerColor = Color(0xFFFFFFFF),
                unfocusedContainerColor = Color(0xFFFFFFFF)
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(WhiteColor)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontFamily = Cabin, color = FontIconColor) },
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

    fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
        if (lastLocation != null && isLocationFresh(lastLocation)) {
            getAddressFromLocation(context, lastLocation, onAddressFound)
        } else {
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
            val cityAndCountry = buildString {
                address.locality?.let { append(it) }
                if (address.locality != null && address.countryName != null) {
                    append(", ")
                }
                address.countryName?.let { append(it) }
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
        .firstOrNull()
        ?.trim()
        ?: fullAddress
}