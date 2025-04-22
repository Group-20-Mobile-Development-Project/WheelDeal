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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.ui.components.CarCard
import com.example.wheeldeal.ui.components.HeroSection
import com.example.wheeldeal.ui.navigation.Screen
import com.example.wheeldeal.ui.theme.PrimaryColor
import com.example.wheeldeal.ui.theme.WhiteColor
import com.example.wheeldeal.viewmodel.ListingState
import com.example.wheeldeal.viewmodel.ListingViewModel
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import java.util.*

@Composable
fun HomeScreen(
    viewModel: ListingViewModel = viewModel(),
    innerNav: NavHostController
) {
    val context = LocalContext.current

    // 1) location‐based
    var permissionDenied by remember { mutableStateOf(false) }
    val nearbyCars by viewModel.nearbyListings.collectAsState()

    // 2) full master list
    val listingState by viewModel.listingState.collectAsState()
    val allCars: List<CarListing> = when (listingState) {
        is ListingState.Success -> (listingState as ListingState.Success).listings
        else                 -> emptyList()
    }

    // permission launcher
    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionDenied = !granted
        if (granted) fetchUserCity(context, viewModel)
    }

    // on first compose, check or ask
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            fetchUserCity(context, viewModel)
        } else {
            locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

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




    // ─── Nearby ───────────────────────────────────────────────────────────────
        Spacer(Modifier.height(24.dp))
        HomeSection(
            icon               = Icons.Default.DirectionsCar,
            title              = "Nearby Cars",
            cars               = nearbyCars,
            innerNav           = innerNav,
            permissionDenied   = permissionDenied,
            onRequestPermission= { locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
        )

        // ─── SUV ─────────────────────────────────────────────────────────────────
        Spacer(Modifier.height(32.dp))
        HomeSection(
            icon     = Icons.Default.FilterList,
            title    = "SUV Cars",
            subtitle = "Approved and inspected",
            cars     = allCars.filter { it.category.equals("SUV", true) },
            innerNav = innerNav
        )

        // ─── Sedan ──────────────────────────────────────────────────────────────
        Spacer(Modifier.height(32.dp))
        HomeSection(
            icon     = Icons.Default.FilterList,
            title    = "Sedan Cars",
            subtitle = "Approved and inspected",
            cars     = allCars.filter { it.category.equals("Sedan", true) },
            innerNav = innerNav
        )

        // ─── Hatchback ──────────────────────────────────────────────────────────
        Spacer(Modifier.height(32.dp))
        HomeSection(
            icon     = Icons.Default.FilterList,
            title    = "Hatchback Cars",
            subtitle = "Approved and inspected",
            cars     = allCars.filter { it.category.equals("Hatchback", true) },
            innerNav = innerNav
        )

        Spacer(Modifier.height(32.dp))
    }
}
}



@Composable
private fun HomeSection(
    icon: ImageVector,
    title: String,
    cars: List<CarListing>,
    innerNav: NavHostController,
    subtitle: String? = null,
    permissionDenied: Boolean = false,
    onRequestPermission: (() -> Unit)? = null
) {
    // header
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            subtitle?.let {
                Text(it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }

    // content
    when {
        // only for Nearby section
        permissionDenied && onRequestPermission != null -> {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Location permission required")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onRequestPermission) { Text("Grant") }
                }
            }
        }

        cars.isEmpty() -> {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No cars found")
            }
        }

        else -> {
            LazyRow(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(cars.take(5)) { car ->
                    CarCard(
                        listing = car,
                        isFavorite = false,
                        onToggleFavorite = { /*…*/ },
                        onClick = {
                            val payload = Uri.encode(Gson().toJson(car))
                            innerNav.navigate("carDetails/$payload") { launchSingleTop = true }
                        },
                        modifier = Modifier.width(150.dp)
                    )
                }
                // centered “See more →”
                item {
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
                            Text("See more",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward,
                                contentDescription = "See more",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun fetchUserCity(context: Context, viewModel: ListingViewModel) {
    val client = LocationServices.getFusedLocationProviderClient(context)
    client.lastLocation
        .addOnSuccessListener { loc ->
            loc?.let {
                runCatching {
                    val geo = Geocoder(context, Locale.getDefault())
                    geo.getFromLocation(it.latitude, it.longitude, 1)
                        ?.firstOrNull()?.locality
                        ?.let(viewModel::loadNearbyListings)
                }.onFailure { e -> Log.e("HomeScreen", "Geocoder error", e) }
            }
        }
        .addOnFailureListener { e -> Log.e("HomeScreen", "Location fetch failed", e) }
}
