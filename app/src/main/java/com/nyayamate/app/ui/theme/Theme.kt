package com.nyayamate.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

val LightColorScheme = lightColorScheme(
    primary = SovereignNavy,
    onPrimary = WhiteText,
    secondary = AccentTerracotta,
    onSecondary = WhiteText,
    background = WarmCanvasBg,
    onBackground = TextPrimaryDark,       // Background mela ulla ella text-um dark ink aagidum
    surface = SandstoneCard,
    onSurface = TextPrimaryDark,          // Cards mela ulla text-um visible aagidum
    surfaceVariant = SandstoneCard,
    onSurfaceVariant = TextSecondaryDark
)

@Composable
fun NyayaMateTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}

/**
 * Standard reusable 100% accessible high-contrast colors for OutlinedTextField
 * Guarantees typed text is deep ink black (#14181F) and placeholder is slate gray (#5A606A).
 */
@Composable
fun nyayaOutlinedTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextPrimaryDark,
        unfocusedTextColor = TextPrimaryDark,
        focusedContainerColor = SandstoneCard,
        unfocusedContainerColor = SandstoneCard,
        cursorColor = SovereignNavy,
        focusedBorderColor = SovereignNavy,
        unfocusedBorderColor = CardBorderStroke,
        focusedPlaceholderColor = TextMutedDark,
        unfocusedPlaceholderColor = TextMutedDark
    )
}
