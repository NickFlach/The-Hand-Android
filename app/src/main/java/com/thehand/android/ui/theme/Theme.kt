package com.thehand.android.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4A5568),
    onPrimary = Color.White,
    secondary = Color(0xFF718096),
    onSecondary = Color.White,
    background = Color(0xFFFAFAFA),
    surface = Color.White,
    onBackground = Color(0xFF1A202C),
    onSurface = Color(0xFF2D3748)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA0AEC0),
    onPrimary = Color(0xFF1A202C),
    secondary = Color(0xFF718096),
    onSecondary = Color.White,
    background = Color(0xFF1A202C),
    surface = Color(0xFF2D3748),
    onBackground = Color(0xFFE2E8F0),
    onSurface = Color(0xFFF7FAFC)
)

@Composable
fun TheHandTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
