package com.example.wheeldeal.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.DriveEta
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.ui.components.CarCard
import com.example.wheeldeal.ui.components.HeroSection
import com.example.wheeldeal.ui.navigation.Screen
import com.example.wheeldeal.ui.theme.FontIconColor
import com.example.wheeldeal.ui.theme.Poppins
import com.example.wheeldeal.ui.theme.PrimaryColor
import com.example.wheeldeal.ui.theme.WhiteColor
import com.example.wheeldeal.viewmodel.ListingState
import com.example.wheeldeal.viewmodel.ListingViewModel
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import java.util.*
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: ListingViewModel = viewModel(),
    innerNav: NavHostController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // State management
    var permissionDenied by remember { mutableStateOf(false) }
    val nearbyCars by viewModel.nearbyListings.collectAsState()
    val listingState by viewModel.listingState.collectAsState()

    // Process listings based on current state
    val allCars = when (listingState) {
        is ListingState.Success -> (listingState as ListingState.Success).listings
        else -> emptyList()
    }

    // Car categories extracted for reuse
    val suvCars = remember(allCars) { allCars.filter { it.category.equals("SUV", true) } }
    val sedanCars = remember(allCars) { allCars.filter { it.category.equals("Sedan", true) } }
    val hatchbackCars = remember(allCars) { allCars.filter { it.category.equals("Hatchback", true) } }

    // Permission launcher
    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionDenied = !granted
        if (granted) {
            coroutineScope.launch {
                fetchUserLocation(context, viewModel)
            }
        }
    }

    // Check location permission on first composition
    LaunchedEffect(Unit) {
        checkAndRequestLocationPermission(context, locationLauncher, viewModel)
    }

    // UI Structure
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryColor, WhiteColor)
                )
            )
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))
            HeroSection(modifier = Modifier.padding(horizontal = 6.dp))

            // Nearby Cars Section
            Spacer(Modifier.height(24.dp))
            HomeSection(
                icon = Icons.Default.LocationOn,  // Changed icon
                title = "Nearby Cars",
                cars = nearbyCars,
                innerNav = innerNav,
                permissionDenied = permissionDenied,
                onRequestPermission = { locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                titleFontSize = 24.sp,
                iconTint = Color(0xFF38AD00),
                tittleColor = FontIconColor,
                tittleFont = Poppins
            )

            // SUV Section
            Spacer(Modifier.height(32.dp))
            HomeSection(
                icon = Icons.Default.DriveEta,
                title = "SUV Cars",
                cars = suvCars,
                innerNav = innerNav,
                titleFontSize = 22.sp,
                iconTint = Color(0xFF3D5AFE) ,
                tittleColor = FontIconColor,
                tittleFont = Poppins
            )

            // Sedan Section
            Spacer(Modifier.height(32.dp))
            HomeSection(
                icon = Icons.Default.DirectionsCar,  // Kept original icon
                title = "Sedan Cars",
                cars = sedanCars,
                innerNav = innerNav,
                titleFontSize = 22.sp,
                iconTint = Color(0xFF00BFA5),
                tittleColor = FontIconColor,
                tittleFont = Poppins
            )

            // Hatchback Section
            Spacer(Modifier.height(32.dp))
            HomeSection(
                icon = Icons.Default.DirectionsCar,  // Changed icon
                title = "Hatchback Cars",
                cars = hatchbackCars,
                innerNav = innerNav,
                titleFontSize = 22.sp,
                iconTint = Color(0xFFFF6D00) ,
                tittleColor = FontIconColor,
                tittleFont = Poppins
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

/**
 * Displays a section of car listings with a title and optional subtitle
 */
@Composable
private fun HomeSection(
    icon: ImageVector,
    title: String,
    cars: List<CarListing>,
    innerNav: NavHostController,
    subtitle: String? = null,
    permissionDenied: Boolean = false,
    onRequestPermission: (() -> Unit)? = null,
    titleFontSize: TextUnit = 20.sp,  // Added parameter
    iconTint: Color = MaterialTheme.colorScheme.primary,  // Added parameter
    tittleColor: Color,
    tittleFont: FontFamily
) {
    // Section header
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(30.dp)  // Enhanced icon size
        )
        Spacer(Modifier.width(10.dp))  // Slightly more spacing
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,  // Enhanced font weight
                letterSpacing = 0.5.sp  // Added letter spacing
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }

    // Section content
    when {
        // Permission denied state (only for Nearby section)
        permissionDenied && onRequestPermission != null -> {
            PermissionRequestSection(onRequestPermission)
        }
        // Empty state
        cars.isEmpty() -> {
            EmptyStateSection()
        }
        // Cars available state
        else -> {
            CarListRow(cars, innerNav)
        }
    }
}

/**
 * Shows permission request UI when location permission is denied
 */
@Composable
private fun PermissionRequestSection(onRequestPermission: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Location permission required")
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRequestPermission) {
                Text("Grant")
            }
        }
    }
}

/**
 * Shows empty state when no cars are available in a section
 */
@Composable
private fun EmptyStateSection() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("No cars found")
    }
}

/**
 * Displays a horizontal row of car cards with a "See more" button
 */
@Composable
private fun CarListRow(cars: List<CarListing>, innerNav: NavHostController) {
    LazyRow(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(cars.take(5)) { car ->
            CarCard(
                listing = car,
                isFavorite = false,
                onToggleFavorite = { /* No functionality changes as requested */ },
                onClick = {
                    navigateToCarDetails(car, innerNav)
                },
                modifier = Modifier.width(250.dp)
            )
        }

        // "See more" button
        item {
            SeeMoreButton(innerNav)
        }
    }
}

/**
 * Button to navigate to the full listing page
 */
@Composable
private fun SeeMoreButton(innerNav: NavHostController) {
    Box(
        Modifier
            .width(150.dp)
            .height(280.dp)
            .clickable { innerNav.navigate(Screen.Buy.route) },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "See more",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp  // Enhanced font size
                )
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "See more",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Check and request location permission if necessary
 */
private fun checkAndRequestLocationPermission(
    context: Context,
    locationLauncher: androidx.activity.result.ActivityResultLauncher<String>,
    viewModel: ListingViewModel
) {
    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED

    if (hasPermission) {
        fetchUserLocation(context, viewModel)
    } else {
        locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

/**
 * Navigate to car details screen with car data
 */
private fun navigateToCarDetails(car: CarListing, innerNav: NavHostController) {
    val payload = Uri.encode(Gson().toJson(car))
    innerNav.navigate("carDetails/$payload") {
        launchSingleTop = true
    }
}

/**
 * Fetch user location and load nearby listings
 */
@SuppressLint("MissingPermission")
private fun fetchUserLocation(context: Context, viewModel: ListingViewModel) {
    val client = LocationServices.getFusedLocationProviderClient(context)
    client.lastLocation
        .addOnSuccessListener { location ->
            location?.let {
                runCatching {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    geocoder.getFromLocation(it.latitude, it.longitude, 1)
                        ?.firstOrNull()?.locality
                        ?.let { city -> viewModel.loadNearbyListings(city) }
                }.onFailure { error ->
                    Log.e("HomeScreen", "Geocoder error", error)
                }
            }
        }
        .addOnFailureListener { error ->
            Log.e("HomeScreen", "Location fetch failed", error)
        }
}