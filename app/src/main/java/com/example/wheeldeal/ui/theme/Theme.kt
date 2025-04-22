package com.example.wheeldeal.ui.theme

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush


private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    onPrimary = FontIconColor,
    background = FontIconColor,
    onBackground = WhiteColor,
    surface = FontIconColor,
    onSurface = WhiteColor
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = FontIconColor,
    background = WhiteColor,
    onBackground = FontIconColor,
    surface = WhiteColor,
    onSurface = FontIconColor
)

@Composable
fun BackgroundWrapper(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(PrimaryColor, WhiteColor))
            ),
        content = content
    )
}

@Composable
fun WheelDealTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false if you don't need dynamic color
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }


    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
