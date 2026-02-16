package de.sportkalender.nativeapp.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val BrandPrimary = Color(0xFF1A5FB4)
val BrandSecondary = Color(0xFF355F8A)
val BrandTertiary = Color(0xFF006A67)

val LightColors = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E3FF),
    onPrimaryContainer = Color(0xFF001B3E),
    secondary = BrandSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD2E4FF),
    onSecondaryContainer = Color(0xFF001D36),
    tertiary = BrandTertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF6FF7F0),
    onTertiaryContainer = Color(0xFF00201F),
    background = Color(0xFFF8F9FF),
    onBackground = Color(0xFF1A1B21),
    surface = Color(0xFFF8F9FF),
    onSurface = Color(0xFF1A1B21),
    surfaceVariant = Color(0xFFE0E2EC),
    onSurfaceVariant = Color(0xFF44474F),
    outline = Color(0xFF74777F)
)

val DarkColors = darkColorScheme(
    primary = Color(0xFFA7C8FF),
    onPrimary = Color(0xFF003061),
    primaryContainer = Color(0xFF004787),
    onPrimaryContainer = Color(0xFFD6E3FF),
    secondary = Color(0xFFA0C9F9),
    onSecondary = Color(0xFF003258),
    secondaryContainer = Color(0xFF1D486F),
    onSecondaryContainer = Color(0xFFD2E4FF),
    tertiary = Color(0xFF4CDAD4),
    onTertiary = Color(0xFF003735),
    tertiaryContainer = Color(0xFF00504E),
    onTertiaryContainer = Color(0xFF6FF7F0),
    background = Color(0xFF111318),
    onBackground = Color(0xFFE2E2E9),
    surface = Color(0xFF111318),
    onSurface = Color(0xFFE2E2E9),
    surfaceVariant = Color(0xFF44474F),
    onSurfaceVariant = Color(0xFFC4C6D0),
    outline = Color(0xFF8E9099)
)
