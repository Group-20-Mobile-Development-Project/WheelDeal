package com.example.wheeldeal.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wheeldeal.R
import com.example.wheeldeal.ui.theme.PrimaryColor
import androidx.compose.foundation.layout.systemBarsPadding

@Composable
fun TopNavigationBar(
    onMessageClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryColor)
            .padding(top = 30.dp) // Slightly more top padding to avoid status bar overlap
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // Compact height
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.height(60.dp)
            )

            // Right icons
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.notification),
                        contentDescription = "Notification",
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onMessageClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.message),
                        contentDescription = "Message",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
