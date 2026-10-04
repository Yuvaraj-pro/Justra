package com.justra.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// =====================================================================
// JUSTRA HIGH-CONTRAST ACCESSIBLE THEME (WCAG 2.1 AA COMPLIANT)
// =====================================================================

// Primary Sovereign Navy: #0F1E36 (Text/icons on primary: #FFFFFF)
val SovereignNavy = Color(0xFF0F1E36)
val OnSovereignNavy = Color(0xFFFFFFFF)
val NavySurface = Color(0xFF152642)
val SoftNavyContainer = Color(0xFFDCE6F2)
val OnNavyContainer = Color(0xFF0F1E36)

// Warm Terracotta Accent: #C85A32 (Action buttons / active voice pulse)
val AccentTerracotta = Color(0xFFC85A32)
val OnAccentTerracotta = Color(0xFFFFFFFF)
val TerracottaBadgeContainer = Color(0xFFFFDBCF)
val OnTerracottaText = Color(0xFF3B1000)

// Canvas Base: #FAF7F2 (Warm Ivory replacing harsh clinical white)
val WarmCanvasBg = Color(0xFFFAF7F2)

// Elevated Surface: #F3ECE1 (Sandstone card surface)
val SandstoneCard = Color(0xFFF3ECE1)
val CardBorderStroke = Color(0xFFD4CAB8)
val CardBorderStrokeLight = Color(0xFFE2D7C5)

// Primary Inks:
// Main Content Ink: #14181F (Headings, body copy, active typed input; contrast > 12:1)
val TextPrimaryDark = Color(0xFF14181F)
// Secondary Ink: #4A4E57 (Subtitles, captions, timestamps)
val TextSecondaryDark = Color(0xFF4A4E57)
// Muted Ink: #5A606A (Placeholder hints, disabled borders)
val TextMutedDark = Color(0xFF5A606A)
val WhiteText = Color(0xFFFFFFFF)

// Status Badges (Strictly dark, visible text on tinted containers):
// Verified / Green Badge: Background #D1E7D0 -> Content Text #0F3815 (Dark Forest Green)
val VerifiedSageGreen = Color(0xFFD1E7D0)
val TextOnSageGreen = Color(0xFF0F3815)

// Warning / Amber Badge: Background #FDE3B8 -> Content Text #4A2B00 (Deep Walnut)
val WarningAmber = Color(0xFFFDE3B8)
val TextOnWarningAmber = Color(0xFF4A2B00)

// Alert / Red Badge: Background #FCDAD4 -> Content Text #5E130A (Deep Crimson)
val AlertCrimson = Color(0xFFFCDAD4)
val TextOnAlertCrimson = Color(0xFF5E130A)

// Secondary Decorative Accents
val CrushedBerry = Color(0xFF880D1E)
val Raspberry = Color(0xFFDD2D4A)
val BubblegumPink = Color(0xFFF26A8D)
val PinkMist = Color(0xFFF49CBB)
val LightCyan = Color(0xFFE3F5F8)

// Gradients
val PaletteGradientTop = Brush.verticalGradient(
    listOf(SovereignNavy, AccentTerracotta)
)
val PaletteGradientRight = Brush.horizontalGradient(
    listOf(SovereignNavy, AccentTerracotta)
)
val PaletteGradientBottom = Brush.verticalGradient(
    listOf(SovereignNavy, AccentTerracotta)
)
val PaletteGradientLeft = Brush.horizontalGradient(
    listOf(AccentTerracotta, SovereignNavy)
)
val PaletteGradientBottomRight = Brush.linearGradient(
    listOf(SovereignNavy, AccentTerracotta)
)
val PaletteGradientTopRight = Brush.linearGradient(
    listOf(SovereignNavy, AccentTerracotta)
)
val BerryRaspberryGradient = Brush.horizontalGradient(
    listOf(SovereignNavy, AccentTerracotta)
)
val BerryPinkGradient = Brush.horizontalGradient(
    listOf(SovereignNavy, AccentTerracotta)
)
val BerryToCyanGradient = Brush.horizontalGradient(
    listOf(SovereignNavy, AccentTerracotta)
)

// ============================================================================
// Semantic M3 Mappings & Aliases for Full Compatibility
// ============================================================================

val DeepImperialNavy = SovereignNavy
val OnImperialNavy = OnSovereignNavy
val PrimaryNavyLight = SovereignNavy
val PrimaryNavyContainer = SoftNavyContainer
val OnPrimaryNavyContainer = OnNavyContainer

val WarmTerracotta = AccentTerracotta
val OnWarmTerracotta = OnAccentTerracotta
val TerracottaContainerLight = TerracottaBadgeContainer
val OnTerracottaContainerDark = OnTerracottaText

val WarmParchmentBase = WarmCanvasBg
val OnParchmentText = TextPrimaryDark

val SecondaryTextColor = TextSecondaryDark
val OnSandstoneSurfaceVariant = TextSecondaryDark
val MutedTextSecondary = TextSecondaryDark

val SandstoneSurface = SandstoneCard
val WarmSurfaceIvory = SandstoneCard
val WarmIvorySurface = WarmCanvasBg
val PaleSandstoneVariant = SandstoneCard
val PrimaryContainerSlate = SoftNavyContainer
val OnPrimarySlateContainer = OnNavyContainer

val SageHerbSuccessContainer = VerifiedSageGreen
val OnSageHerbSuccess = TextOnSageGreen

val MutedAmberWarningContainer = WarningAmber
val OnMutedAmberWarning = TextOnWarningAmber

val DeepHennaAlertContainer = AlertCrimson
val OnDeepHennaAlert = TextOnAlertCrimson

val ParchmentOutline = CardBorderStroke
val ParchmentOutlineVariant = CardBorderStrokeLight

// Dark Theme Variants
val DarkNavyBackground = Color(0xFF0A1322)
val DarkSandstoneSurface = Color(0xFF141F33)
val DarkSurfaceVariant = Color(0xFF1C2B44)
val DarkOnSurface = Color(0xFFFAF7F2)
val DarkPrimaryNavy = Color(0xFF8FAEE0)
val DarkOnPrimary = Color(0xFF0F1E36)
val DarkSecondaryTerracotta = Color(0xFFE88A68)
val DarkOnSecondary = Color(0xFF3B1000)
val DarkSageHerbContainer = Color(0xFF1B3D23)
val DarkOnSageHerb = Color(0xFFD1E7D0)
val DarkAmberWarningContainer = Color(0xFF4A2B00)
val DarkOnAmberWarning = Color(0xFFFDE3B8)
val DarkHennaAlertContainer = Color(0xFF4D100A)
val DarkOnHennaAlert = Color(0xFFFCDAD4)

// Backwards-compatibility aliases
val DeepIndigoSlatePrimary = SovereignNavy
val PrimarySlateLight = SovereignNavy
val TerracottaAccentSecondary = AccentTerracotta
val TerracottaAccentLight = AccentTerracotta
val TerracottaContainer = TerracottaBadgeContainer
val OnTerracottaContainer = OnTerracottaText
val SageGreenSuccessContainer = VerifiedSageGreen
val SageGreenSuccessText = TextOnSageGreen
val SaffronAmberWarningContainer = WarningAmber
val SaffronAmberWarningText = TextOnWarningAmber
val LegalAmberSecondary = WarningAmber
val LegalAmberLight = WarningAmber
val HennaRedAlertContainer = AlertCrimson
val HennaRedAlertText = TextOnAlertCrimson
val CharcoalTextPrimary = TextPrimaryDark
val WarmOutline = CardBorderStroke
val DarkIndigoBackground = DarkNavyBackground
val DarkPrimarySlate = DarkPrimaryNavy
val DarkTerracotta = DarkSecondaryTerracotta




