@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)

package com.example.wheeldeal.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.ui.components.CarCard
import com.example.wheeldeal.ui.navigation.Screen
import com.example.wheeldeal.ui.theme.BackgroundWrapper
import com.example.wheeldeal.ui.theme.DarkBlue
import com.example.wheeldeal.ui.theme.FontIconColor
import com.example.wheeldeal.ui.theme.WhiteColor
import com.example.wheeldeal.viewmodel.BuyFilterViewModel
import com.example.wheeldeal.viewmodel.FavoritesViewModel
import com.example.wheeldeal.viewmodel.ListingState
import com.example.wheeldeal.viewmodel.ListingViewModel

@Composable
fun BuyScreen(
    navController: NavController,
    viewModel: ListingViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel = viewModel(),
    filterViewModel: BuyFilterViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.listingState.collectAsState()
    val favIds by favoritesViewModel.favoriteIds.collectAsState()
    val filters by filterViewModel.filters.collectAsState()

    // Local drafts for the filter inputs - keeping exactly as original
    var localBrand by remember { mutableStateOf(filters.brand) }
    var localCategory by remember { mutableStateOf(filters.category) }
    var localTransmission by remember { mutableStateOf(filters.transmission) }
    var localFuelType by remember { mutableStateOf(filters.fuelType) }
    var localYear by remember { mutableStateOf(filters.year) }
    var localBudget by remember { mutableFloatStateOf(filters.budget) }
    var selectedSort by remember { mutableStateOf(
        when (filters.sortType) {
            "price_asc" -> "Price: Low to High"
            "price_desc" -> "Price: High to Low"
            else -> "Default"
        }
    )}

    BackgroundWrapper {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                .padding(16.dp)
        ) {
            // Header section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Car Marketplace",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue
                    )
                )
                Text(
                    "Browse all available cars",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DarkBlue.copy(alpha = 0.7f)
                    )
                )
            }

            // Filter and Sort row
            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { filterViewModel.toggleFilterSection() },
                    modifier = Modifier.weight(0.8f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkBlue,
                        contentColor = WhiteColor
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (filters.showFilters) "Hide Filters" else "Show Filters")
                    }
                }

                var expanded by remember { mutableStateOf(false) }
                val sortOptions = listOf("Default", "Price: Low to High", "Price: High to Low")

                Box(
                    modifier = Modifier.weight(0.2f)
                        .fillMaxHeight()
                ) {
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkBlue, RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort",
                            tint = WhiteColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(WhiteColor)
                    ) {
                        sortOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        option,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                },
                                onClick = {
                                    selectedSort = option
                                    expanded = false
                                    filterViewModel.updateSort(
                                        when (option) {
                                            "Price: Low to High" -> "price_asc"
                                            "Price: High to Low" -> "price_desc"
                                            else -> "default"
                                        }
                                    )
                                    filterViewModel.applyFilters()
                                }
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            // Enhanced filter inputs with animation
            AnimatedVisibility(
                visible = filters.showFilters,
                enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = WhiteColor,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Filter Options",
                            fontWeight = FontWeight.Bold,
                            color = DarkBlue
                        )

                        Spacer(Modifier.height(16.dp))

                        // Brand input - kept exactly as original function call
                        BrandInput(localBrand) { localBrand = it }

                        Spacer(Modifier.height(8.dp))

                        // Category - kept exactly as original function call
                        FilterDropdown("Category", listOf(
                            "Sedan", "Hatchback", "SUV", "Coupe",
                            "Convertible", "Van", "Truck"
                        ), localCategory) {
                            localCategory = it
                        }

                        Spacer(Modifier.height(8.dp))

                        // Transmission - kept exactly as original function call
                        FilterDropdown("Transmission", listOf("Automatic", "Manual"), localTransmission) {
                            localTransmission = it
                        }

                        Spacer(Modifier.height(8.dp))

                        // Fuel Type - kept exactly as original function call
                        FilterDropdown("Fuel Type", listOf("Petrol", "Diesel", "Electric", "Hybrid"), localFuelType) {
                            localFuelType = it
                        }

                        Spacer(Modifier.height(8.dp))

                        // Year - kept exactly as original function call
                        FilterDropdown("Year", (2000..2025).map { it.toString() }, localYear) {
                            localYear = it
                        }

                        Spacer(Modifier.height(16.dp))

                        // Budget section with better formatting
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Budget: \u20ac${localBudget.toInt()}",
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            // Original slider with original properties
                            Slider(
                                value = localBudget,
                                onValueChange = { localBudget = it },
                                valueRange = 1_000f..1_000_000f,
                                steps = 20,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            // Budget range labels
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "€1,000",
                                    color = FontIconColor.copy(alpha = 0.7f)
                                )

                                Text(
                                    text = "€1,000,000",
                                    color = FontIconColor.copy(alpha = 0.7f)
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Button row - kept exactly the same logic
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    localBrand = ""
                                    localCategory = ""
                                    localTransmission = ""
                                    localFuelType = ""
                                    localYear = ""
                                    localBudget = 50_000f
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Clear")
                            }

                            Button(
                                onClick = {
                                    filterViewModel.updateAllFilters(
                                        brand = localBrand,
                                        category = localCategory,
                                        transmission = localTransmission,
                                        fuelType = localFuelType,
                                        year = localYear,
                                        budget = localBudget
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DarkBlue,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Apply Filters")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Content based on loading / error / success - kept as original with minor visual improvements
            when (state) {
                ListingState.Loading -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = DarkBlue)
                }

                is ListingState.Error -> {
                    val msg = (state as ListingState.Error).message
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(168.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Error loading listings: $msg",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                is ListingState.Success -> {
                    val all = (state as ListingState.Success).listings
                    val filtered = filterViewModel.filterListings(all)

                    if (filtered.isEmpty()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            color = WhiteColor,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .height(168.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No listings found.")
                            }
                        }
                    } else {
                        // Break the full list into rows - kept as original
                        val rows: List<List<CarListing>> = filtered.chunked(4)

                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rows.forEach { rowItems ->
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(rowItems) { listing ->
                                        CarCard(
                                            modifier = Modifier
                                                .width(250.dp)
                                                .padding(vertical = 4.dp),
                                            listing = listing,
                                            isFavorite = favIds.contains(listing.id),
                                            onToggleFavorite = {
                                                favoritesViewModel.toggleFavorite(listing.id)
                                                Toast.makeText(
                                                    context,
                                                    if (favIds.contains(listing.id))
                                                        "Removed from favorites"
                                                    else
                                                        "Added to favorites",
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
    }
}

// Original BrandInput function - kept exactly the same
@Composable
fun BrandInput(
    brand: String,
    onBrandChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = brand,
        onValueChange = onBrandChanged,
        label = { Text("Brand", color = FontIconColor) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedTextColor = FontIconColor,
            focusedTextColor = FontIconColor,
            unfocusedBorderColor = FontIconColor.copy(alpha = 0.5f),
            focusedBorderColor = FontIconColor,
            unfocusedLabelColor = FontIconColor.copy(alpha = 0.5f),
            focusedLabelColor = FontIconColor,
            cursorColor = FontIconColor
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

// Original FilterDropdown function - kept exactly the same
@Composable
private fun FilterDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = FontIconColor) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedTextColor = FontIconColor,
                focusedTextColor = FontIconColor,
                unfocusedBorderColor = FontIconColor.copy(alpha = 0.5f),
                focusedBorderColor = FontIconColor,
                unfocusedLabelColor = FontIconColor.copy(alpha = 0.5f),
                focusedLabelColor = FontIconColor,
                unfocusedTrailingIconColor = FontIconColor.copy(alpha = 0.5f),
                focusedTrailingIconColor = FontIconColor,
                cursorColor = FontIconColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(24.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(WhiteColor)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = FontIconColor, modifier = Modifier.fillMaxWidth()) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(textColor = FontIconColor)
                )
            }
            }
        }
}