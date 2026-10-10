package com.justra.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Immutable
data class NyayaCustomColors(
    val containerSuccess: Color,
    val onContainerSuccess: Color,
    val containerWarning: Color,
    val onContainerWarning: Color,
    val containerAlert: Color,
    val onContainerAlert: Color,
    val crushedBerry: Color = CrushedBerry,
    val raspberry: Color = Raspberry,
    val bubblegumPink: Color = BubblegumPink,
    val pinkMist: Color = PinkMist,
    val lightCyan: Color = LightCyan,
    val backgroundColor: Color = LightCyan,
    val secondaryTextColor: Color = Raspberry,
    val gradientRight: Brush = PaletteGradientRight,
    val gradientBottomRight: Brush = PaletteGradientBottomRight
)

val LocalNyayaCustomColors = staticCompositionLocalOf {
    NyayaCustomColors(
        containerSuccess = SageHerbSuccessContainer,
        onContainerSuccess = OnSageHerbSuccess,
        containerWarning = MutedAmberWarningContainer,
        onContainerWarning = OnMutedAmberWarning,
        containerAlert = DeepHennaAlertContainer,
        onContainerAlert = OnDeepHennaAlert,
        crushedBerry = CrushedBerry,
        raspberry = Raspberry,
        bubblegumPink = BubblegumPink,
        pinkMist = PinkMist,
        lightCyan = LightCyan,
        backgroundColor = LightCyan,
        secondaryTextColor = Raspberry,
        gradientRight = PaletteGradientRight,
        gradientBottomRight = PaletteGradientBottomRight
    )
}

val MaterialTheme.nyayaColors: NyayaCustomColors
    @Composable
    get() = LocalNyayaCustomColors.current

private val LightColorScheme = lightColorScheme(
    primary = SovereignNavy,
    onPrimary = Color.White,
    primaryContainer = SoftNavyContainer,
    onPrimaryContainer = OnNavyContainer,
    secondary = AccentTerracotta,
    onSecondary = Color.White,
    secondaryContainer = TerracottaBadgeContainer,
    onSecondaryContainer = OnTerracottaText,
    tertiary = SovereignNavy,
    onTertiary = Color.White,
    tertiaryContainer = SoftNavyContainer,
    onTertiaryContainer = OnNavyContainer,
    background = WarmCanvasBg,
    onBackground = TextPrimaryDark,
    surface = SandstoneCard,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFFE8E0D2),
    onSurfaceVariant = TextSecondaryDark,
    surfaceTint = SovereignNavy,
    inverseSurface = SovereignNavy,
    inverseOnSurface = WarmCanvasBg,
    inversePrimary = SoftNavyContainer,
    outline = CardBorderStroke,
    outlineVariant = CardBorderStrokeLight,
    scrim = Color.Black,
    error = TextOnAlertCrimson,
    errorContainer = AlertCrimson,
    onError = Color.White,
    onErrorContainer = TextOnAlertCrimson
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF38BDF8),
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF1E293B),
    onPrimaryContainer = Color(0xFFE2E8F0),
    secondary = Color(0xFFFB923C),
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF334155),
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = Color(0xFF38BDF8),
    onTertiary = Color(0xFF0F172A),
    tertiaryContainer = Color(0xFF1E293B),
    onTertiaryContainer = Color(0xFFE2E8F0),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    surfaceTint = Color(0xFF38BDF8),
    inverseSurface = Color(0xFFE2E8F0),
    inverseOnSurface = Color(0xFF0F172A),
    inversePrimary = Color(0xFF1E293B),
    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF334155),
    scrim = Color.Black,
    error = DarkOnHennaAlert,
    errorContainer = DarkHennaAlertContainer,
    onError = Color(0xFF68000F),
    onErrorContainer = DarkOnHennaAlert
)

private val LightNyayaColors = NyayaCustomColors(
    containerSuccess = SageHerbSuccessContainer,
    onContainerSuccess = OnSageHerbSuccess,
    containerWarning = MutedAmberWarningContainer,
    onContainerWarning = OnMutedAmberWarning,
    containerAlert = DeepHennaAlertContainer,
    onContainerAlert = OnDeepHennaAlert,
    crushedBerry = CrushedBerry,
    raspberry = Raspberry,
    bubblegumPink = BubblegumPink,
    pinkMist = PinkMist,
    lightCyan = LightCyan,
    backgroundColor = LightCyan,
    secondaryTextColor = Raspberry,
    gradientRight = PaletteGradientRight,
    gradientBottomRight = PaletteGradientBottomRight
)

private val DarkNyayaColors = NyayaCustomColors(
    containerSuccess = DarkSageHerbContainer,
    onContainerSuccess = DarkOnSageHerb,
    containerWarning = DarkAmberWarningContainer,
    onContainerWarning = DarkOnAmberWarning,
    containerAlert = DarkHennaAlertContainer,
    onContainerAlert = DarkOnHennaAlert,
    crushedBerry = Color(0xFF38BDF8),
    raspberry = Color(0xFFFB923C),
    bubblegumPink = Color(0xFFFDBA74),
    pinkMist = Color(0xFFBAE6FD),
    lightCyan = Color(0xFF1E293B),
    backgroundColor = Color(0xFF0F172A),
    secondaryTextColor = Color(0xFFCBD5E1),
    gradientRight = PaletteGradientRight,
    gradientBottomRight = PaletteGradientBottomRight
)

/**
 * Material 3 Theme for Justra application.
 * Supports light, dark, and system themes.
 */
@Composable
fun JustraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val customColors = if (darkTheme) DarkNyayaColors else LightNyayaColors

    CompositionLocalProvider(LocalNyayaCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun NyayaMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    JustraTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    JustraTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

/**
 * Standard High-Contrast OutlinedTextField colors conforming to WCAG 2.1 AA (>12:1 contrast).
 * Enforces deep ink black text on sandstone containers in light mode, and bright slate text on dark surfaces in dark mode.
 */
@Composable
fun justraOutlinedTextFieldColors(): androidx.compose.material3.TextFieldColors =
    nyayaOutlinedTextFieldColors()

@Composable
fun nyayaOutlinedTextFieldColors(): androidx.compose.material3.TextFieldColors {
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF0F172A) || MaterialTheme.colorScheme.surface == Color(0xFF1E293B)
    return if (isDark) {
        androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color(0xFFE2E8F0),
            unfocusedTextColor = Color(0xFFE2E8F0),
            focusedContainerColor = Color(0xFF1E293B),
            unfocusedContainerColor = Color(0xFF1E293B),
            cursorColor = Color(0xFF38BDF8),
            focusedBorderColor = Color(0xFF38BDF8),
            unfocusedBorderColor = Color(0xFF475569),
            focusedPlaceholderColor = Color(0xFFCBD5E1),
            unfocusedPlaceholderColor = Color(0xFF94A3B8),
            focusedSupportingTextColor = Color(0xFFCBD5E1),
            unfocusedSupportingTextColor = Color(0xFF94A3B8)
        )
    } else {
        androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color(0xFF14181F),
            unfocusedTextColor = Color(0xFF14181F),
            focusedContainerColor = Color(0xFFF3ECE1),
            unfocusedContainerColor = Color(0xFFF3ECE1),
            cursorColor = Color(0xFF0F1E36),
            focusedBorderColor = Color(0xFF0F1E36),
            unfocusedBorderColor = Color(0xFFD4CAB8),
            focusedPlaceholderColor = Color(0xFF5A606A),
            unfocusedPlaceholderColor = Color(0xFF5A606A),
            focusedSupportingTextColor = Color(0xFF4A4E57),
            unfocusedSupportingTextColor = Color(0xFF4A4E57)
        )
    }
}
