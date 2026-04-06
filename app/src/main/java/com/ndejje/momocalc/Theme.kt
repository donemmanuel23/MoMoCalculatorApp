package com.ndejje.momocalc

import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import android.content.res.Configuration
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.tooling.preview.Preview

private val DarkColorScheme = darkColorScheme(
  primary         = BrandGold,        // gold becomes the hero in dark mode
  onPrimary       = NavyBlueDark,
  secondary       = NavyBlue,
  onSecondary     = White,
  background      = DarkBackground,
  onBackground    = OnDarkText,
  surface         = DarkSurface,
  onSurface       = OnDarkText,
  error           = ErrorRed,
  onError         = OnErrorWhite
)

private val LightColorScheme = lightColorScheme(
  primary         = NavyBlue,
  onPrimary       = White,
  secondary       = BrandGold,
  onSecondary     = NavyBlueDark,
  background      = LightGrey,
  onBackground    = DarkSurface,
  surface         = White,
  onSurface       = DarkSurface,
  error           = ErrorRed,
  onError         = OnErrorWhite
)

@Composable
fun MoMoAppTheme(
  darkTheme: Boolean = isSystemInDarkTheme(), // auto-detect by default
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography  = MoMoTypography,
    shapes      = MoMoShapes,
    content     = content
  )
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun PreviewLight() {
  MoMoAppTheme(darkTheme = false) {
    MoMoCalcScreen(
        network = Network.MTN,
        onNetworkChange = {}
    )
  }
}

@Preview(
  name = "Dark Mode",
  showBackground = true,
  uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewDark() {
  MoMoAppTheme(darkTheme = true) {
    MoMoCalcScreen(
        network = Network.MTN,
        onNetworkChange = {}
    )
  }
}
