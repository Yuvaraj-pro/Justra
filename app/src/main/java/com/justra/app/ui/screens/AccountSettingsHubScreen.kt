package com.justra.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.viewmodel.NyayaMateViewModel

@Composable
fun AccountSettingsHubScreen(
    viewModel: NyayaMateViewModel,
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: (() -> Unit)? = null,
    onNavigateToRoute: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    SettingsScreen(
        viewModel = viewModel,
        currentLanguage = currentLanguage,
        onToggleLanguage = onToggleLanguage,
        onBackClick = onNavigateBack,
        modifier = modifier
    )
}
