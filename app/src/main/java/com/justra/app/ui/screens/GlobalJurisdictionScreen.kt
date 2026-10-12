package com.justra.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.domain.model.CrossBorderReliefItem
import com.justra.app.domain.model.GlobalJurisdictionRepository
import com.justra.app.domain.model.JurisdictionNation
import com.justra.app.domain.model.JurisdictionPhase
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.domain.model.LegalSystemType
import com.justra.app.domain.model.UNOfficialLanguage
import com.justra.app.ui.theme.DeepIndigoSlatePrimary
import com.justra.app.ui.theme.HennaRedAlertContainer
import com.justra.app.ui.theme.HennaRedAlertText
import com.justra.app.ui.theme.PaleSandstoneVariant
import com.justra.app.ui.theme.PrimaryContainerSlate
import com.justra.app.ui.theme.SageGreenSuccessContainer
import com.justra.app.ui.theme.SageGreenSuccessText
import com.justra.app.ui.theme.SaffronAmberWarningContainer
import com.justra.app.ui.theme.SaffronAmberWarningText
import com.justra.app.ui.theme.TerracottaAccentSecondary
import com.justra.app.ui.theme.WarmIvorySurface
import com.justra.app.ui.theme.nyayaOutlinedTextFieldColors
import com.justra.app.util.BilingualStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalJurisdictionScreen(
    currentLanguage: LanguagePreference,
    activeNation: JurisdictionNation,
    onSelectNation: (JurisdictionNation) -> Unit,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateToWorldWideLaw: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var expandedNationCode by remember { mutableStateOf<String?>(activeNation.countryCode) }
    var showComparisonDialog by remember { mutableStateOf(false) }

    val tabTitles = listOf(
        BilingualStrings.t("tab_all_nations", currentLanguage),
        BilingualStrings.t("tab_phase_1", currentLanguage),
        BilingualStrings.t("tab_phase_2", currentLanguage),
        BilingualStrings.t("tab_phase_3", currentLanguage),
        BilingualStrings.t("tab_un_p5", currentLanguage),
        BilingualStrings.t("tab_un_languages", currentLanguage)
    )

    val filteredNations = remember(selectedTabIndex, searchQuery) {
        val baseList = when (selectedTabIndex) {
            0 -> GlobalJurisdictionRepository.ALL_NATIONS
            1 -> GlobalJurisdictionRepository.getNationsByPhase(JurisdictionPhase.PHASE_1)
            2 -> GlobalJurisdictionRepository.getNationsByPhase(JurisdictionPhase.PHASE_2)
            3 -> GlobalJurisdictionRepository.getNationsByPhase(JurisdictionPhase.PHASE_3)
            4 -> GlobalJurisdictionRepository.ALL_NATIONS.filter { it.isP5PermanentMember || it.phase == JurisdictionPhase.UN_P5_STRATEGIC || it.countryCode in listOf("DE", "JP", "IN") }
            else -> GlobalJurisdictionRepository.ALL_NATIONS
        }

        if (searchQuery.isBlank()) {
            baseList
        } else {
            val q = searchQuery.trim().lowercase()
            baseList.filter { nation ->
                nation.nameEn.lowercase().contains(q) ||
                        nation.nameTa.lowercase().contains(q) ||
                        nation.nativeName.lowercase().contains(q) ||
                        nation.countryCode.lowercase().contains(q) ||
                        nation.legalSystem.titleEn.lowercase().contains(q) ||
                        nation.primaryLegalLanguagesEn.any { it.lowercase().contains(q) }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = BilingualStrings.t("global_hub_title", currentLanguage),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigoSlatePrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = BilingualStrings.t("global_hub_subtitle", currentLanguage),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.secondary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("global_jurisdiction_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DeepIndigoSlatePrimary
                        )
                    }
                },
                actions = {
                    // Quick Language Toggle
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PaleSandstoneVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onToggleLanguage() }
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                            .testTag("global_lang_toggle_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = DeepIndigoSlatePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isTa) "தமிழ்" else "English",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DeepIndigoSlatePrimary
                                )
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmIvorySurface)
            )
        },
        containerColor = WarmIvorySurface,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Current Active Jurisdiction Hero Banner
            item {
                ActiveJurisdictionHeroBanner(
                    activeNation = activeNation,
                    isTa = isTa,
                    onCompareClick = { showComparisonDialog = true }
                )
            }

            // World Wide Law Open Data Banner
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = PrimaryContainerSlate),
                    border = BorderStroke(1.dp, DeepIndigoSlatePrimary.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToWorldWideLaw?.invoke() }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "🌐", fontSize = 24.sp)
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = if (isTa) "World Wide Law - 110+ நாடுகள்" else "World Wide Law Repository",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                                    )
                                    Surface(shape = RoundedCornerShape(4.dp), color = SageGreenSuccessContainer) {
                                        Text(
                                            text = "GLOBAL REPOSITORY",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = SageGreenSuccessText,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (isTa) "1.6 கோடி திறந்தநிலை ஆவணங்கள் & சர்வதேச சட்டங்கள்" else "16M+ open legal docs across 110+ jurisdictions",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open World Wide Law",
                            tint = DeepIndigoSlatePrimary
                        )
                    }
                }
            }

            // 2. Search Field
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (isTa) "நாடு, மொழி அல்லது சட்ட வகை மூலம் தேடவும்..." else "Search by nation, language, or legal system...",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = DeepIndigoSlatePrimary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Check, contentDescription = "Clear", tint = TerracottaAccentSecondary)
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = nyayaOutlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("jurisdiction_search_input")
                )
            }

            // 3. Category Tabs
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    tabTitles = tabTitles,
                    onTabSelected = { selectedTabIndex = it }
                )
            }

            // 4. Tab 5: UN 6 Official Languages view vs Nations list
            if (selectedTabIndex == 5) {
                item {
                    UNLanguagesHeaderCard(isTa = isTa)
                }

                items(GlobalJurisdictionRepository.UN_6_OFFICIAL_LANGUAGES) { unLang ->
                    UNLanguageCardItem(unLang = unLang, isTa = isTa)
                }

                item {
                    ComparativeFrameworkMatrixCard(isTa = isTa)
                }
            } else {
                // Section Header
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = when (selectedTabIndex) {
                                1 -> if (isTa) "Phase 1: பொது சட்டம் & தமிழ் டயஸ்போரா நாடுகள் (${filteredNations.size})" else "Phase 1: Common Law & Tamil Diaspora (${filteredNations.size})"
                                2 -> if (isTa) "Phase 2: ஐரோப்பிய சிவில் சட்ட நாடுகள் (${filteredNations.size})" else "Phase 2: European Civil Law Expansion (${filteredNations.size})"
                                3 -> if (isTa) "Phase 3: மத்திய கிழக்கு & வளைகுடா மையங்கள் (${filteredNations.size})" else "Phase 3: Middle East & Gulf Hub (${filteredNations.size})"
                                4 -> if (isTa) "ஐ.நா. P5 நிரந்தர உறுப்பு நாடுகள் & வல்லரசுகள் (${filteredNations.size})" else "UN Security Council P5 & Strategic Powers (${filteredNations.size})"
                                else -> if (isTa) "அனைத்து சர்வதேச சட்ட எல்லைகள் (${filteredNations.size})" else "Global Legal Jurisdictions (${filteredNations.size})"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigoSlatePrimary
                            )
                        )

                        if (selectedTabIndex == 1) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SageGreenSuccessContainer
                            ) {
                                Text(
                                    text = if (isTa) "ஆங்கிலம் + தமிழ் தயார்நிலை" else "English + Tamil Active",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SageGreenSuccessText
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Nations List
                items(filteredNations, key = { it.countryCode }) { nation ->
                    val isExpanded = expandedNationCode == nation.countryCode
                    val isActive = activeNation.countryCode == nation.countryCode

                    NationDetailCard(
                        nation = nation,
                        isActive = isActive,
                        isExpanded = isExpanded,
                        isTa = isTa,
                        onToggleExpand = {
                            expandedNationCode = if (isExpanded) null else nation.countryCode
                        },
                        onSetActive = {
                            onSelectNation(nation)
                            Toast.makeText(
                                context,
                                "${BilingualStrings.t("jurisdiction_switched_success", currentLanguage)} ${nation.flagEmoji} ${if (isTa) nation.nameTa else nation.nameEn}",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onDialEmergency = { number ->
                            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                            dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            try {
                                context.startActivity(dialIntent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Dialer unavailable: $number", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onOpenPortal = { url ->
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            try {
                                context.startActivity(browserIntent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Cannot open portal: $url", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    UNPeaceTreatyNoticeCard(isTa = isTa)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun ScrollableTabRow(
    selectedTabIndex: Int,
    tabTitles: List<String>,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabTitles.forEachIndexed { index, title ->
            val isSelected = selectedTabIndex == index
            FilterChip(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                label = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = DeepIndigoSlatePrimary,
                    selectedLabelColor = Color.White,
                    containerColor = PaleSandstoneVariant,
                    labelColor = DeepIndigoSlatePrimary
                ),
                border = BorderStroke(
                    1.dp,
                    if (isSelected) DeepIndigoSlatePrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("tab_chip_$index")
            )
        }
    }
}

@Composable
private fun ActiveJurisdictionHeroBanner(
    activeNation: JurisdictionNation,
    isTa: Boolean,
    onCompareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryContainerSlate),
        border = BorderStroke(1.5.dp, DeepIndigoSlatePrimary),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = activeNation.flagEmoji,
                        fontSize = 32.sp
                    )
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (isTa) activeNation.nameTa else activeNation.nameEn,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigoSlatePrimary
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SageGreenSuccessContainer
                            ) {
                                Text(
                                    text = if (isTa) "தற்போதைய சட்ட எல்லை" else "Active Jurisdiction",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SageGreenSuccessText
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isTa) activeNation.unStatusBadgeTa else activeNation.unStatusBadgeEn,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TerracottaAccentSecondary
                            )
                        )
                    }
                }

                if (activeNation.isP5PermanentMember) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF4A148C).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFF4A148C)),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "P5",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4A148C)
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DeepIndigoSlatePrimary.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isTa) "சட்டக் கட்டமைப்பு:" else "Legal Framework:",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.secondary)
                    )
                    Text(
                        text = if (isTa) activeNation.legalSystem.titleTa else activeNation.legalSystem.titleEn,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigoSlatePrimary
                        )
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isTa) "உயர்நீதிமன்றம்:" else "Apex Judicial Body:",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.secondary)
                    )
                    Text(
                        text = if (isTa) activeNation.apexCourtTa else activeNation.apexCourtEn,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigoSlatePrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NationDetailCard(
    nation: JurisdictionNation,
    isActive: Boolean,
    isExpanded: Boolean,
    isTa: Boolean,
    onToggleExpand: () -> Unit,
    onSetActive: () -> Unit,
    onDialEmergency: (String) -> Unit,
    onOpenPortal: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBorderColor = when {
        isActive -> DeepIndigoSlatePrimary
        nation.isP5PermanentMember -> Color(0xFF4A148C).copy(alpha = 0.6f)
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    val cardBg = if (isActive) Color.White else PaleSandstoneVariant

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(if (isActive) 2.dp else 1.dp, cardBorderColor),
        modifier = modifier
            .fillMaxWidth()
            .testTag("nation_card_${nation.countryCode.lowercase()}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = nation.flagEmoji, fontSize = 28.sp)
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (isTa) nation.nameTa else nation.nameEn,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigoSlatePrimary
                                )
                            )
                            if (nation.isP5PermanentMember) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF4A148C)
                                ) {
                                    Text(
                                        text = "UN P5",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = nation.nativeName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (isActive) {
                        Surface(
                            shape = CircleShape,
                            color = SageGreenSuccessContainer,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Active",
                                    tint = SageGreenSuccessText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    IconButton(onClick = onToggleExpand) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = DeepIndigoSlatePrimary
                        )
                    }
                }
            }

            // Quick Badges Row (Languages + Legal System)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (nation.phase) {
                        JurisdictionPhase.PHASE_1 -> Color(0xFFE8F5E9)
                        JurisdictionPhase.PHASE_2 -> Color(0xFFE3F2FD)
                        JurisdictionPhase.PHASE_3 -> Color(0xFFFFF3E0)
                        JurisdictionPhase.UN_P5_STRATEGIC -> Color(0xFFF3E5F5)
                    }
                ) {
                    Text(
                        text = if (isTa) nation.phase.titleTa.substringBefore(" -").substringBefore(":") else nation.phase.titleEn.substringBefore(":"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(nation.phase.badgeColor)
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PrimaryContainerSlate
                ) {
                    Text(
                        text = if (isTa) nation.legalSystem.titleTa.substringBefore(" (") else nation.legalSystem.titleEn.substringBefore(" /"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = DeepIndigoSlatePrimary
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                (if (isTa) nation.primaryLegalLanguagesTa else nation.primaryLegalLanguagesEn).take(2).forEach { lang ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.White,
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = lang,
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Expandable Detailed Section
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // 1. Apex Court & System Description
                    Text(
                        text = if (isTa) "🏛️ உச்ச நீதிமன்றம் & சட்ட முறைமை:" else "🏛️ Apex Court & Jurisprudence:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigoSlatePrimary
                        )
                    )
                    Text(
                        text = if (isTa) "${nation.apexCourtTa} • ${nation.legalSystem.descriptionTa}" else "${nation.apexCourtEn} • ${nation.legalSystem.descriptionEn}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        ),
                        modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                    )

                    // 2. Major Acts & Statutory Codes
                    Text(
                        text = if (isTa) "📜 முதன்மை சட்டங்கள் (Major Statutes):" else "📜 Major Legal Statutes & Codes:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigoSlatePrimary
                        )
                    )
                    (if (isTa) nation.majorActsTa else nation.majorActsEn).forEach { act ->
                        Text(
                            text = "• $act",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(start = 6.dp, top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Electronic Evidence Standard
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PrimaryContainerSlate,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Fingerprint, contentDescription = null, tint = DeepIndigoSlatePrimary, modifier = Modifier.size(16.dp))
                                Text(
                                    text = if (isTa) "டிஜிட்டல் சான்றுகள் சட்ட நெறிமுறை:" else "Electronic Evidence Standard:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepIndigoSlatePrimary
                                    )
                                )
                            }
                            Text(
                                text = if (isTa) nation.electronicEvidenceStandardTa else nation.electronicEvidenceStandardEn,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DeepIndigoSlatePrimary,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                ),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4. Tamil Diaspora & Consular Support
                    Text(
                        text = if (isTa) "🤝 தமிழ் டயஸ்போரா & தூதரக ஆதரவு:" else "🤝 Tamil Diaspora & Consular Support:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TerracottaAccentSecondary
                        )
                    )
                    Text(
                        text = if (isTa) nation.tamilDiasporaSummaryTa else nation.tamilDiasporaSummaryEn,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        ),
                        modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                    )

                    // 5. Cross-Border Relief Portals
                    if (nation.crossBorderRelief.isNotEmpty()) {
                        Text(
                            text = if (isTa) "🌐 சர்வதேச எல்லை கடந்த நிவாரண வழிகள்:" else "🌐 Cross-Border Relief Channels:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigoSlatePrimary
                            )
                        )
                        nation.crossBorderRelief.forEach { relief ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White,
                                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = if (isTa) relief.titleTa else relief.titleEn,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = DeepIndigoSlatePrimary
                                            )
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = PaleSandstoneVariant
                                        ) {
                                            Text(
                                                text = relief.authorityOrPortal,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    color = DeepIndigoSlatePrimary
                                                ),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (isTa) relief.detailsTa else relief.detailsEn,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 6. Direct Dial Buttons & e-Filing Portal
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { onDialEmergency(nation.emergencyPoliceNumber) },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, HennaRedAlertText),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = HennaRedAlertText),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("dial_police_${nation.countryCode}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "Police: ${nation.emergencyPoliceNumber}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { onDialEmergency(nation.cyberHelplineNumber.substringBefore(" /").substringBefore(" (")) },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, DeepIndigoSlatePrimary),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepIndigoSlatePrimary),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("dial_cyber_${nation.countryCode}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "Cyber: ${nation.cyberHelplineNumber.take(12)}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Open Portal Button
                    OutlinedButton(
                        onClick = { onOpenPortal(nation.eFilingPortalUrl) },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, TerracottaAccentSecondary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TerracottaAccentSecondary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_portal_${nation.countryCode}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(
                                text = "${nation.eFilingPortalName} (${if (isTa) "இணைய தளம்" else "Official Portal"})",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Set as Active Jurisdiction CTA
                    if (!isActive) {
                        Button(
                            onClick = onSetActive,
                            colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("set_active_jurisdiction_${nation.countryCode}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Text(
                                    text = if (isTa) "இவ்வெல்லைக்கு மாறுக (${nation.nameTa})" else "Set ${nation.nameEn} as Active Jurisdiction",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UNLanguagesHeaderCard(isTa: Boolean) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryContainerSlate),
        border = BorderStroke(1.dp, DeepIndigoSlatePrimary.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = CircleShape,
                    color = DeepIndigoSlatePrimary,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = if (isTa) "ஐக்கிய நாடுகள் சபையின் 6 அதிகாரப்பூர்வ மொழிகள்" else "6 Official Languages of the United Nations (UN)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigoSlatePrimary
                        )
                    )
                    Text(
                        text = if (isTa) "சர்வதேச சட்டம், உடன்படிக்கைகள் மற்றும் தூதரக வழக்காடல் மொழிகள்" else "Global treaty drafting, ICJ jurisprudence & international diplomacy",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.secondary)
                    )
                }
            }
        }
    }
}

@Composable
private fun UNLanguageCardItem(unLang: UNOfficialLanguage, isTa: Boolean) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (unLang.isUNOfficial6) PaleSandstoneVariant else Color.White),
        border = BorderStroke(
            1.dp,
            if (unLang.isUNOfficial6) DeepIndigoSlatePrimary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = unLang.nativeName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigoSlatePrimary
                        )
                    )
                    Text(
                        text = "(${if (isTa) unLang.tamilName else unLang.englishName})",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (unLang.isUNOfficial6) Color(0xFF1B5E20) else TerracottaAccentSecondary
                ) {
                    Text(
                        text = if (unLang.isUNOfficial6) (if (isTa) "ஐ.நா. அதிகாரப்பூர்வ மொழி" else "UN Official 6") else (if (isTa) "முக்கிய சட்ட மொழி" else "Strategic Legal"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isTa) unLang.usageScopeTa else unLang.usageScopeEn,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.Translate, contentDescription = null, tint = TerracottaAccentSecondary, modifier = Modifier.size(14.dp))
                Text(
                    text = if (isTa) "உலகளாவிய பேசுவோர்: ${unLang.globalSpeakers}" else "Global Speakers: ${unLang.globalSpeakers}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DeepIndigoSlatePrimary
                    )
                )
            }
        }
    }
}

@Composable
private fun ComparativeFrameworkMatrixCard(isTa: Boolean) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WarmIvorySurface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = if (isTa) "⚖️ சர்வதேச சட்டக் கட்டமைப்புகள் ஒப்பீடு:" else "⚖️ Comparative Global Legal Systems Matrix:",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = DeepIndigoSlatePrimary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            LegalSystemType.values().forEach { sys ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PaleSandstoneVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (isTa) sys.titleTa else sys.titleEn,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigoSlatePrimary
                            )
                        )
                        Text(
                            text = if (isTa) sys.descriptionTa else sys.descriptionEn,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UNPeaceTreatyNoticeCard(isTa: Boolean) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryContainerSlate),
        border = BorderStroke(1.dp, DeepIndigoSlatePrimary.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Public,
                contentDescription = null,
                tint = DeepIndigoSlatePrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = if (isTa) "ஐக்கிய நாடுகள் சபையின் 193 உறுப்பு நாடுகள் நெறிமுறை" else "193 UN Member States Sovereign Legal Accord",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepIndigoSlatePrimary
                    )
                )
                Text(
                    text = if (isTa) "ஜஸ்ட்ரா சர்வதேச டயஸ்போரா மற்றும் சிவில்/பொது சட்ட கட்டமைப்புகளை சர்வதேச சாசனங்களின்படி வழிநடத்துகிறது." else "Justra guides expatriate legal intake, consular relays, and cross-border arbitration pursuant to UN & New York Convention treaties.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}
