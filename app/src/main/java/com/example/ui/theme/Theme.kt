package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun MyApplicationTheme(
  paletteIndex: Int = 0,
  content: @Composable () -> Unit,
) {
  val palette = PrimeThemes.getTheme(paletteIndex)
  val colorScheme = palette.toColorScheme()

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
