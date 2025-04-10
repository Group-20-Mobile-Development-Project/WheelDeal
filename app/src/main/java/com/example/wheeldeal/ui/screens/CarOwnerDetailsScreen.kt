package com.example.wheeldeal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.wheeldeal.ui.components.BottomNavigationBar
import com.example.wheeldeal.ui.components.TopNavigationBar
import com.example.wheeldeal.ui.theme.FontIconColor
import com.example.wheeldeal.ui.theme.Poppins
import com.example.wheeldeal.ui.theme.PrimaryColor
import com.example.wheeldeal.ui.theme.WhiteColor

@Preview
@Composable
fun CarOwnerDetailsScreenWrapper() {
    Scaffold(
        topBar = {
            TopNavigationBar(
                onMessageClick = {},
                onNotificationClick = {}
            )
        },
        bottomBar = {
            BottomNavigationBar()
        }
    ) { innerPadding ->
        CarOwnerDetailsScreenContent(Modifier.padding(innerPadding))
    }
}

@Composable
fun CarOwnerDetailsScreenContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(PrimaryColor, Color(0xFFF9F7EF))
                )
            )
            .padding(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier.background(WhiteColor)
            ) {
                AsyncImage(
                    model = "https://pictures.dealer.com/generic-aoa-OEM_VIN_STOCK_PHOTOS/d35617fab9ca34b3a36c31dc82b832d5.jpg?impolicy=downsize_bkpt&imdensity=1&w=520",
                    contentDescription = "Car Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Audi Q3",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = FontIconColor
                    )
                    Text(
                        "2,5600 KM · Diesel · Kathmandu",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Transmission", color = Color.Gray)
                            Text("Auto", fontWeight = FontWeight.Bold, color = FontIconColor)
                        }
                        Spacer(modifier = Modifier.width(24.dp))
                        Column {
                            Text("Engine Capacity", color = Color.Gray)
                            Text("1496 cc", fontWeight = FontWeight.Bold, color = FontIconColor)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "£20,000",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = FontIconColor
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF003366)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Owner Icon",
                        tint = WhiteColor
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Car Owner",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = FontIconColor
                )
            }

            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = FontIconColor
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        InfoField(label = "E Mail ID", value = "owner@example.com")
        InfoField(label = "Number", value = "+44 7123 456789")
        InfoField(label = "Location", value = "Kathmandu")

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { /* TODO: Implement Chat */ },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FontIconColor),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Default.Chat, contentDescription = "Chat", tint = WhiteColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Chat", fontWeight = FontWeight.Bold, color = WhiteColor)
            }

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedButton(
                onClick = { /* TODO: Implement Call */ },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = FontIconColor),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Icon(Icons.Default.Call, contentDescription = "Call", tint = FontIconColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Call", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun InfoField(label: String, value: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        label = {
            Text(
                text = label,
                color = FontIconColor,
                fontFamily = Poppins
            )
        },
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = FontIconColor,
            focusedBorderColor = PrimaryColor,
            cursorColor = PrimaryColor
        )
    )
}
