package com.justra.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.theme.DeepIndigoSlatePrimary
import com.justra.app.ui.theme.PaleSandstoneVariant
import com.justra.app.ui.theme.SageGreenSuccessContainer
import com.justra.app.ui.theme.SageGreenSuccessText
import com.justra.app.ui.theme.TerracottaAccentSecondary
import com.justra.app.ui.theme.WarmIvorySurface
import com.justra.app.util.BilingualStrings

@Composable
fun LanguageConsentScreen(
    currentLanguage: LanguagePreference,
    onLanguageSelected: (LanguagePreference) -> Unit,
    onConsentAccepted: () -> Unit,
    modifier: Modifier = Modifier
) {
    var hasAgreedToTerms by remember { mutableStateOf(false) }
    var selectedLang by remember { mutableStateOf(currentLanguage) }

    Scaffold(
        containerColor = WarmIvorySurface,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // Sovereign Emblem Finial
            Surface(
                shape = CircleShape,
                color = PaleSandstoneVariant,
                border = androidx.compose.foundation.BorderStroke(2.dp, TerracottaAccentSecondary),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = "Justra Finial",
                        tint = DeepIndigoSlatePrimary,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Justra (ஜஸ்ட்ரா)",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigoSlatePrimary,
                    textAlign = TextAlign.Center
                )
            )

            Text(
                text = BilingualStrings.t("welcome_desc", selectedLang),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF4A4E57),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Step 1: Language Selection Cards
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PaleSandstoneVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = DeepIndigoSlatePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = BilingualStrings.t("select_language", selectedLang),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigoSlatePrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val languages = listOf(
                        LanguagePreference.ENGLISH to Pair("English", "India & Global"),
                        LanguagePreference.TAMIL to Pair("தமிழ்", "தமிழ்நாடு & இந்தியா"),
                        LanguagePreference.HINDI to Pair("हिन्दी", "भारत & उत्तर/மத்திய"),
                        LanguagePreference.TELUGU to Pair("తెలుగు", "ஆந்திரா & தெலுங்கானா"),
                        LanguagePreference.MALAYALAM to Pair("മലയാളം", "கேரளா & இந்தியா"),
                        LanguagePreference.KANNADA to Pair("கன்னட / ಕನ್ನಡ", "கர்நாடகா & இந்தியா")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        languages.chunked(2).forEach { rowLangs ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                rowLangs.forEach { (lang, details) ->
                                    LanguageChoiceCard(
                                        title = details.first,
                                        subtitle = details.second,
                                        isSelected = selectedLang == lang,
                                        onClick = {
                                            selectedLang = lang
                                            onLanguageSelected(lang)
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("select_${lang.name.lowercase()}_button")
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step 2: Statutory Boundary Notice
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PaleSandstoneVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Policy,
                            contentDescription = null,
                            tint = TerracottaAccentSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = BilingualStrings.t("terms_headline", selectedLang),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigoSlatePrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = BilingualStrings.t("terms_statutory_text", selectedLang),
                        style = MaterialTheme.typography.bodySmall.copy(
                            lineHeight = 18.sp,
                            color = Color(0xFF4A4E57)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { hasAgreedToTerms = !hasAgreedToTerms }
                            .padding(vertical = 4.dp)
                            .testTag("consent_checkbox_row")
                    ) {
                        Checkbox(
                            checked = hasAgreedToTerms,
                            onCheckedChange = { hasAgreedToTerms = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = TerracottaAccentSecondary,
                                checkmarkColor = Color.White
                            ),
                            modifier = Modifier.testTag("consent_checkbox")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedLang == LanguagePreference.TAMIL)
                                "சட்ட மறுப்பு அறிவிப்பைப் படித்து ஒப்புக்கொள்கிறேன்."
                            else
                                "I acknowledge the statutory disclaimer & data privacy terms.",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DeepIndigoSlatePrimary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Button
            Button(
                onClick = onConsentAccepted,
                enabled = hasAgreedToTerms,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepIndigoSlatePrimary,
                    contentColor = Color.White,
                    disabledContainerColor = DeepIndigoSlatePrimary.copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("accept_terms_continue_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = BilingualStrings.t("accept_and_proceed", selectedLang),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    if (hasAgreedToTerms) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LanguageChoiceCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) TerracottaAccentSecondary else Color.Transparent
    val containerColor = if (isSelected) Color.White else WarmIvorySurface

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) TerracottaAccentSecondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigoSlatePrimary
                    )
                )
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = TerracottaAccentSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF4A4E57)
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
