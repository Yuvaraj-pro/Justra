package com.example.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.LanguagePreference
import com.example.ui.navigation.Screen
import com.example.ui.theme.SovereignNavy

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars

@Composable
fun AppBottomNavBar(
    currentRoute: String,
    onNavigateTo: (String) -> Unit,
    currentLanguage: LanguagePreference = LanguagePreference.ENGLISH,
    modifier: Modifier = Modifier
) {
    val isTa = currentLanguage == LanguagePreference.TAMIL

    NavigationBar(
        windowInsets = WindowInsets.navigationBars,
        containerColor = SovereignNavy,
        contentColor = Color.White,
        tonalElevation = 8.dp,
        modifier = modifier
    ) {
        // Hub 1: Home
        NavigationBarItem(
            selected = currentRoute == "home" || currentRoute == Screen.Home.route,
            onClick = { onNavigateTo("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.size(22.dp)) },
            label = {
                Text(
                    text = if (isTa) "முகப்பு" else "Home",
                    fontSize = 11.sp,
                    fontWeight = if (currentRoute == "home") FontWeight.Bold else FontWeight.Medium
                )
            },
            alwaysShowLabel = true,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SovereignNavy,
                selectedTextColor = Color.White,
                indicatorColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.65f),
                unselectedTextColor = Color.White.copy(alpha = 0.65f)
            )
        )

        // Hub 2: Voice & AI Assistant Intake
        NavigationBarItem(
            selected = currentRoute == "voice_complaint" || currentRoute == Screen.VoiceComplaint.route || currentRoute == "chat_and_voice",
            onClick = { onNavigateTo(Screen.VoiceComplaint.route) },
            icon = { Icon(Icons.Default.Mic, contentDescription = "Voice & AI", modifier = Modifier.size(22.dp)) },
            label = {
                Text(
                    text = if (isTa) "குரல் & AI" else "Voice & AI",
                    fontSize = 11.sp,
                    fontWeight = if (currentRoute == "voice_complaint") FontWeight.Bold else FontWeight.Medium
                )
            },
            alwaysShowLabel = true,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SovereignNavy,
                selectedTextColor = Color.White,
                indicatorColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.65f),
                unselectedTextColor = Color.White.copy(alpha = 0.65f)
            )
        )

        // Hub 3: My Cases Hub
        NavigationBarItem(
            selected = currentRoute == "my_cases_hub" || currentRoute.startsWith("action_navigator") || currentRoute.startsWith("evidence_vault"),
            onClick = { onNavigateTo("my_cases_hub") },
            icon = { Icon(Icons.Default.Folder, contentDescription = "My Cases", modifier = Modifier.size(22.dp)) },
            label = {
                Text(
                    text = if (isTa) "வழக்குகள்" else "My Cases",
                    fontSize = 11.sp,
                    fontWeight = if (currentRoute == "my_cases_hub") FontWeight.Bold else FontWeight.Medium
                )
            },
            alwaysShowLabel = true,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SovereignNavy,
                selectedTextColor = Color.White,
                indicatorColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.65f),
                unselectedTextColor = Color.White.copy(alpha = 0.65f)
            )
        )

        // Hub 4: Statutory Tools & Knowledge Hub
        NavigationBarItem(
            selected = currentRoute == "statutory_tools_hub" || currentRoute == "court_fee_calculator" || currentRoute == "section_65b_certificate",
            onClick = { onNavigateTo("statutory_tools_hub") },
            icon = { Icon(Icons.Default.Gavel, contentDescription = "Law & Tools", modifier = Modifier.size(22.dp)) },
            label = {
                Text(
                    text = if (isTa) "சட்டம் & கருவிகள்" else "Law & Tools",
                    fontSize = 11.sp,
                    fontWeight = if (currentRoute == "statutory_tools_hub") FontWeight.Bold else FontWeight.Medium
                )
            },
            alwaysShowLabel = true,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SovereignNavy,
                selectedTextColor = Color.White,
                indicatorColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.65f),
                unselectedTextColor = Color.White.copy(alpha = 0.65f)
            )
        )

        // Hub 5: Profile & Settings Hub
        NavigationBarItem(
            selected = currentRoute == "account_settings_hub" || currentRoute == "profile",
            onClick = { onNavigateTo("account_settings_hub") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile", modifier = Modifier.size(22.dp)) },
            label = {
                Text(
                    text = if (isTa) "சுயவிவரம்" else "Profile",
                    fontSize = 11.sp,
                    fontWeight = if (currentRoute == "account_settings_hub") FontWeight.Bold else FontWeight.Medium
                )
            },
            alwaysShowLabel = true,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SovereignNavy,
                selectedTextColor = Color.White,
                indicatorColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.65f),
                unselectedTextColor = Color.White.copy(alpha = 0.65f)
            )
        )
    }
}
