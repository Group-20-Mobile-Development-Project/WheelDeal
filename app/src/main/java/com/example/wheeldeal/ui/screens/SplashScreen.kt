package com.example.wheeldeal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.example.wheeldeal.R
import com.example.wheeldeal.ui.theme.FontIconColor
import com.example.wheeldeal.ui.theme.PrimaryColor
import com.example.wheeldeal.ui.theme.WhiteColor

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryColor, WhiteColor)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔄 Show logo if available
            Image(
                painter = painterResource(id = R.drawable.logo), // make sure logo exists
                contentDescription = "App Logo",
                modifier = Modifier
                    .height(120.dp)
                    .padding(bottom = 16.dp)
            )

            // Or fallback to brand name
            Text(
                text = "WheelDeal",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = FontIconColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            CircularProgressIndicator(
                color = FontIconColor,
                strokeWidth = 3.dp
            )
        }
    }
}
