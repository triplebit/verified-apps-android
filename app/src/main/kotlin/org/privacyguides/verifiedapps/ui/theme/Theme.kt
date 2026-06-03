package org.privacyguides.verifiedapps.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import org.privacyguides.verifiedapps.preferences.PreferencesViewModel

private val DarkColorScheme = darkColorScheme(
    primary = TealDarkPrimary,
    onPrimary = TealDarkOnPrimary,
    primaryContainer = TealDarkPrimaryContainer,
    onPrimaryContainer = TealDarkOnPrimaryContainer,
    background = Color(0xFF101413),
    onBackground = Color(0xFFE2E3E1),
    surface = Color(0xFF101413),
    onSurface = Color(0xFFE2E3E1),
    surfaceContainerLow = Color(0xFF1A211F),
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = Color(0xFF242B29),
    surfaceContainerHighest = Color(0xFF2F3634),
    outline = Color(0xFF8A9290),
    outlineVariant = Color(0xFF3F4745),
)

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = TealOnPrimary,
    primaryContainer = TealPrimaryContainer,
    onPrimaryContainer = TealOnPrimaryContainer,
    background = Color(0xFFFBFDFC),
    onBackground = Color(0xFF191C1B),
    surface = Color(0xFFFBFDFC),
    onSurface = Color(0xFF191C1B),
    surfaceContainerLow = Color(0xFFF0F4F3),
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = Color(0xFFE6ECEC),
    surfaceContainerHighest = Color(0xFFDAE4E2),
    outline = Color(0xFF6F7977),
    outlineVariant = Color(0xFFBFC9C6),
)

@Composable
fun AppVerifierTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    preferencesViewModel: PreferencesViewModel,
    content: @Composable () -> Unit,
) {
    val settingsUiState by preferencesViewModel.uiState.collectAsState()
    val pitchBlackBackground = settingsUiState.pitchBlackBackground.second.value && darkTheme

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                if (pitchBlackBackground) {
                    dynamicDarkColorScheme(context).copy(
                        background = Color.Black,
                        surface = Color.Black,
                    )
                } else {
                    dynamicDarkColorScheme(context)
                }
            } else {
                dynamicLightColorScheme(context)
            }
        }

        darkTheme -> {
            if (pitchBlackBackground) {
                DarkColorScheme.copy(
                    background = Color.Black,
                    surface = Color.Black,
                )
            } else {
                DarkColorScheme
            }
        }

        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content,
    )
}
