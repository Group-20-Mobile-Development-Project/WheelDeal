package com.example.wheeldeal.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.wheeldeal.R
import com.example.wheeldeal.ui.components.TopNavigationBar
import com.example.wheeldeal.ui.components.BottomNavigationBar
import com.example.wheeldeal.ui.components.BottomNavItem
import com.example.wheeldeal.ui.theme.PrimaryColor
import com.example.wheeldeal.ui.theme.WhiteColor

@Composable
fun CarDetailsScreen(
    navController: NavHostController,
    carName: String,
    carMileage: String,
    carFuelType: String,
    carLocation: String,
    carTransmission: String,
    carEngine: String,
    carDescription: String,
    carPrice: String
) {
    val context = LocalContext.current

    // Define bottom nav items
    val bottomNavItems = listOf(
        BottomNavItem("Home", Icons.Default.Home, "home_screen"),
        BottomNavItem("Buy", Icons.Default.ShoppingCart, "buy_screen"),
        BottomNavItem("Favorites", Icons.Default.Favorite, "favorites_screen"),
        BottomNavItem("Sell", Icons.Default.Add, "sell_screen"),
        BottomNavItem("Account", Icons.Default.Person, "account_screen")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFD966))
    ) {
        // Top Navigation Bar
        TopNavigationBar(
            onMessageClick = {
                Toast.makeText(context, "Messages Clicked!", Toast.LENGTH_SHORT).show()
            },
            onNotificationClick = {
                Toast.makeText(context, "Notifications Clicked!", Toast.LENGTH_SHORT).show()
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Text(
                text = "See the details Of the Cars",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF003366),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp)) // Increased gap between title and details box

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp) // Increased padding for larger box
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.placeholder_image),
                        contentDescription = "Car Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp) // Increased height of the image
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp)) // Increased gap after the image

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = carName,
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

                    Text(text = "$carMileage · $carFuelType · $carLocation", fontSize = 16.sp, color = Color.Black)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Transmission", fontSize = 16.sp, color = Color.Black)
                            Text(text = carTransmission, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text(text = "Engine Capacity", fontSize = 16.sp, color = Color.Black)
                            Text(text = carEngine, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Description:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = carDescription, fontSize = 14.sp, color = Color.Black)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "£$carPrice", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF003366))
                }
            }

            Spacer(modifier = Modifier.height(24.dp)) // Increased gap between the details box and the buttons

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
                    Text(text = "Book Test Drive", color = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        Toast.makeText(context, "Contacting Owner...", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Contact Owner", color = Color.White)
                }
            }
        }

        // Bottom Navigation Bar
        BottomNavigationBar(
            items = bottomNavItems,
            onItemSelected = { selectedItem ->
                navController.navigate(selectedItem.route) {
                    popUpTo("home_screen") { inclusive = false }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCarDetailsScreen() {
    CarDetailsScreen(
        navController = rememberNavController(),
        carName = "Audi Q3",
        carMileage = "2,5600 KM",
        carFuelType = "Diesel",
        carLocation = "Kathmandu",
        carTransmission = "Auto",
        carEngine = "1496 cc",
        carDescription = "2018 Honda Civic EX in excellent condition. Only 65,000 km driven, single owner, and fully serviced with records. Features include sunroof, rear camera, Bluetooth, and Apple CarPlay. Non-smoker vehicle, no accidents. Selling due to upgrade. Great fuel efficiency and smooth drive—perfect for city or highway!",
        carPrice = "20,000"
    )
}
