package com.example.wheeldeal.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun HeroSection(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFC107),
                            Color(0xFFFFA000)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(size.width * 0.6f, size.height * 0.1f)
                quadraticTo(
                    size.width * 0.8f, size.height * 0.4f,
                    size.width * 1.1f, size.height * 0.3f
                )
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.2f),
                        Color.Transparent
                    )
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Get Your\nDream CAR Here",
                style = MaterialTheme.typography.headlineLarge.copy(
                    letterSpacing = 0.8.sp,
                    color = Color(0xFF0E2F56)
                )
            )

            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFF0E2F56),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                // Replace body1 with bodyMedium
                Text(
                    text = "Wheel Deal",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Preview
@Composable
fun HeroSectionPreview() {
    HeroSection()
}