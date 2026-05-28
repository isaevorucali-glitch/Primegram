package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun parseHexColor(hex: String, default: Color): Color {
    if (hex.isBlank()) return default
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        default
    }
}

@Composable
fun MyApplicationTheme(
    paletteIndex: Int = 0,
    customPrimaryHex: String = "",
    customSecondaryHex: String = "",
    customBackgroundHex: String = "",
    customFontFamily: String = "Default",
    content: @Composable () -> Unit,
) {
    val palette = PrimeThemes.getTheme(paletteIndex)
    
    // Fallback to preset scheme first
    val baseScheme = palette.toColorScheme()
    
    // Resolve overrides
    val finalPrimary = parseHexColor(customPrimaryHex, baseScheme.primary)
    val finalSecondary = parseHexColor(customSecondaryHex, baseScheme.secondary)
    val finalBackground = parseHexColor(customBackgroundHex, baseScheme.background)
    val finalSurface = if (customBackgroundHex.isNotBlank()) parseHexColor(customBackgroundHex, baseScheme.surface) else baseScheme.surface

    val colorScheme = if (palette.isDark) {
        darkColorScheme(
            primary = finalPrimary,
            secondary = finalSecondary,
            background = finalBackground,
            surface = finalSurface,
            tertiary = baseScheme.tertiary,
            onPrimary = baseScheme.onPrimary,
            onBackground = baseScheme.onBackground,
            onSurface = baseScheme.onSurface
        )
    } else {
        lightColorScheme(
            primary = finalPrimary,
            secondary = finalSecondary,
            background = finalBackground,
            surface = finalSurface,
            tertiary = baseScheme.tertiary,
            onPrimary = baseScheme.onPrimary,
            onBackground = baseScheme.onBackground,
            onSurface = baseScheme.onSurface
        )
    }

    // Resolve font
    val resolvedFontFamily = when (customFontFamily) {
        "Serif" -> FontFamily.Serif
        "SansSerif" -> FontFamily.SansSerif
        "Monospace" -> FontFamily.Monospace
        "Cursive" -> FontFamily.Cursive
        else -> FontFamily.Default
    }

    val customTypography = androidx.compose.material3.Typography(
        bodyLarge = TextStyle(
            fontFamily = resolvedFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = resolvedFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = resolvedFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),
        titleLarge = TextStyle(
            fontFamily = resolvedFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp
        ),
        titleMedium = TextStyle(
            fontFamily = resolvedFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 24.sp
        ),
        labelSmall = TextStyle(
            fontFamily = resolvedFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = customTypography,
        content = content
    )
}
