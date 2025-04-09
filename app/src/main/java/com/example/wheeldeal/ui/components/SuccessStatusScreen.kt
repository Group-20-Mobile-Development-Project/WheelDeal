package com.example.wheeldeal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SuccessStatusScreen(
    title: String,
    onMenuClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFE07D)) // Yellow background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Checkmark icon
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success Checkmark",
                tint = Color(0xFF0A284F),
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Title (e.g., Listed / Deleted / Updated)
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

            Spacer(modifier = Modifier.height(24.dp))

            // Car icon
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = "Car Icon",
                tint = Color(0xFF0A284F),
                modifier = Modifier.size(64.dp)
            )
        }

        // Top-right menu icon
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

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewSuccessStatusScreen() {
    SuccessStatusScreen(title = "Listed")
}
