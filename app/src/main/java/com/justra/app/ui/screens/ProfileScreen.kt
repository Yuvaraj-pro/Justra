package com.justra.app.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.domain.model.UserRole
import com.justra.app.ui.components.AppBottomNavBar
import com.justra.app.ui.theme.*
import com.justra.app.ui.viewmodel.NyayaMateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: NyayaMateViewModel,
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRoute: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL
    val securityManager = viewModel.securityManager

    // User Profile State
    var userDisplayName by remember { mutableStateOf(securityManager.getUserDisplayName()) }
    var userPhone by remember { mutableStateOf(securityManager.getUserPhoneNumber()) }
    var selectedDistrict by remember { mutableStateOf(securityManager.getSelectedDistrict()) }
    var advocateEnrollId by remember { mutableStateOf(securityManager.getAdvocateEnrollmentId()) }
    val currentRole by viewModel.userRole.collectAsState()
    val activeCases by viewModel.activeCases.collectAsState()

    var isEditing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isTa) "எனது சுயவிவரம்" else "My Profile",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = SovereignNavy
                        )
                    )
                },
                actions = {
                    // Settings Button in Profile Top Bar
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag("profile_settings_icon_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = SovereignNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmIvorySurface)
            )
        },
        bottomBar = {
            AppBottomNavBar(
                currentRoute = "profile",
                onNavigateTo = { route -> onNavigateToRoute(route) },
                currentLanguage = currentLanguage
            )
        },
        containerColor = WarmCanvasBg,
        modifier = modifier.testTag("profile_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
        ) {
            // 1. Hero Profile Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SovereignNavy),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(AccentTerracotta)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Avatar",
                                tint = Color.White,
                                modifier = Modifier.size(54.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = userDisplayName.ifBlank { if (isTa) "நீதி பயனர்" else "Justice Citizen" },
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = FontFamily.Serif
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = userPhone.ifBlank { if (isTa) "+91 நொய்யா பாதுகாப்பு எண்கள்" else "+91 Protected Number" },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = AccentTerracotta
                            ) {
                                Text(
                                    text = currentRole.name,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = PaleSandstoneVariant
                            ) {
                                Text(
                                    text = if (currentLanguage == LanguagePreference.TAMIL) "தமிழ் (IN)" else "English (IN)",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SovereignNavy
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Quick Settings Launcher Tile
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmIvorySurface),
                    border = BorderStroke(1.dp, SovereignNavy.copy(alpha = 0.15f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToSettings() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = DeepIndigoSlatePrimary.copy(alpha = 0.1f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "App Settings",
                                        tint = SovereignNavy
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = if (isTa) "பயன்பாட்டு அமைப்புகள் & பாதுகாப்பு" else "App Settings & Security",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                )
                                Text(
                                    text = if (isTa) "மொழி, பயோமெட்ரிக், தனியுரிமை & தரவு மீட்டமைப்பு" else "Language, Biometrics, Privacy & Data",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondaryDark
                                    )
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Navigate",
                            tint = SovereignNavy
                        )
                    }
                }
            }

            // 3. User Stats Summary Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, SovereignNavy.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${activeCases.size}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignNavy
                                )
                            )
                            Text(
                                text = if (isTa) "செயலில் உள்ள வழக்குகள்" else "Active Cases",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                            )
                        }

                        VerticalDivider(modifier = Modifier.height(40.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "100%",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AccentTerracotta
                                )
                            )
                            Text(
                                text = if (isTa) "சட்ட பாதுகாப்பு" else "Legal Vault",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                            )
                        }

                        VerticalDivider(modifier = Modifier.height(40.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isTa) "தமிழ்/Eng" else "En/Ta",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigoSlatePrimary
                                )
                            )
                            Text(
                                text = if (isTa) "மொழி" else "Language",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                            )
                        }
                    }
                }
            }

            // 4. Personal Info & Profile Edit Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmIvorySurface),
                    border = BorderStroke(1.dp, SovereignNavy.copy(alpha = 0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = SovereignNavy
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isTa) "தனிப்பட்ட விபரங்கள்" else "Personal Details",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                )
                            }

                            TextButton(onClick = {
                                if (isEditing) {
                                    securityManager.saveUserProfile(
                                        displayName = userDisplayName,
                                        phoneNumber = userPhone,
                                        district = selectedDistrict,
                                        advocateEnrollmentId = advocateEnrollId
                                    )
                                    Toast.makeText(
                                        context,
                                        if (isTa) "சுயவிவரம் சேமிக்கப்பட்டது!" else "Profile Saved Successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                isEditing = !isEditing
                            }) {
                                Text(
                                    text = if (isEditing) (if (isTa) "சேமி" else "Save") else (if (isTa) "திருத்து" else "Edit"),
                                    color = SovereignNavy,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isEditing) {
                            OutlinedTextField(
                                value = userDisplayName,
                                onValueChange = { userDisplayName = it },
                                label = { Text(if (isTa) "முழு பெயர்" else "Full Name") },
                                colors = nyayaOutlinedTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = userPhone,
                                onValueChange = { userPhone = it },
                                label = { Text(if (isTa) "தொலைபேசி எண்" else "Phone Number") },
                                colors = nyayaOutlinedTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = selectedDistrict,
                                onValueChange = { selectedDistrict = it },
                                label = { Text(if (isTa) "மாவட்டம் / மாநிலம்" else "District / Jurisdiction") },
                                colors = nyayaOutlinedTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (currentRole == UserRole.LEGAL_COUNSEL) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = advocateEnrollId,
                                    onValueChange = { advocateEnrollId = it },
                                    label = { Text(if (isTa) "வழக்கறிஞர் பதிவு எண்" else "Bar Council Enrollment ID") },
                                    colors = nyayaOutlinedTextFieldColors(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        } else {
                            ProfileDetailRow(
                                label = if (isTa) "பெயர்" else "Name",
                                value = userDisplayName.ifBlank { "Not set" }
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray.copy(alpha = 0.2f))
                            ProfileDetailRow(
                                label = if (isTa) "தொலைபேசி" else "Phone",
                                value = userPhone.ifBlank { "Not set" }
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray.copy(alpha = 0.2f))
                            ProfileDetailRow(
                                label = if (isTa) "மாவட்டம்" else "District",
                                value = selectedDistrict.ifBlank { "Tamil Nadu (Default)" }
                            )

                            if (currentRole == UserRole.LEGAL_COUNSEL && advocateEnrollId.isNotBlank()) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray.copy(alpha = 0.2f))
                                ProfileDetailRow(
                                    label = if (isTa) "வழக்கறிஞர் பதிவு எண்" else "Enrollment ID",
                                    value = advocateEnrollId
                                )
                            }
                        }
                    }
                }
            }

            // 5. Role Switcher Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmIvorySurface),
                    border = BorderStroke(1.dp, SovereignNavy.copy(alpha = 0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isTa) "பயனர் பங்கு தேர்வு" else "Active Legal Role Profile",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isTa) "குடிமகன் அல்லது வழக்கறிஞர் சுயவிவர நிலைக்கு மாறவும்" else "Switch role to customize AI analysis view and templates",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            UserRole.entries.forEach { role ->
                                val isSelected = currentRole == role
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setRole(role) },
                                    label = {
                                        Text(
                                            text = role.name,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SovereignNavy,
                                        selectedLabelColor = Color.White,
                                        containerColor = Color.White,
                                        labelColor = TextPrimaryDark
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // 6. Emergency Legal Contact Cards
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, HennaRedAlertText.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = HennaRedAlertText
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTa) "அவசர சட்ட உதவிகள் (India)" else "Emergency Legal Helplines (India)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = HennaRedAlertText
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            EmergencyBadge(title = "NALSA Legal Aid", number = "15100")
                            EmergencyBadge(title = "Cyber Fraud", number = "1930")
                            EmergencyBadge(title = "Women Helpline", number = "1091")
                        }
                    }
                }
            }

            // 7. Logout / Sign Out Tile
            item {
                OutlinedButton(
                    onClick = onNavigateToLogin,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = HennaRedAlertText),
                    border = BorderStroke(1.5.dp, HennaRedAlertText),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Logout")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTa) "வெளியேறு (Logout)" else "Sign Out / Lock Session",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondaryDark,
                fontWeight = FontWeight.Medium
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun EmergencyBadge(
    title: String,
    number: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = HennaRedAlertContainer.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, HennaRedAlertText.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = HennaRedAlertText
                )
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextPrimaryDark,
                    fontSize = 10.sp
                )
            )
        }
    }
}
