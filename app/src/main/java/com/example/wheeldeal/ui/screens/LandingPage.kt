package com.example.wheeldeal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.font.FontWeight
import com.example.wheeldeal.R
import com.example.wheeldeal.ui.theme.PrimaryColor
import com.example.wheeldeal.ui.theme.WhiteColor
import com.example.wheeldeal.ui.theme.FontIconColor


@Composable
fun LandingPage(
    modifier: Modifier = Modifier,
    onGetStartedClick: () -> Unit
) {
    // Apply gradient directly to the Column
    Column(
        modifier = modifier
            .fillMaxSize() // Fill the whole screen
            .background(Brush.verticalGradient(
                colors = listOf(PrimaryColor, WhiteColor), // Yellow to White gradient
                startY = 0f, // Start gradient at the top
                endY = 1500f // Increase the white section (adjust this for desired effect)
            )),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top // Adjust vertical arrangement to push content upwards
    ) {
        // Logo (Moved up a bit)
        Image(
            painter = painterResource(id = R.drawable.logo), // Replace with your logo resource
            contentDescription = "Logo",
            modifier = Modifier
                .size(400.dp) // Increased size of logo
                .offset(y = (-10).dp) // Move the logo up more (adjust for positioning)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Vector Art Car Image (Moved up)
        Image(
            painter = painterResource(id = R.drawable.car_vector), // Replace with your car vector image
            contentDescription = "Car Image",
            modifier = Modifier
                .size(350.dp) // Adjust the size of your car vector image
                .offset(y = (-50).dp) // Move car image up a bit more
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Get Started Button (Moved up)
        Button(
            onClick = onGetStartedClick,
            modifier = Modifier
                .fillMaxWidth(0.5f) // Set button width to 50% of the screen width
                .padding(horizontal = 16.dp) // Horizontal padding to make button wider
                .offset(y = (-20).dp), // Move button up a bit
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = FontIconColor)
        ) {
            Text(
                text = "Get Started",
                color = WhiteColor,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LandingPagePreview() {
    LandingPage(onGetStartedClick = {})
}
