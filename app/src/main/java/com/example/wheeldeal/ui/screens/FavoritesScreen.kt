package com.example.wheeldeal.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.ui.theme.FontIconColor
import com.example.wheeldeal.ui.theme.Poppins
import com.example.wheeldeal.ui.theme.PrimaryColor
import com.example.wheeldeal.ui.theme.WhiteColor
import com.example.wheeldeal.viewmodel.FavoritesViewModel
import com.example.wheeldeal.viewmodel.ListingState
import com.example.wheeldeal.viewmodel.ListingViewModel

@Composable
fun FavoritesScreen(
    listingViewModel: ListingViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by listingViewModel.listingState.collectAsState()
    val favoriteIds by favoritesViewModel.favoriteIds.collectAsState()

    val favoriteListings = (state as? ListingState.Success)?.listings
        ?.filter { favoriteIds.contains(it.id) }
        ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryColor)
            .padding(16.dp)
    ) {
        HeaderSection()

        Spacer(modifier = Modifier.height(12.dp))

        if (favoriteListings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No favorites yet", color = WhiteColor, fontSize = 18.sp)
            }
        } else {
            LazyColumn {
                items(favoriteListings) { car ->
                    FavoriteCarCard(car = car) {
                        favoritesViewModel.toggleFavorite(car.id)
                        Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFFC107), Color(0xFFFFA000))
                ),
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "Your Favorite Cars",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF002366),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun FavoriteCarCard(car: CarListing, onRemove: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = car.photos.firstOrNull() ?: "https://via.placeholder.com/300",
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("${car.brand} ${car.model}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = FontIconColor)
                Text("${car.year} · ${car.fuelType} · ${car.location}", color = FontIconColor)
                Text("$${"%,.2f".format(car.price)}", fontWeight = FontWeight.Bold, color = FontIconColor)
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}