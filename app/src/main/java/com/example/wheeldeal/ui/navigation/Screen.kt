package com.example.wheeldeal.ui.navigation

sealed class Screen(val route: String) {
    object Landing : Screen("landing")
    object Main : Screen("main")

    // Sub-routes for bottom nav
    object Home : Screen("home")
    object Buy : Screen("buy")
    object Favorites : Screen("favorites")
    object Sell : Screen("sell")
    object Account : Screen("account")
    object Profile : Screen("profile")
}
