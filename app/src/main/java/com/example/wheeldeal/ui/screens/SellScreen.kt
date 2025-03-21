package com.example.wheeldeal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wheeldeal.ui.components.TopNavigationBar
import com.example.wheeldeal.ui.components.BottomNavigationBar
import com.example.wheeldeal.ui.navigation.Screen

@Composable
fun SellScreen(
    onNavigateBack: () -> Unit,
    onSubmitListing: () -> Unit
) {
    var carName by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var transmission by remember { mutableStateOf("") }
    var engineCapacity by remember { mutableStateOf("") }
    var fuelType by remember { mutableStateOf("") }
    var odometer by remember { mutableStateOf("") }
    var accidents by remember { mutableStateOf("") }
    var seats by remember { mutableStateOf("") }
    var lastInspection by remember { mutableStateOf("") }
    var ownership by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var isPriceNegotiable by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        // Top Navigation Bar (You can customize it as needed)
        TopNavigationBar(
            onMessageClick = { /* Handle message click */ },
            onNotificationClick = { /* Handle notification click */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Car Name
        OutlinedTextField(
            value = carName,
            onValueChange = { carName = it },
            label = { Text("Car Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Condition
        OutlinedTextField(
            value = condition,
            onValueChange = { condition = it },
            label = { Text("Condition (New/Used)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Brand
        OutlinedTextField(
            value = brand,
            onValueChange = { brand = it },
            label = { Text("Brand") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Transmission
        OutlinedTextField(
            value = transmission,
            onValueChange = { transmission = it },
            label = { Text("Transmission") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Engine Capacity
        OutlinedTextField(
            value = engineCapacity,
            onValueChange = { engineCapacity = it },
            label = { Text("Engine Capacity") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Fuel Type
        OutlinedTextField(
            value = fuelType,
            onValueChange = { fuelType = it },
            label = { Text("Fuel Type") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Avg Mileage Section
        Text(
            text = "Avg Mileage",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Odometer
        OutlinedTextField(
            value = odometer,
            onValueChange = { odometer = it },
            label = { Text("Odometer") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Accidents
        OutlinedTextField(
            value = accidents,
            onValueChange = { accidents = it },
            label = { Text("Accidents") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Seats
        OutlinedTextField(
            value = seats,
            onValueChange = { seats = it },
            label = { Text("Seats") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Last Inspection
        OutlinedTextField(
            value = lastInspection,
            onValueChange = { lastInspection = it },
            label = { Text("Last Inspection") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Ownership
        OutlinedTextField(
            value = ownership,
            onValueChange = { ownership = it },
            label = { Text("Ownership") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Location
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Price
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Is Price Negotiable
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Is Price Negotiable?")
            Checkbox(
                checked = isPriceNegotiable,
                onCheckedChange = { isPriceNegotiable = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        Button(
            onClick = onSubmitListing,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("SUBMIT")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Navigation Bar (You can customize it as needed)

    }
}

@Preview(showBackground = true)
@Composable
fun SellScreenPreview() {
    SellScreen(
        onNavigateBack = { /* Handle back navigation */ },
        onSubmitListing = { /* Handle submit */ }
    )
}
