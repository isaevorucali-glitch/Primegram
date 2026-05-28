package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

data class PrimeThemePalette(
    val name: String,
    val isDark: Boolean,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val background: Color,
    val surface: Color,
    val onPrimary: Color = Color.White,
    val onBackground: Color,
    val onSurface: Color
)

object PrimeThemes {
    val list = listOf(
        PrimeThemePalette(
            name = "Immersive UI (Slate Dark)",
            isDark = true,
            primary = Color(0xFF3B82F6),
            secondary = Color(0xFF94A3B8),
            tertiary = Color(0xFFC084FC),
            background = Color(0xFF0F1115),
            surface = Color(0xFF15171D),
            onBackground = Color(0xFFF1F5F9),
            onSurface = Color(0xFFF1F5F9)
        ),
        PrimeThemePalette(
            name = "Violet Aurora",
            isDark = true,
            primary = Color(0xFFBB86FC),
            secondary = Color(0xFF03DAC6),
            tertiary = Color(0xFFCF6679),
            background = Color(0xFF180A2B),
            surface = Color(0xFF26143D),
            onBackground = Color(0xFFEFE6FF),
            onSurface = Color(0xFFEFE6FF)
        ),
        PrimeThemePalette(
            name = "Emerald Ghost",
            isDark = true,
            primary = Color(0xFF00E676),
            secondary = Color(0xFF1DE9B6),
            tertiary = Color(0xFFECEFF1),
            background = Color(0xFF0B140E),
            surface = Color(0xFF14241A),
            onBackground = Color(0xFFE0F2F1),
            onSurface = Color(0xFFE0F2F1)
        ),
        PrimeThemePalette(
            name = "Cyberpunk Tokyo",
            isDark = true,
            primary = Color(0xFFFF007F),
            secondary = Color(0xFF00F0FF),
            tertiary = Color(0xFFFFE600),
            background = Color(0xFF0B071E),
            surface = Color(0xFF1F103F),
            onBackground = Color(0xFFE1F5FE),
            onSurface = Color(0xFFE1F5FE)
        ),
        PrimeThemePalette(
            name = "Ocean Depths",
            isDark = true,
            primary = Color(0xFF29B6F6),
            secondary = Color(0xFF26A69A),
            tertiary = Color(0xFFAB47BC),
            background = Color(0xFF0A192F),
            surface = Color(0xFF172A45),
            onBackground = Color(0xFFF1F5F9),
            onSurface = Color(0xFFE2E8F0)
        ),
        PrimeThemePalette(
            name = "Royal Amethyst",
            isDark = true,
            primary = Color(0xFFCE93D8),
            secondary = Color(0xFF9FA8DA),
            tertiary = Color(0xFFF48FB1),
            background = Color(0xFF1A122E),
            surface = Color(0xFF2B1F45),
            onBackground = Color(0xFFF3E5F5),
            onSurface = Color(0xFFF3E5F5)
        ),
        PrimeThemePalette(
            name = "Prestigious Gold",
            isDark = true,
            primary = Color(0xFFFFD700),
            secondary = Color(0xFFCFB53B),
            tertiary = Color(0xFFFFECB3),
            background = Color(0xFF1A1A10),
            surface = Color(0xFF2C2C1D),
            onBackground = Color(0xFFFFFDF0),
            onSurface = Color(0xFFFFFDF0)
        ),
        PrimeThemePalette(
            name = "Crimson Signal",
            isDark = true,
            primary = Color(0xFFFF5252),
            secondary = Color(0xFFFF7043),
            tertiary = Color(0xFFFFB300),
            background = Color(0xFF1D0000),
            surface = Color(0xFF2F1212),
            onBackground = Color(0xFFFFECEC),
            onSurface = Color(0xFFFFECEC)
        ),
        PrimeThemePalette(
            name = "Terminal 80",
            isDark = true,
            primary = Color(0xFF33FF33),
            secondary = Color(0xFF00FF00),
            tertiary = Color(0xFF00AA00),
            background = Color(0xFF000000),
            surface = Color(0xFF0A0A0A),
            onBackground = Color(0xFF33FF33),
            onSurface = Color(0xFF33FF33)
        ),
        PrimeThemePalette(
            name = "Sapphire Spark",
            isDark = true,
            primary = Color(0xFF3D5AFE),
            secondary = Color(0xFF00B0FF),
            tertiary = Color(0xFF651FFF),
            background = Color(0xFF080D21),
            surface = Color(0xFF131B3D),
            onBackground = Color(0xFFECEFF1),
            onSurface = Color(0xFFECEFF1)
        ),
        // LIGHT THEMES
        PrimeThemePalette(
            name = "Platinum White (Light)",
            isDark = false,
            primary = Color(0xFF0084FF),
            secondary = Color(0xFF78909C),
            tertiary = Color(0xFFFF7043),
            background = Color(0xFFFFFFFF),
            surface = Color(0xFFFFFFFF),
            onPrimary = Color.White,
            onBackground = Color(0xFF1A1A1C),
            onSurface = Color(0xFF1A1A1C)
        ),
        PrimeThemePalette(
            name = "Sakura Petals",
            isDark = false,
            primary = Color(0xFFEC407A),
            secondary = Color(0xFFF48FB1),
            tertiary = Color(0xFFFFAB40),
            background = Color(0xFFFFFFFF),
            surface = Color(0xFFFFFFFF),
            onPrimary = Color.White,
            onBackground = Color(0xFF4A148C),
            onSurface = Color(0xFF4A148C)
        ),
        PrimeThemePalette(
            name = "Warm Sepia",
            isDark = false,
            primary = Color(0xFF8D6E63),
            secondary = Color(0xFFA1887F),
            tertiary = Color(0xFFFFB74D),
            background = Color(0xFFFFFFFF),
            surface = Color(0xFFFFFFFF),
            onPrimary = Color.White,
            onBackground = Color(0xFF3E2723),
            onSurface = Color(0xFF4E342E)
        ),
        PrimeThemePalette(
            name = "Desert Mirage",
            isDark = false,
            primary = Color(0xFFD4AF37),
            secondary = Color(0xFFBCAAA4),
            tertiary = Color(0xFFFFE082),
            background = Color(0xFFFFFFFF),
            surface = Color(0xFFFFFFFF),
            onPrimary = Color.White,
            onBackground = Color(0xFF3E2723),
            onSurface = Color(0xFF3E2723)
        ),
        PrimeThemePalette(
            name = "Aero Mint",
            isDark = false,
            primary = Color(0xFF00BFA5),
            secondary = Color(0xFF26A69A),
            tertiary = Color(0xFF26C6DA),
            background = Color(0xFFFFFFFF),
            surface = Color(0xFFFFFFFF),
            onPrimary = Color.White,
            onBackground = Color(0xFF004D40),
            onSurface = Color(0xFF004D40)
        ),
        PrimeThemePalette(
            name = "Lavender Fields",
            isDark = false,
            primary = Color(0xFF7E57C2),
            secondary = Color(0xFF9575CD),
            tertiary = Color(0xFFAB47BC),
            background = Color(0xFFFFFFFF),
            surface = Color(0xFFFFFFFF),
            onPrimary = Color.White,
            onBackground = Color(0xFF311B92),
            onSurface = Color(0xFF311B92)
        ),
        PrimeThemePalette(
            name = "Pacific Sky",
            isDark = false,
            primary = Color(0xFF0288D1),
            secondary = Color(0xFF03A9F4),
            tertiary = Color(0xFF00BCD4),
            background = Color(0xFFFFFFFF),
            surface = Color(0xFFFFFFFF),
            onPrimary = Color.White,
            onBackground = Color(0xFF01579B),
            onSurface = Color(0xFF01579B)
        ),
        PrimeThemePalette(
            name = "Banana Split",
            isDark = false,
            primary = Color(0xFFFBC02D),
            secondary = Color(0xFFFDD835),
            tertiary = Color(0xFFFFE082),
            background = Color(0xFFFFFFFF),
            surface = Color(0xFFFFFFFF),
            onPrimary = Color(0xFF3E2723),
            onBackground = Color(0xFF5D4037),
            onSurface = Color(0xFF5D4037)
        ),
        PrimeThemePalette(
            name = "Monochrome Noir",
            isDark = true,
            primary = Color(0xFFFFFFFF),
            secondary = Color(0xFFB0BEC5),
            tertiary = Color(0xFFECEFF1),
            background = Color(0xFF000000),
            surface = Color(0xFF121212),
            onPrimary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White
        ),
        PrimeThemePalette(
            name = "Vaporwave Synth",
            isDark = true,
            primary = Color(0xFFFF71CE),
            secondary = Color(0xFF01CDFE),
            tertiary = Color(0xFFB967FF),
            background = Color(0xFF2A1B3D),
            surface = Color(0xFF44318D),
            onBackground = Color(0xFFFFF3FA),
            onSurface = Color(0xFFFFF3FA)
        ),
        PrimeThemePalette(
            name = "Dracula's Vault",
            isDark = true,
            primary = Color(0xFFBD93F9),
            secondary = Color(0xFF50FA7B),
            tertiary = Color(0xFFFF79C6),
            background = Color(0xFF282A36),
            surface = Color(0xFF343746),
            onBackground = Color(0xFFF8F8F2),
            onSurface = Color(0xFFF8F8F2)
        ),
        PrimeThemePalette(
            name = "Toxic Acid Lime",
            isDark = true,
            primary = Color(0xFFA3E635),
            secondary = Color(0xFF84CC16),
            tertiary = Color(0xFFFACC15),
            background = Color(0xFF090D16),
            surface = Color(0xFF111827),
            onBackground = Color(0xFFECFDF5),
            onSurface = Color(0xFFECFDF5)
        ),
        PrimeThemePalette(
            name = "Amber Ember",
            isDark = true,
            primary = Color(0xFFF97316),
            secondary = Color(0xFFF59E0B),
            tertiary = Color(0xFFEF4444),
            background = Color(0xFF180802),
            surface = Color(0xFF2D1107),
            onBackground = Color(0xFFFFF1F2),
            onSurface = Color(0xFFFFF1F2)
        ),
        PrimeThemePalette(
            name = "Deep Forest Wood",
            isDark = true,
            primary = Color(0xFF22C55E),
            secondary = Color(0xFF10B981),
            tertiary = Color(0xFF84CC16),
            background = Color(0xFF051B11),
            surface = Color(0xFF0C2B1C),
            onBackground = Color(0xFFF0FDF4),
            onSurface = Color(0xFFF0FDF4)
        ),
        PrimeThemePalette(
            name = "Lavender Dawn Light",
            isDark = false,
            primary = Color(0xFF8B5CF6),
            secondary = Color(0xFFA78BFA),
            tertiary = Color(0xFFEC4899),
            background = Color(0xFFFFFFFF),
            surface = Color(0xFFFFFFFF),
            onBackground = Color(0xFF4C1D95),
            onSurface = Color(0xFF4C1D95)
        ),
        PrimeThemePalette(
            name = "Nordic Pale Frost",
            isDark = false,
            primary = Color(0xFF0284C7),
            secondary = Color(0xFF38BDF8),
            tertiary = Color(0xFF14B8A6),
            background = Color(0xFFFFFFFF),
            surface = Color(0xFFFFFFFF),
            onBackground = Color(0xFF0369A1),
            onSurface = Color(0xFF0369A1)
        )
    )

    fun getTheme(index: Int): PrimeThemePalette {
        if (index < 0 || index >= list.size) return list[0]
        return list[index]
    }
}

fun PrimeThemePalette.toColorScheme(): ColorScheme {
    return if (isDark) {
        darkColorScheme(
            primary = primary,
            secondary = secondary,
            tertiary = tertiary,
            background = background,
            surface = surface,
            onPrimary = onPrimary,
            onBackground = onBackground,
            onSurface = onSurface
        )
    } else {
        lightColorScheme(
            primary = primary,
            secondary = secondary,
            tertiary = tertiary,
            background = background,
            surface = surface,
            onPrimary = onPrimary,
            onBackground = onBackground,
            onSurface = onSurface
        )
    }
}
