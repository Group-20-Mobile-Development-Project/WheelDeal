package com.example.wheeldeal.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wheeldeal.R
import com.example.wheeldeal.ui.theme.WhiteColor

@Composable
fun TopNavigationBar(
    onMessageClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(100.dp),  // Increased the height slightly to allow more space
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Logo
        Image(
            painter = painterResource(id = R.drawable.logo), // Your logo image
            contentDescription = "Logo",
            modifier = Modifier.height(500.dp) // Increased the size of the logo
        )

        // Right side: Notification and Message icons
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp), // Reduced spacing to make them closer
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Notification Icon
            IconButton(onClick = onNotificationClick) {
                Image(
                    painter = painterResource(id = R.drawable.notification), // Your notification image
                    contentDescription = "Notification",
                    modifier = Modifier.size(24.dp)
                )
            }

            // Message Icon
            IconButton(onClick = onMessageClick) {
                Image(
                    painter = painterResource(id = R.drawable.message), // Your message image
                    contentDescription = "Message",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopNavigationBarPreview() {
    TopNavigationBar(
        onMessageClick = { /* Handle message click */ },
        onNotificationClick = { /* Handle notification click */ }
    )
}
