package com.justra.app.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.theme.SovereignNavy

sealed class BottomNavDestination(val route: String, val labelEn: String, val labelTa: String, val icon: ImageVector) {
    data object Home : BottomNavDestination("home", "Home", "முகப்பு", Icons.Default.Home)
    data object AiChat : BottomNavDestination("chat", "AI Chat", "AI சாட்", Icons.AutoMirrored.Filled.Chat)
    data object ScamGuard : BottomNavDestination("scam_checker", "Scam Guard", "மோசடி பாதுகாப்பு", Icons.Default.Shield)
    data object Vault : BottomNavDestination("evidence_vault", "Vault", "சான்றுகள்", Icons.Default.FolderShared)
}

@Composable
fun JustraBottomBar(
    currentRoute: String?,
    currentLanguage: LanguagePreference,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavDestination.Home,
        BottomNavDestination.AiChat,
        BottomNavDestination.ScamGuard,
        BottomNavDestination.Vault
    )
    val isTa = currentLanguage == LanguagePreference.TAMIL

    NavigationBar(
        windowInsets = WindowInsets.navigationBars,
        containerColor = Color(0xFFFAF7F2),
        contentColor = SovereignNavy,
        modifier = modifier
    ) {
        items.forEach { item ->
            val isSelected = currentRoute?.startsWith(item.route) == true ||
                    (item.route == "home" && currentRoute == "home") ||
                    (item.route == "evidence_vault" && currentRoute?.contains("evidence_vault") == true)
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = if (isTa) item.labelTa else item.labelEn) },
                label = { Text(if (isTa) item.labelTa else item.labelEn) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = SovereignNavy,
                    indicatorColor = SovereignNavy,
                    unselectedIconColor = SovereignNavy.copy(alpha = 0.6f),
                    unselectedTextColor = SovereignNavy.copy(alpha = 0.6f)
                )
            )
        }
    }
}
