@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)


package com.example.wheeldeal.ui.screens

import android.widget.Toast
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import coil.compose.AsyncImage
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




@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
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

    // Local temporary copies before “Apply”
    var localBrand by remember { mutableStateOf(filters.brand) }
    var localCategory by remember { mutableStateOf(filters.category) }
    var localTransmission by remember { mutableStateOf(filters.transmission) }
    var localFuelType by remember { mutableStateOf(filters.fuelType) }
    var localYear by remember { mutableStateOf(filters.year) }
    var localBudget by remember { mutableFloatStateOf(filters.budget) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Text(
                        text = "Buy Cars",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        BackgroundWrapper {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                    .padding(16.dp)
                    .padding(paddingValues) // Add padding from Scaffold
            ) {
                // Toggle filter section
                Button(
                    onClick = { filterViewModel.toggleFilterSection() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkBlue,
                        contentColor = WhiteColor
                    )
                ) {
                    Text(if (filters.showFilters) "Hide Filters" else "Show Filters")
                }
                Spacer(Modifier.height(12.dp))

                if (filters.showFilters) {
                    BrandInput(
                        brand = localBrand,
                        onBrandChanged = { localBrand = it }
                    )

                    FilterDropdown("Category", listOf(
                        "Sedan", "Hatchback", "SUV", "Coupe",
                        "Convertible", "Van", "Truck"
                    ), localCategory) {
                        localCategory = it
                    }
                    FilterDropdown("Transmission", listOf("Automatic", "Manual"), localTransmission) {
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
                        valueRange = 1_000f..1_000_000f,
                        steps = 20
                    )

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton({
                            localBrand = ""
                            localCategory = ""
                            localTransmission = ""
                            localFuelType = ""
                            localYear = ""
                            localBudget = 50_000f
                        }) {
                            Text("Clear")
                        }
                        Button({
                            filterViewModel.updateAllFilters(
                                brand = localBrand,
                                category = localCategory,
                                transmission = localTransmission,
                                fuelType = localFuelType,
                                year = localYear,
                                budget = localBudget
                            )
                        }) {
                            Text("Apply Filters")
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // Content based on state
                when (state) {
                    ListingState.Loading -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                    is ListingState.Error -> {
                        val msg = (state as ListingState.Error).message
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Error loading listings: $msg", color = MaterialTheme.colorScheme.error)
                        }
                    }

                    is ListingState.Success -> {
                        val all = (state as ListingState.Success).listings
                        val filtered = filterViewModel.filterListings(all)

                        if (filtered.isEmpty()) {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No listings found.")
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                gridItems(filtered) { listing ->
                                    CarCard(
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}



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
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedTextColor = FontIconColor,
                focusedTextColor = FontIconColor,
                unfocusedBorderColor = FontIconColor.copy(alpha = 0.5f),
                focusedBorderColor = FontIconColor,
                unfocusedLabelColor = FontIconColor.copy(alpha = 0.5f),
                focusedLabelColor = FontIconColor,
                cursorColor = FontIconColor,
                unfocusedTrailingIconColor = FontIconColor.copy(alpha = 0.5f),
                focusedTrailingIconColor = FontIconColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(24.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(WhiteColor)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option,
                            color = FontIconColor,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = FontIconColor
                    )
                )
            }
        }
    }
}

