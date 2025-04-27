package com.example.wheeldeal.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.ui.navigation.Screen
import com.example.wheeldeal.ui.theme.*
import com.example.wheeldeal.viewmodel.FavoritesViewModel
import com.example.wheeldeal.viewmodel.ListingState
import com.example.wheeldeal.viewmodel.ListingViewModel
import com.google.gson.Gson

@Composable
fun FavoritesScreen(
    navController: NavHostController,
    listingViewModel: ListingViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel = viewModel()
) {
    val context       = LocalContext.current
    val state by listingViewModel.listingState.collectAsState()
    val favoriteIds by favoritesViewModel.favoriteIds.collectAsState()

    val favoriteListings = (state as? ListingState.Success)
        ?.listings
        ?.filter { favoriteIds.contains(it.id) }
        ?: emptyList()

    BackgroundWrapper {
        Column(
            Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(16.dp)
        ) {
            HeaderSection()
            Spacer(Modifier.height(16.dp))

            if (favoriteListings.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No favorites yet", color = WhiteColor, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(favoriteListings, key = { it.id }) { car ->
                        FavoriteCarCard(
                            car      = car,
                            onView = {
                                val json = Gson().toJson(car) // Convert to JSON
                                val encoded = Uri.encode(json) // URI-encode it once
                                navController.navigate(Screen.CarDetails.createRoute(encoded))
                            },
                            onRemove = {
                                favoritesViewModel.toggleFavorite(car.id)
                                Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderSection(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    brush = Brush.linearGradient(listOf(Color(0xFFFFC107), Color(0xFFFFA000))),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(size.width * 0.5f, size.height * 0.0f)
                quadraticTo(size.width * 0.75f, size.height * 0.5f,
                    size.width * 1.1f, size.height * 0.3f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(
                path = path,
                brush = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.25f), Color.Transparent))
            )
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Your\nFavorites Car´s",
                style = MaterialTheme.typography.headlineLarge.copy(
                    letterSpacing = 0.8.sp,
                    color = Color(0xFF0E2F56)
                )
            )
            Button(
                onClick = {},
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E2F56)),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text("Wheel Deal", color = WhiteColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FavoriteCarCard(
    car: CarListing,
    onView: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp) // Fixed card height
            .padding(horizontal = 8.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteColor)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Delete button
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(20.dp)
            ) {
                Icon(Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f))
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Taller image (120dp height) with aspect ratio preserved
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(138.dp) // Increased height
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = car.photos.firstOrNull() ?: "",
                        contentDescription = "${car.brand} ${car.model}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Column( modifier = Modifier.offset(y = (5).dp)
                    ) {
                        Text(
                            text = "${car.brand} ${car.model}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = FontIconColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "${car.year} | ${car.fuelType} | ${car.location}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FontIconColor.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "£${"%,.2f".format(car.price)}",
                            style = TextStyle(
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = FontIconColor
                        )

                        TextButton(
                            onClick = onView,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 3.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FontIconColor,
                                contentColor   = WhiteColor
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = "View →",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}