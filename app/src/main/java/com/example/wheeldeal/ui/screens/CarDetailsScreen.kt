package com.example.wheeldeal.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.DriveEta
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.ui.navigation.Screen
import com.example.wheeldeal.ui.theme.*
import com.google.gson.Gson

@Composable
fun CarDetailsScreen(
    navController: NavHostController,
    listing: CarListing
) {
    val context = LocalContext.current
    val scroll = rememberScrollState()

    BackgroundWrapper {
        Column(modifier = Modifier.fillMaxSize()) {

            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .verticalScroll(scroll)
            ) {
                // —— Header ——
                Text(
                    text = "See the Details Of The Car",
                    style = MaterialTheme.typography.headlineMedium,
                    color = FontIconColor,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(16.dp))

                // —— Car Image ——
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    AsyncImage(
                        model = listing.photos.firstOrNull(),
                        contentDescription = "Car Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(Modifier.height(16.dp))

                // —— Title ——
                Text(
                    text = "${listing.brand} ${listing.model} (${listing.year})",
                    style = MaterialTheme.typography.headlineLarge,
                    color = FontIconColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // mileage
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = FontIconColor)
                        Spacer(Modifier.width(4.dp))
                        Text("${listing.avgMileage} KM", color = FontIconColor)
                    }
                    // fuel
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocalGasStation,
                            contentDescription = null,
                            tint = FontIconColor
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(listing.fuelType, color = FontIconColor)
                    }
                    // location
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = FontIconColor)
                        Spacer(Modifier.width(4.dp))
                        Text(listing.location, color = FontIconColor)
                    }
                }
                Spacer(Modifier.height(24.dp))

                // —— Technical Specifications ——
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Technical Specifications",
                            style = MaterialTheme.typography.headlineMedium,
                            color = FontIconColor
                        )
                        Spacer(Modifier.height(12.dp))

                        // row 1
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SpecificationItem("Category", listing.category)
                            SpecificationItem("Condition", listing.condition)
                            SpecificationItem("Color", listing.color)
                        }
                        Spacer(Modifier.height(12.dp))

                        // row 2
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SpecificationItem("Transmission", listing.transmission)
                            SpecificationItem("Engine", "${listing.engineCapacity} cc")
                            SpecificationItem("Seats", listing.seats.toString())
                        }
                        Spacer(Modifier.height(12.dp))

                        // row 3
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SpecificationItem("Odometer", "${listing.odometer} km")
                            SpecificationItem(
                                "Accidents",
                                if (listing.accidents > 0) "${listing.accidents}" else "None"
                            )
                            SpecificationItem("Ownership", listing.ownership)
                        }
                        Spacer(Modifier.height(12.dp))

                        // row 4
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SpecificationItem("Last Inspection", listing.lastInspection)
                            SpecificationItem(
                                "Negotiable",
                                if (listing.negotiable) "Yes" else "No"
                            )
                            Spacer(Modifier.width(100.dp))
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))

                // —— Description ——
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Description",
                            style = MaterialTheme.typography.headlineMedium,
                            color = FontIconColor
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = listing.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = FontIconColor,
                            lineHeight = 20.sp
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))

                // —— Price ——
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Price",
                            style = MaterialTheme.typography.headlineMedium,
                            color = FontIconColor,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "€${"%,.0f".format(listing.price)}",
                                style = MaterialTheme.typography.headlineLarge,
                                color = FontIconColor
                            )
                            Text(
                                text = if (listing.negotiable) "Negotiable" else "Non-negotiable",
                                color = if (listing.negotiable) Color(0xFF388E3C) else Color(
                                    0xFFD32F2F
                                ),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            // —— Action Buttons ——
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Filled “Book Test Drive”
                Button(
                    onClick = {
                        Toast.makeText(context, "Test Drive Booked!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = FontIconColor
                    ),
                    border = BorderStroke(1.dp, FontIconColor),
                    shape = RoundedCornerShape(24.dp) // match pill shape
                ) {
                    Icon(Icons.Default.DriveEta, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Book Test Drive")
                }

                Spacer(Modifier.width(16.dp))

                // Outlined “Contact Owner”
                Button(
                    onClick = {
                        val json = Gson().toJson(listing)
                        navController.navigate(Screen.CarOwnerDetails.createRoute(json))
                    },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FontIconColor,
                    contentColor = WhiteColor
                )
                ) {
                Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Contact Owner")
            }
            }
        }

        }
}

@Composable
fun SpecificationItem(label: String, value: String) {
    Column(modifier = Modifier.widthIn(min = 100.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = FontIconColor.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = FontIconColor
        )
    }
}
