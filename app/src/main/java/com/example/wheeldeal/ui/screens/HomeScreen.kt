package com.example.wheeldeal.ui.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Welcome to WheelDeal Home Screen!")

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wheeldeal.R
import com.example.wheeldeal.ui.components.BottomNavItem
import com.example.wheeldeal.ui.components.BottomNavigationBar
import com.example.wheeldeal.ui.components.HeroSection
import com.example.wheeldeal.ui.components.TopNavigationBar

@Preview(showBackground = true)

@Composable
fun HomeScreen() {
    // Define your five bottom nav items
    val bottomNavItems = listOf(
        BottomNavItem("Home", Icons.Default.Home),
        BottomNavItem("Buy", Icons.Default.ShoppingCart),
        BottomNavItem("Favorites", Icons.Default.Favorite),
        BottomNavItem("Sell", Icons.Default.Add),
        BottomNavItem("Account", Icons.Default.Person)
    )

    Scaffold(
        topBar = {
            TopNavigationBar(
                onMessageClick = { /* Handle message click */ },
                onNotificationClick = { /* Handle notification click */ }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                items = bottomNavItems,
                onItemSelected = { selectedItem ->
                    // Handle navigation action here
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ✅ Added Hero Section
            HeroSection()

            Spacer(modifier = Modifier.height(16.dp))

            // Other content goes here...
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Welcome to WheelDeal Home Screen!")
            }
        }
    }
}
