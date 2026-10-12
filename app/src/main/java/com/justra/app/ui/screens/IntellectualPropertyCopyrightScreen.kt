package com.justra.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.components.NyayaTopBar
import com.justra.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntellectualPropertyCopyrightScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTa = currentLanguage == LanguagePreference.TAMIL

    Scaffold(
        topBar = {
            NyayaTopBar(
                title = if (isTa) "முத்திரை காப்புரிமை IPR அறிவிப்பு வழிகாட்டி" else "IPR Trademark Cease & Desist Builder",
                subtitle = if (isTa) "வணிக முத்திரைச் சட்டம் 1999 & பதிப்புரிமைச் சட்டம் 1957" else "Trade Marks Act 1999 & Copyright Act 1957 Notice Generator",
                currentLanguage = currentLanguage,
                onToggleLanguage = onToggleLanguage,
                onBackClick = onBackClick
            )
        },
        containerColor = WarmCanvasBg,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SandstoneCard),
                border = BorderStroke(1.dp, CardBorderStroke),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isTa) "©️ வர்த்தக முத்திரை & பதிப்புரிமை மீறல் தடுப்பு" else "©️ Cease & Desist Infringement Demand Notice",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SovereignNavy, fontFamily = FontFamily.Serif)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isTa) "போலி பிராண்ட் பயன்பாடு மற்றும் காப்பி அடித்தலை தடுக்க 7 நாள் இறுதிக்கெடு அறிவிப்பு அனுப்பலாம்." else "Issue 7-day statutory warning against brand impersonation, deceptive similarity, and stolen digital assets.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                    )
                }
            }
        }
    }
}
