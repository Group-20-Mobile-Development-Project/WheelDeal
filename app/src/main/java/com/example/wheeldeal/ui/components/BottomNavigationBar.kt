package com.example.wheeldeal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wheeldeal.ui.theme.FontIconColor
import com.example.wheeldeal.ui.theme.PrimaryColor
import com.example.wheeldeal.ui.theme.WhiteColor

data class BottomNavItem(
    val label: String,
    val icon: ImageVector
)


@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    onItemSelected: (BottomNavItem) -> Unit
) {
    // Track the selected item
    val selectedItem = remember { mutableStateOf(items.first()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .background(PrimaryColor),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = (item == selectedItem.value)

            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false), // default ripple effect
                    ) {
                        selectedItem.value = item
                        onItemSelected(item)
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Icon
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        // White if selected, otherwise your dark blue
                        tint = if (isSelected) WhiteColor else FontIconColor
                    )
                    // Label
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        // White if selected, otherwise dark blue
                        color = if (isSelected) WhiteColor else FontIconColor,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
