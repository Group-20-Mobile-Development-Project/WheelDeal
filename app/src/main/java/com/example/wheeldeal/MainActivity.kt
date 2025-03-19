package com.example.wheeldeal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier

import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import com.example.wheeldeal.ui.screen.LandingPage
import com.example.wheeldeal.ui.theme.WheelDealTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WheelDealTheme {
                // Use Scaffold as the layout for the app
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Call the LandingPage composable
                    LandingPage(
                        modifier = Modifier.padding(innerPadding),
                        onGetStartedClick = {
                            // Handle Get Started button click, navigate to next screen
                            // You can add navigation logic here if needed
                        }
                    )
                }
            }
        }
    }
}
