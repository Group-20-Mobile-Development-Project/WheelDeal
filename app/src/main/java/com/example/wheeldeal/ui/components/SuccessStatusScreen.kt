package com.example.wheeldeal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.wheeldeal.ui.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SuccessStatusScreen(
    title: String,
    onMenuClick: () -> Unit = {},
    onCloseClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFE07D)) // Light Yellow background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Circle with check icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0A284F)) // Dark Blue
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success Checkmark",
                    tint = Color(0xFFFFE07D), // Yellow checkmark
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Dynamic status text
            Text(
                text = title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0A284F)
            )

            Text(
                text = "Successfully",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0A284F)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Car icon at bottom
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = "Car Icon",
                tint = Color(0xFF0A284F),
                modifier = Modifier.size(64.dp)
            )
        }

        // Top-left X (close) icon
        IconButton(
            onClick = onCloseClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(25.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Icon",
                tint = Color.Black
            )
        }

        // Top-right Menu icon (optional)
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu Icon",
                tint = Color.Black
            )
        }
    }
}
@Composable
fun SuccessStatusScreen(
    navController: NavController,
    title: String
) {
    // Run a side effect that waits 4 seconds and navigates back to SellScreen
    LaunchedEffect(Unit) {
        delay(4000)
        navController.popBackStack() // OR: navController.navigate(Screen.Sell.route)
    }

    // Reuse the actual UI
    SuccessStatusScreen(
        title = title,
        onCloseClick = {
            navController.popBackStack()
        }
    )
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewSuccessStatusScreen() {
    SuccessStatusScreen(
        title = "Listed",
        onCloseClick = {}, // Simulate popBackStack or dismiss here
        onMenuClick = {}
    )
}
