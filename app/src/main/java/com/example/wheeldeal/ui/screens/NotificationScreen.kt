package com.example.wheeldeal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.wheeldeal.R
import com.example.wheeldeal.model.NotificationItem
import com.example.wheeldeal.ui.theme.DarkBlue
import com.example.wheeldeal.ui.theme.WheelDealYellow
import com.example.wheeldeal.viewmodel.NotificationViewModel

@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = viewModel()
) {
    val notifications by viewModel.notifications.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WheelDealYellow)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Notifications",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBlue
            )
            IconButton(onClick = { viewModel.deleteAllNotifications() }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete All",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = R.drawable.notification),
            contentDescription = "Bell Icon",
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn {
            items(notifications) { notification ->
                ExpandableNotificationCard(
                    notification = notification,
                    navController = navController,
                    onMarkAsRead = { viewModel.markAsRead(it) },
                    onDelete = { viewModel.deleteNotification(it) }
                )
            }
        }
    }
}

@Composable
fun ExpandableNotificationCard(
    notification: NotificationItem,
    navController: NavController,
    onMarkAsRead: (NotificationItem) -> Unit,
    onDelete: (NotificationItem) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (notification.isRead) Color.Gray else DarkBlue)
            .clickable {
                if (!notification.isRead) {
                    onMarkAsRead(notification)
                }
                navController.navigate("buy")
            }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = notification.title,
                fontStyle = FontStyle.Italic,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { onDelete(notification) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Notification",
                    tint = MaterialTheme.colorScheme.error
                )
            }
            IconButton(onClick = { isExpanded = !isExpanded }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Expand",
                    tint = Color.White
                )
            }
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = notification.details,
                fontSize = 14.sp,
                color = Color.LightGray
            )
        }
    }
}