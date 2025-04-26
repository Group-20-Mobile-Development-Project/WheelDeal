package com.example.wheeldeal.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Badge
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wheeldeal.R
import com.example.wheeldeal.ui.theme.PrimaryColor
import com.example.wheeldeal.viewmodel.NotificationViewModel

@Composable
fun TopNavigationBar(
    onMessageClick: () -> Unit,
    onNotificationClick: () -> Unit,
    notificationViewModel: NotificationViewModel = viewModel()
) {
    val notifications = notificationViewModel.notifications.collectAsState()
    val unreadCount = notifications.value.count { !it.isRead } // Calculate unread notifications

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
                // Notification Icon with Badge
                Box {
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
                    if (unreadCount > 0) {
                        Badge(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = 4.dp),
                            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.error
                        ) {
                            androidx.compose.material3.Text(
                                text = unreadCount.toString(),
                                color = androidx.compose.ui.graphics.Color.White
                            )
                        }
                    }
                }

                // Message Icon
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