package com.example.wheeldeal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.wheeldeal.ui.navigation.NavGraph
import com.example.wheeldeal.ui.theme.WheelDealTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WheelDealTheme {
                NavGraph()
            }
        }
    }
}
