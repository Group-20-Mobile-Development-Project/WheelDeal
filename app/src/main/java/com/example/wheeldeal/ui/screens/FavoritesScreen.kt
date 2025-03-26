package com.example.wheeldeal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


data class Car(
    val name: String,
    val kilometers: String,
    val fuelType: String,
    val location: String,
    val price: String
)

@Composable
fun FavoritesScreen() {
    val favoriteCars = listOf(
        Car("Audi Q3", "2,500 KM", "Diesel", "Kathmandu", "£20,000"),
        Car("Audi Q3", "2,500 KM", "Diesel", "Kathmandu", "£20,000"),
        Car("Audi Q3", "2,500 KM", "Diesel", "Kathmandu", "£20,000")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFD95A))
            .padding(16.dp)
    ) {
        HeaderSection()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(favoriteCars) { car ->
                FavoriteCarCard(car)
            }
        }
        BottomNavigationBar()
    }
}

@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(vertical = 16.dp)
            .background(Color(0xFFFFCC00), shape = RoundedCornerShape(16.dp))
    ) {
        Text(
            text = "Your Favorite Car's",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF002366),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun FavoriteCarCard(car: Car) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(text = car.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "${car.kilometers} | ${car.fuelType} | ${car.location}")
                Text(text = car.price, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Button(onClick = {}, modifier = Modifier.padding(start = 8.dp)) {
                Text(text = "View car →")
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFFFD95A)),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Home")
        Text("Buy")
        Text("Favorites")
        Text("Sell")
        Text("Account")
    }
}
