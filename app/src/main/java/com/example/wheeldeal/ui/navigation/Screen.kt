package com.example.wheeldeal.ui.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Landing : Screen("landing")
    object Main : Screen("main")

    object Home : Screen("home")
    object Buy : Screen("buy")
    object Favorites : Screen("favorites")
    object Sell : Screen("sell")
    object Account : Screen("account")
    object Profile : Screen("profile")

    object CarDetails : Screen("carDetails/{carJson}") {
        fun createRoute(carJson: String) = "carDetails/${Uri.encode(carJson)}"
    }
    object CarOwnerDetails : Screen("carOwnerDetails/{listingJson}") {
        fun createRoute(listingJson: String) = "carOwnerDetails/${Uri.encode(listingJson)}"
    }

    object Chat : Screen("chat/{chatId}/{receiverId}") {
        fun createRoute(chatId: String, receiverId: String) = "chat/$chatId/$receiverId"
    }

    object ChatList : Screen("chatList") {
        fun createRoute() = "chatList"
    }
}
