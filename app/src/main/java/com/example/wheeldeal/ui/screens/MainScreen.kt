package com.example.wheeldeal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wheeldeal.viewmodel.FavoritesViewModel
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.ui.components.*
import com.example.wheeldeal.ui.navigation.Screen
import com.example.wheeldeal.viewmodel.*
import com.google.gson.Gson
import android.net.Uri


@Composable
fun MainScreen(
    viewModel: AuthViewModel = viewModel(),
    navController: NavHostController,
    favoritesViewModel: FavoritesViewModel
) {
    val userData by viewModel.userData.collectAsState()
    val innerNav = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem("Home",      Icons.Default.Home,         Screen.Home.route),
        BottomNavItem("Buy",       Icons.Default.ShoppingCart, Screen.Buy.route),
        BottomNavItem("Favorites", Icons.Default.Favorite,     Screen.Favorites.route),
        BottomNavItem("Sell",      Icons.Default.Add,          Screen.Sell.route),
        BottomNavItem("Account",   Icons.Default.Person,       Screen.Account.route)
    )

    val filterViewModel: BuyFilterViewModel         = viewModel()
    val notificationViewModel: NotificationViewModel = viewModel()
    val listingViewModel: ListingViewModel     = viewModel()
    val favoritesViewModel: FavoritesViewModel = viewModel()



    Scaffold(
        topBar = {
            TopNavigationBar(
                onMessageClick      = { innerNav.navigate(Screen.ChatList.route) },
                onNotificationClick = { innerNav.navigate("notifications") }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                items = bottomNavItems,
                onItemSelected = { item ->
                    innerNav.navigate(item.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            NavHost(
                navController   = innerNav,
                startDestination = Screen.Home.route
            ) {
                // 1) Home
                composable(Screen.Home.route) {
                    HomeScreen(innerNav = innerNav)
                }
                // 2) Buy
                composable(Screen.Buy.route) {
                    BuyScreen(
                        navController   = innerNav,
                        filterViewModel = filterViewModel,
                        favoritesViewModel = favoritesViewModel
                    )
                }
                // 3) Favorites
                composable(Screen.Favorites.route) {
                    LaunchedEffect(Unit) {
                        favoritesViewModel.loadFavorites()
                    }
                    FavoritesScreen(
                        navController     = innerNav,
                        listingViewModel  = listingViewModel,
                        favoritesViewModel = favoritesViewModel
                    )
                }
                // 4) Sell
                composable(Screen.Sell.route) {
                    SellScreen()
                }
                // 5) Account (switch between logged‑in vs. login)
                composable(Screen.Account.route) {
                    if (userData != null) {
                        ProfileScreen(
                            onBackToMain = {
                                innerNav.navigate(Screen.Account.route) {
                                    popUpTo(Screen.Account.route) { inclusive = true }
                                }
                            },
                            viewModel = viewModel,
                            favoritesViewModel = favoritesViewModel
                        )
                    } else {
                        AccountScreen(
                            viewModel        = viewModel,
                            favoritesViewModel = favoritesViewModel,
                            onLoginSuccess = {
                                favoritesViewModel.loadFavorites()
                                innerNav.navigate(Screen.Account.route) {
                                    popUpTo(Screen.Account.route) { inclusive = true }
                                }
                            }
                        )
                    }
                }
                // Notifications
                composable("notifications") {
                    NotificationScreen(
                        navController = innerNav,
                        viewModel     = notificationViewModel
                    )
                }
                // Car details
                composable("carDetails/{carJson}") { backStack ->
                    val json    = backStack.arguments?.getString("carJson") ?: ""
                    val decoded = Uri.decode(json)
                    val listing = Gson().fromJson(decoded, CarListing::class.java)
                    CarDetailsScreen(listing = listing, navController = innerNav)
                }
                // Car owner details
                composable("carOwnerDetails/{listingJson}") { backStack ->
                    val json    = backStack.arguments?.getString("listingJson") ?: ""
                    val listing = Gson().fromJson(json, CarListing::class.java)
                    CarOwnerDetailsScreen(listing = listing, navController = innerNav)
                }
                // Chat list & single chat
                composable(Screen.ChatList.route) {
                    ChatListScreen(navController = innerNav)
                }
                composable(Screen.Chat.route) { backStack ->
                    val chatId     = backStack.arguments?.getString("chatId")     ?: ""
                    val receiverId = backStack.arguments?.getString("receiverId") ?: ""
                    ChatScreen(
                        chatId        = chatId,
                        receiverId    = receiverId,
                        navController = innerNav
                    )
                }
            }
        }
    }
}
