package com.example.wheeldeal.ui.navigation

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.ui.screens.*
import com.example.wheeldeal.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.delay
import com.example.wheeldeal.ui.components.SuccessStatusScreen



@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: AuthViewModel = viewModel()
) {
    val auth = FirebaseAuth.getInstance()
    var startDestination by remember { mutableStateOf<String?>(null) }

    if (startDestination == null) {
        SplashScreen()
        LaunchedEffect(Unit) {
            delay(1200)
            startDestination = if (auth.currentUser != null) Screen.Main.route else Screen.Landing.route
        }
        return
    }

    NavHost(navController = navController, startDestination = startDestination!!) {
        composable(Screen.Landing.route) {
            LandingPage {
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Landing.route) { inclusive = true }
                }
            }
        }

        composable(Screen.Main.route) {
            MainScreen(viewModel = viewModel, navController = navController)
        }
        composable("success_screen") {
            SuccessStatusScreen(
                title = "Listed",
                onCloseClick = {
                    navController.popBackStack() // This lets user go back if they tap "X"
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onBackToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }

        composable(Screen.CarDetails.route) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("carJson") ?: ""
            val decoded = Uri.decode(json)
            val listing = Gson().fromJson(decoded, CarListing::class.java)
            CarDetailsScreen(listing = listing, navController = navController)
        }

        composable(Screen.CarOwnerDetails.route) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("listingJson") ?: ""
            val decoded = Uri.decode(json)
            val listing = Gson().fromJson(decoded, CarListing::class.java)
            CarOwnerDetailsScreen(listing = listing)
        }
    }
}


