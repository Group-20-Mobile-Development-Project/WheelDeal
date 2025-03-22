package com.example.wheeldeal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wheeldeal.ui.components.BottomNavItem
import com.example.wheeldeal.ui.components.BottomNavigationBar
import com.example.wheeldeal.ui.components.TopNavigationBar
import com.example.wheeldeal.ui.navigation.Screen

@Composable
fun MainScreen() {
    val bottomNavController = rememberNavController()

    // Define bottom nav items
    val bottomNavItems = listOf(
        BottomNavItem("Home", Icons.Default.Home, Screen.Home.route),
        BottomNavItem("Buy", Icons.Default.ShoppingCart, Screen.Buy.route),
        BottomNavItem("Favorites", Icons.Default.Favorite, Screen.Favorites.route),
        BottomNavItem("Sell", Icons.Default.Add, Screen.Sell.route),
        BottomNavItem("Account", Icons.Default.Person, Screen.Account.route)
    )

    Scaffold(
        // 1) Add the top bar
        topBar = {
            TopNavigationBar(
                onMessageClick = { /* e.g., navigate to a chat screen */ },
                onNotificationClick = { /* e.g., show notifications */ }
            )
        },
        // 2) Bottom nav
        bottomBar = {
            BottomNavigationBar(
                items = bottomNavItems,
                onItemSelected = { selectedItem ->
                    bottomNavController.navigate(selectedItem.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )
        }
    ) { innerPadding ->
        // The sub-navigation for the bottom nav
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            NavHost(
                navController = bottomNavController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) { HomeScreen() }
                composable(Screen.Buy.route) { BuyScreen() }
                composable(Screen.Favorites.route) { FavoritesScreen() }
                composable(Screen.Sell.route) { SellScreen() }
                composable(Screen.Account.route) {
                    AccountScreen(
                        onLoginSuccess = {
                            bottomNavController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Account.route) { inclusive = true }
                            }
                        }
                    )
                }

            }
        }
    }
}
