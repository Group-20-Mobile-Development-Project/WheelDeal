package com.example.wheeldeal.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.wheeldeal.model.CarListing
import com.example.wheeldeal.ui.theme.DarkBlue
import com.example.wheeldeal.ui.theme.Poppins
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CarCard(
    modifier: Modifier = Modifier,
    listing: CarListing,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    var firstName by remember { mutableStateOf("Car Owner") }
    val interactionSource = remember { MutableInteractionSource() }
    val pressed = interactionSource.collectIsPressedAsState()
    val scale = animateFloatAsState(
        targetValue = if (pressed.value) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
    )

    // Heart icon "pop" animation
    val favoriteScale = remember { Animatable(1f) }
    LaunchedEffect(isFavorite) {
        if (isFavorite) {
            favoriteScale.animateTo(1.3f, animationSpec = tween(150))
            favoriteScale.animateTo(1f, animationSpec = tween(150))
        }
    }

    // Load owner name
    LaunchedEffect(listing.userId) {
        try {
            val doc = FirebaseFirestore.getInstance()
                .collection("users")
                .document(listing.userId)
                .get()
                .await()
            firstName = doc.getString("firstName") ?: "Car Owner"
        } catch (_: Exception) {}
    }

    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .width(200.dp)
            .padding(8.dp)
            .scale(scale.value),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp, 8.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        interactionSource = interactionSource
    ) {
        Column {
            // Image pager + overlays
            Box(
                Modifier
                    .height(130.dp)
                    .fillMaxWidth()
            ) {
                if (listing.photos.isNotEmpty()) {
                    val pagerState = rememberPagerState(pageCount = { listing.photos.size })
                    val scope = rememberCoroutineScope()

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        AsyncImage(
                            model = listing.photos[page],
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    if (listing.photos.size > 1) {
                        Row(
                            Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(listing.photos.size) { idx ->
                                val dotSize = if (pagerState.currentPage == idx) 6.dp else 5.dp
                                Box(
                                    Modifier
                                        .padding(2.dp)
                                        .size(dotSize)
                                        .background(
                                            color = if (pagerState.currentPage == idx)
                                                Color.White else Color.White.copy(alpha = 0.5f),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            scope.launch {
                                                pagerState.animateScrollToPage(idx)
                                            }
                                        }
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.BrokenImage,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Gradient overlay
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0x90000000)),
                                startY = 0f, endY = 300f
                            )
                        )
                )

                // Time badge
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xE6FFFFFF),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Text(
                        formatTimeAgo(listing.createdAt.toDate().time),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp, fontWeight = FontWeight.Medium
                        ),
                        color = Color.DarkGray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Favorite toggle
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(28.dp)
                        .shadow(2.dp, CircleShape)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                        .scale(favoriteScale.value)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) Color.Red else DarkBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Brand & model text, lowered slightly
                Text(
                    text = "${listing.brand} ${listing.model}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        color = Color.White,
                        fontSize = 16.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 8.dp, bottom = 4.dp)
                )
            }

            Column(Modifier.padding(16.dp).fillMaxWidth()) {
                // Price
                Text(
                    text = formatCurrency(listing.price),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = Poppins,
                        color = DarkBlue,
                        fontSize = 18.sp
                    )
                )

                Spacer(Modifier.height(12.dp))

                // Fuel / Condition / Location row
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        Icons.Default.LocalGasStation to listing.fuelType,
                        Icons.Default.CarRepair to listing.condition,
                        Icons.Default.LocationOn to listing.location
                    ).forEach { (icon, text) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(icon, contentDescription = null, tint = DarkBlue, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Owner + View button
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = DarkBlue,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    firstName.take(1).uppercase(),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            firstName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = onClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkBlue, contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text("View", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                    }
                }
            }
        }
    }
}

fun formatCurrency(amount: Double): String {
    val fmt = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = if (amount % 1 == 0.0) 0 else 2
        minimumFractionDigits = if (amount % 1 == 0.0) 0 else 2
    }
    return "€${fmt.format(amount)}" // Euro symbol with US number formatting
}
fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val secs = TimeUnit.MILLISECONDS.toSeconds(diff)
    val mins = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hrs  = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    return when {
        secs < 60  -> "${secs}s ago"
        mins < 60  -> "${mins}m ago"
        hrs < 24   -> "${hrs}h ago"
        days < 30  -> "${days}d ago"
        else       -> SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(timestamp))
    }
}

@Preview(showBackground = true)
@Composable
fun EnhancedCarCardPreview() {
    val sample = CarListing(
        userId = "previewUser",
        photos = listOf(
            "https://via.placeholder.com/300x150",
            "https://via.placeholder.com/300x150"
        ),
        brand = "BMW",
        model = "X5",
        price = 300000.0,
        createdAt = Timestamp(Date()),
        odometer = 65000,
        fuelType = "Diesel",
        condition = "Excellent",
        location = "Berlin"
    )
    CarCard(
        listing = sample,
        isFavorite = true,
        onToggleFavorite = {},
        onClick = {}
    )
}
