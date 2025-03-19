package com.example.wheeldeal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.wheeldeal.ui.screens.HomeScreen
import com.example.wheeldeal.ui.theme.WheelDealTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WheelDealTheme {
                HomeScreen()
            }
        }
    }
}
