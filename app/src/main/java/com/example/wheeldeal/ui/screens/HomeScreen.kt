package com.example.wheeldeal.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.ui.components.CarCard
import com.example.wheeldeal.ui.components.HeroSection
import com.example.wheeldeal.viewmodel.ListingViewModel
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: ListingViewModel = viewModel(),
    innerNav: NavHostController
) {
    val context = LocalContext.current
    var permissionDenied by remember { mutableStateOf(false) }
    val nearbyCars by viewModel.nearbyListings.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            permissionDenied = false
            fetchUserCity(context, viewModel)
        } else {
            permissionDenied = true
        }
    }

    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                fetchUserCity(context, viewModel)
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                (context as Activity),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                permissionDenied = true
            }
            else -> {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        HeroSection(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .padding(bottom = 16.dp)
        )
        // Main Content
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            // Permission Denied Message
            if (permissionDenied) {
                PermissionDeniedMessage {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Nearby Cars Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = "Nearby Cars",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Nearby Cars",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                }
            }

            when {
                nearbyCars.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No cars found in your area",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                else -> {
                    NearbyCarsList(
                        cars = nearbyCars,
                        innerNav = innerNav,  // Pass the NavController directly
                        modifier = Modifier
                    )
                }
                }
            }
        }


@Composable
private fun NearbyCarsList(
    cars: List<CarListing>,
    innerNav: NavHostController,  // Receive NavController directly
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(cars) { car ->
            CarCard(
                listing = car,
                isFavorite = false,
                onToggleFavorite = { /* Handle favorite toggle */ },
                onClick = {
                    try {
                        val json = Gson().toJson(car)
                        val encodedJson = Uri.encode(json)
                        innerNav.navigate("carDetails/$encodedJson") {
                            // Optional: Add navigation options
                            launchSingleTop = true
                        }
                        Log.d("Navigation", "Navigating to carDetails")
                    } catch (e: Exception) {
                        Log.e("Navigation", "Failed to navigate", e)
                    }
                },
                modifier = Modifier.width(280.dp)
            )
        }
    }
}

@Composable
private fun PermissionDeniedMessage(onRequestPermission: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Location permission required to show nearby cars",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Grant Permission")
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun fetchUserCity(context: Context, viewModel: ListingViewModel) {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                addresses?.firstOrNull()?.locality?.let { city ->
                    viewModel.loadNearbyListings(city)
                }
            } catch (e: Exception) {
                Log.e("HomeScreen", "Geocoder error", e)
            }
        }
    }.addOnFailureListener { e ->
        Log.e("HomeScreen", "Location error", e)
    }
}