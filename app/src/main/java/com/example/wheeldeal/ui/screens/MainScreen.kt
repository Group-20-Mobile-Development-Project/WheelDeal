package com.example.wheeldeal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wheeldeal.viewmodel.NotificationViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wheeldeal.model.NotificationItem
import com.example.wheeldeal.ui.components.BottomNavItem
import com.example.wheeldeal.ui.components.BottomNavigationBar
import com.example.wheeldeal.ui.components.TopNavigationBar
import com.example.wheeldeal.ui.navigation.Screen
import com.example.wheeldeal.viewmodel.AuthViewModel
import com.example.wheeldeal.viewmodel.BuyFilterViewModel

@Composable
fun MainScreen(
    viewModel: AuthViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val bottomNavController = rememberNavController()
    val userData by viewModel.userData.collectAsState()

    val bottomNavItems = listOf(
        BottomNavItem("Home", Icons.Default.Home, Screen.Home.route),
        BottomNavItem("Buy", Icons.Default.ShoppingCart, Screen.Buy.route),
        BottomNavItem("Favorites", Icons.Default.Favorite, Screen.Favorites.route),
        BottomNavItem("Sell", Icons.Default.Add, Screen.Sell.route),
        BottomNavItem("Account", Icons.Default.Person, Screen.Account.route)
    )
    val filterViewModel: BuyFilterViewModel = viewModel()
    val notificationViewModel: NotificationViewModel = viewModel()


    Scaffold(
        topBar = {
            TopNavigationBar(
                onMessageClick = { /* handle messages */ },
                onNotificationClick = { bottomNavController.navigate("notifications") }
            )
        },
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
                composable(Screen.Buy.route) {
                    BuyScreen(filterViewModel = filterViewModel)
                }
                composable(Screen.Favorites.route) { FavoritesScreen() }
                composable(Screen.Sell.route) { SellScreen() }

                composable(Screen.Account.route) {
                    if (userData != null) {
                        ProfileScreen(
                            onBackToMain = {
                                bottomNavController.navigate(Screen.Account.route) {
                                    popUpTo(Screen.Account.route) { inclusive = true }
                                }
                            },
                            viewModel = viewModel
                        )
                    } else {
                        AccountScreen(
                            viewModel = viewModel,
                            onLoginSuccess = {
                                bottomNavController.navigate(Screen.Account.route) {
                                    popUpTo(Screen.Account.route) { inclusive = true }
                                }
                            }
                        )
                    }
                }

                composable("notifications") {
                    NotificationScreen(
                        navController = bottomNavController,
                        viewModel = notificationViewModel
                    )
            }
                composable("buy") {
                    BuyScreen(filterViewModel = filterViewModel) }
        }
    }
}
}