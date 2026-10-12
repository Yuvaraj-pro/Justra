package com.justra.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
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
    onNavigateToLogin: (() -> Unit)? = null,
    onNavigateToRoute: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL
    val securityManager = viewModel.securityManager

    // User details state
    var fullName by remember { mutableStateOf(securityManager.getUserDisplayName().ifBlank { if (isTa) "நீதி பயனர்" else "Justice Citizen" }) }
    var contactPhone by remember { mutableStateOf(securityManager.getUserPhoneNumber().ifBlank { "+91 98765 43210" }) }
    var primaryJurisdiction by remember { mutableStateOf(securityManager.getSelectedDistrict().ifBlank { "Madras High Court / District Court" }) }

    val activeRole by viewModel.userRole.collectAsState()
    var selectedPersona by remember { mutableStateOf(activeRole) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isTa) "சுயவிவரம்" else "User Profile",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = SovereignNavy
                        )
                    )
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag("profile_settings_gear_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = SovereignNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmCanvasBg)
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
            // Header Avatar Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SandstoneCard),
                    border = BorderStroke(1.dp, CardBorderStroke),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(SovereignNavy)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Avatar",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = fullName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignNavy
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = selectedPersona.badgeEn + " • " + primaryJurisdiction,
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark)
                            )
                        }
                    }
                }
            }

            // Section 1: User Profile & Judicial Jurisdiction
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SandstoneCard),
                    border = BorderStroke(1.dp, CardBorderStroke),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isTa) "பயனர் விவரங்கள் & நீதித்துறை அதிகார வரம்பு" else "User Profile & Judicial Jurisdiction",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SovereignNavy,
                                fontFamily = FontFamily.Serif
                            )
                        )

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text(if (isTa) "முழு பெயர்" else "Full Name") },
                            colors = justraOutlinedTextFieldColors(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_full_name_input")
                        )

                        OutlinedTextField(
                            value = contactPhone,
                            onValueChange = { contactPhone = it },
                            label = { Text(if (isTa) "தொடர்பு தொலைபேசி எண்" else "Contact Phone Number") },
                            colors = justraOutlinedTextFieldColors(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_phone_input")
                        )

                        OutlinedTextField(
                            value = primaryJurisdiction,
                            onValueChange = { primaryJurisdiction = it },
                            label = { Text(if (isTa) "முதன்மை அதிகார வரம்பு / நீதிமன்றம்" else "Primary Jurisdiction (e.g. District Court, Madras High Court)") },
                            colors = justraOutlinedTextFieldColors(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_jurisdiction_input")
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Legal Persona Selector
                        Text(
                            text = if (isTa) "சட்ட ஆளுமை வகை (Legal Persona)" else "Legal Persona Selector",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = SovereignNavy
                            )
                        )

                        val personas = listOf(
                            UserRole.CITIZEN to if (isTa) "குடிமக்கள் (Citizen)" else "Citizen Complainant",
                            UserRole.LEGAL_COUNSEL to if (isTa) "வழக்கறிஞர் (Counsel)" else "Legal Counsel",
                            UserRole.MSME_BUSINESS to if (isTa) "வணிக உரிமையாளர் (MSME)" else "MSME Business Owner",
                            UserRole.CYBER_FRAUD_VICTIM to if (isTa) "சைபர் தற்காப்பு (Cyber)" else "Cyber Defense"
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            personas.forEach { (role, label) ->
                                val isSelected = selectedPersona == role
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedPersona = role },
                                    label = {
                                        Text(
                                            text = label,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else SovereignNavy
                                        )
                                    },
                                    leadingIcon = if (isSelected) {
                                        { Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp)) }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SovereignNavy,
                                        selectedLabelColor = Color.White,
                                        containerColor = Color.White,
                                        labelColor = SovereignNavy
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = CardBorderStroke,
                                        selectedBorderColor = SovereignNavy
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Save Button
                        Button(
                            onClick = {
                                securityManager.saveUserProfile(
                                    displayName = fullName,
                                    phoneNumber = contactPhone,
                                    district = primaryJurisdiction,
                                    advocateEnrollmentId = securityManager.getAdvocateEnrollmentId()
                                )
                                viewModel.setUserRole(selectedPersona)
                                Toast.makeText(
                                    context,
                                    if (isTa) "சுயவிவரம் புதுப்பிக்கப்பட்டது!" else "Profile updated successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SovereignNavy,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("profile_save_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTa) "சுயவிவரத்தை சேமி" else "Save & Update Profile",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Section 2: Language & Accessibility Preferences
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SandstoneCard),
                    border = BorderStroke(1.dp, CardBorderStroke),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = SovereignNavy,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTa) "மொழி & அணுகல்தன்மை முன்னுரிமைகள்" else "Language & Accessibility Preferences",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignNavy,
                                    fontFamily = FontFamily.Serif
                                )
                            )
                        }

                        Text(
                            text = if (isTa) "பயன்பாட்டின் முதன்மை மொழியைத் தேர்ந்தெடுக்கவும்:" else "Select app-wide primary language:",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondaryDark)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // English Selection Button
                            val isEng = currentLanguage == LanguagePreference.ENGLISH
                            OutlinedButton(
                                onClick = {
                                    if (!isEng) {
                                        viewModel.setLanguage(LanguagePreference.ENGLISH)
                                    }
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isEng) SovereignNavy else Color.White,
                                    contentColor = if (isEng) Color.White else SovereignNavy
                                ),
                                border = BorderStroke(1.dp, SovereignNavy),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("lang_toggle_english")
                            ) {
                                Text(
                                    text = "English",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEng) Color.White else SovereignNavy
                                )
                            }

                            // Tamil Selection Button
                            val isTamilSel = currentLanguage == LanguagePreference.TAMIL
                            OutlinedButton(
                                onClick = {
                                    if (!isTamilSel) {
                                        viewModel.setLanguage(LanguagePreference.TAMIL)
                                    }
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isTamilSel) SovereignNavy else Color.White,
                                    contentColor = if (isTamilSel) Color.White else SovereignNavy
                                ),
                                border = BorderStroke(1.dp, SovereignNavy),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("lang_toggle_tamil")
                            ) {
                                Text(
                                    text = "தமிழ்",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isTamilSel) Color.White else SovereignNavy
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
