package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
  primary = BluePrimary,
  onPrimary = Color.White,
  primaryContainer = BlueContainer,
  onPrimaryContainer = OnBlueContainer,
  secondary = BlueSecondary,
  onSecondary = Color.White,
  tertiary = BlueTertiary,
  background = WhiteBackground,
  surface = WhiteSurface,
  surfaceVariant = SurfaceVariant,
  onBackground = TextPrimary,
  onSurface = TextPrimary,
  onSurfaceVariant = TextSecondary,
  outline = OutlineColor,
  outlineVariant = OutlineVariant,
  error = StatusError,
  onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
  primary = DarkPrimary,
  onPrimary = Color(0xFF00296B),
  primaryContainer = DarkContainer,
  onPrimaryContainer = Color(0xFFD1E4FF),
  secondary = BlueSecondary,
  background = DarkBackground,
  surface = DarkSurface,
  surfaceVariant = Color(0xFF334155),
  onBackground = Color(0xFFF8FAFC),
  onSurface = Color(0xFFF8FAFC)
)

@Composable
fun PhotoSessionTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
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
    typography = Typography,
    content = content
  )
}
