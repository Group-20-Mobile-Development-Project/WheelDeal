package com.example.wheeldeal.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wheeldeal.ui.theme.AppTypography
import com.example.wheeldeal.ui.theme.FontIconColor
import kotlinx.coroutines.delay

data class Ad(
    val title: String,
    val subtitle: String,
    val image: Painter? = null
)

@OptIn(ExperimentalAnimationApi::class) // Opt-in to use the experimental animation API
@Composable
fun AdsBannerSlider(
    modifier: Modifier = Modifier,
    ads: List<Ad>,
    onClick: (Ad) -> Unit = {}
) {
    var currentAdIndex by remember { mutableIntStateOf(0) }  // Replaced mutableStateOf with mutableIntStateOf
    val currentAd = ads[currentAdIndex]

    // Auto-slide logic (change ad every 5 seconds)
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000) // 5 seconds interval between ads
            currentAdIndex = (currentAdIndex + 1) % ads.size
        }
    }

    // Display the current ad with animation
    AnimatedContent(
        targetState = currentAdIndex,
        transitionSpec = {
            fadeIn(animationSpec = tween(durationMillis = 500)) with fadeOut(animationSpec = tween(durationMillis = 500))
        }
    ) { targetAdIndex ->
        val ad = ads[targetAdIndex]
        AdsBanner(
            modifier = modifier,
            title = ad.title,
            subtitle = ad.subtitle,
            adBannerImage = ad.image,
            onClick = { onClick(ad) }
        )
    }
}

@Composable
fun AdsBanner(
    modifier: Modifier = Modifier,
    title: String = "AutoShield Insurance",
    subtitle: String = "Secure your car today with flexible insurance plans.",
    adBannerImage: Painter? = null,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(10.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFE082),  // Light Yellow
                            Color(0xFFFFB300)   // Darker Yellow/Orange
                        )
                    )
                )
        ) {
            if (adBannerImage != null) {
                Image(
                    painter = adBannerImage,
                    contentDescription = "Brand Ad Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1.5f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = title,
                            style = AppTypography.titleLarge.copy(fontSize = 20.sp),
                            color = FontIconColor, // Using FontIconColor for text color
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = subtitle,
                            style = AppTypography.bodyMedium.copy(fontSize = 14.sp),
                            color = FontIconColor.copy(alpha = 0.9f), // Using FontIconColor for text color
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Button(
                            onClick = onClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Learn More", color = Color(0xFFFFA000)) // Orange color for button text
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(
                        imageVector = Icons.Default.LocalOffer,
                        contentDescription = "Ad Icon",
                        tint = FontIconColor, // Using FontIconColor for icon tint
                        modifier = Modifier
                            .size(56.dp)
                            .weight(0.5f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAdsBannerSlider() {
    val ads = listOf(
        Ad(
            title = "AutoShield Insurance",
            subtitle = "Secure your car today with flexible insurance plans.",
            image = null
        ),
        Ad(
            title = "Best Deal on Wheels",
            subtitle = "Get 10% off on all listings!",
            image = null
        ),
        Ad(
            title = "CarWiz - Maintenance Deals",
            subtitle = "Exclusive offers on car repairs.",
            image = null
        )
    )

    AdsBannerSlider(ads = ads)
}
