package com.example.wheeldeal.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.ui.components.TopNavigationBar
import com.example.wheeldeal.ui.theme.WhiteColor
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.example.wheeldeal.ui.navigation.Screen
import com.example.wheeldeal.ui.theme.BackgroundWrapper
import com.example.wheeldeal.ui.theme.PrimaryColor
import com.google.gson.Gson

@Composable
fun CarDetailsScreen(
    navController: NavHostController,
    listing: CarListing
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
BackgroundWrapper {
    Column(
        modifier = Modifier
            .fillMaxSize()

    ) {
        // Scrollable Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "See the details Of the Cars",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF003366),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Column {
                    AsyncImage(
                        model = listing.photos.firstOrNull(),
                        contentDescription = "Car Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${listing.brand} ${listing.model} (${listing.year})",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF003366)
                        )
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Favorite",
                            tint = Color(0xFF003366)
                        )
                    }

                    Text(
                        text = "${listing.avgMileage} KM · ${listing.fuelType} · ${listing.location}",
                        fontSize = 16.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Transmission", fontSize = 16.sp, color = Color.Black)
                            Text(listing.transmission, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Engine Capacity", fontSize = 16.sp, color = Color.Black)
                            Text("${listing.engineCapacity} cc", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Description:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(
                        listing.description,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$${listing.price}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003366)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        Toast.makeText(context, "Test Drive Booked!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Book Test Drive", color = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val json = Gson().toJson(listing)
                        navController.navigate(Screen.CarOwnerDetails.createRoute(json))
                    },

                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Contact Owner", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // bottom scroll padding
        }
    }
}
}
