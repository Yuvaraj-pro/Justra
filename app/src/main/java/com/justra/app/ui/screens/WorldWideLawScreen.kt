package com.justra.app.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.domain.model.WorldRegion
import com.justra.app.domain.model.WorldWideLawDataType
import com.justra.app.domain.model.WorldWideLawDocument
import com.justra.app.domain.model.WorldWideLawSource
import com.justra.app.ui.theme.CharcoalTextPrimary
import com.justra.app.ui.theme.DeepIndigoSlatePrimary
import com.justra.app.ui.theme.PaleSandstoneVariant
import com.justra.app.ui.theme.PrimaryContainerSlate
import com.justra.app.ui.theme.SageGreenSuccessContainer
import com.justra.app.ui.theme.SageGreenSuccessText
import com.justra.app.ui.theme.TerracottaAccentSecondary
import com.justra.app.ui.theme.WarmIvorySurface
import com.justra.app.ui.theme.WarmOutline
import com.justra.app.ui.theme.nyayaOutlinedTextFieldColors
import com.justra.app.ui.viewmodel.NyayaMateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldWideLawScreen(
    viewModel: NyayaMateViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.language.collectAsState()
    val isTa = language == LanguagePreference.TAMIL

    val allSources by viewModel.worldWideLawSources.collectAsState()
    val pinnedSources by viewModel.pinnedWorldWideLawSources.collectAsState()
    val allDocs by viewModel.worldWideLawDocs.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: All Sources, 1: Case Law & Decisions, 2: Offline Pinned
    var searchQuery by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf(WorldRegion.ALL) }
    var selectedDataType by remember { mutableStateOf<WorldWideLawDataType?>(null) }
    var selectedDocumentForViewing by remember { mutableStateOf<WorldWideLawDocument?>(null) }

    // Filter sources
    val filteredSources = remember(allSources, searchQuery, selectedRegion, selectedDataType) {
        allSources.filter { src ->
            val matchesRegion = selectedRegion == WorldRegion.ALL || src.region == selectedRegion
            val matchesType = selectedDataType == null || src.dataTypes.contains(selectedDataType)
            val matchesQuery = searchQuery.isBlank() ||
                    src.name.contains(searchQuery, ignoreCase = true) ||
                    src.id.contains(searchQuery, ignoreCase = true) ||
                    src.countryCode.contains(searchQuery, ignoreCase = true) ||
                    src.countryNameEn.contains(searchQuery, ignoreCase = true) ||
                    src.countryNameTa.contains(searchQuery, ignoreCase = true) ||
                    src.lawEnforcementDomain.contains(searchQuery, ignoreCase = true)
            matchesRegion && matchesType && matchesQuery
        }
    }

    // Filter documents
    val filteredDocs = remember(allDocs, searchQuery) {
        if (searchQuery.isBlank()) {
            allDocs
        } else {
            allDocs.filter { doc ->
                doc.title.contains(searchQuery, ignoreCase = true) ||
                        doc.text.contains(searchQuery, ignoreCase = true) ||
                        doc.lawEnforcementSubject.contains(searchQuery, ignoreCase = true) ||
                        doc.countryCode.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        modifier = modifier.testTag("world_wide_law_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (isTa) "உலகளாவிய சட்டத் தரவுத்தளம்" else "World Wide Law Hub",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SageGreenSuccessContainer
                            ) {
                                Text(
                                    text = "110+ NATIONS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SageGreenSuccessText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isTa) "960+ சேகரிப்பு ஸ்கிரிப்ட்கள் • 1.6 கோடி திறந்தநிலை ஆவணங்கள்" else "960+ Collection Scripts • 16M+ Open Legal Records",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://legaldatahunter.com"))
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Live Dashboard",
                            tint = DeepIndigoSlatePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WarmIvorySurface,
                    titleContentColor = CharcoalTextPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(WarmIvorySurface)
        ) {
            // Live Open Data Stats & Legal Data Hunter Banner
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryContainerSlate),
                border = androidx.compose.foundation.BorderStroke(1.dp, DeepIndigoSlatePrimary.copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = DeepIndigoSlatePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isTa) "திறந்தநிலை சட்ட உள்கட்டமைப்பு" else "Open Legal Infrastructure",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigoSlatePrimary
                                )
                            )
                        }

                        Text(
                            text = "legaldatahunter.com",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TerracottaAccentSecondary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://legaldatahunter.com"))
                                context.startActivity(intent)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricItem(value = "110+", label = if (isTa) "நாடுகள்" else "Countries")
                        MetricItem(value = "960+", label = if (isTa) "ஸ்கிரிப்ட்கள்" else "Scripts")
                        MetricItem(value = "3,413", label = if (isTa) "ஆதாரங்கள்" else "Endpoints")
                        MetricItem(value = "16M+", label = if (isTa) "ஆவணங்கள்" else "Indexed Docs")
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isTa)
                            "அரசு போர்ட்டல்கள், உச்ச நீதிமன்றங்கள் மற்றும் ஒழுங்குமுறை அமைப்புகளிலிருந்து சட்டங்களை தரநிலைப்படுத்தி ஆஃப்லைனில் வழங்குகிறது."
                        else
                            "Standardized open legal data normalized from official government gazettes, supreme courts, and regulatory enforcement authorities.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Tabs Row
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = WarmIvorySurface,
                contentColor = DeepIndigoSlatePrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = if (isTa) "ஆதாரங்கள் (${filteredSources.size})" else "Sources (${filteredSources.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = if (isTa) "தீர்ப்புகள் & தடைகள்" else "Case Law & Sanctions",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        BadgedBox(badge = {
                            if (pinnedSources.isNotEmpty()) {
                                Badge(containerColor = TerracottaAccentSecondary) {
                                    Text(text = "${pinnedSources.size}")
                                }
                            }
                        }) {
                            Text(
                                text = if (isTa) "ஆஃப்லைன் பெட்டகம்" else "Offline Vault",
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                )
            }

            // Search and Filters Bar
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (isTa) "நாடு, சட்டம், நீதிமன்றம் அல்லது ஒழுங்குமுறை ஆணையம் தேடுக..." else "Search country, statute, tribunal, court or enforcement...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Text("✕", fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = nyayaOutlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("world_wide_law_search")
                )

                if (selectedTab == 0) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Region Scrollable Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        WorldRegion.values().forEach { region ->
                            val isSelected = selectedRegion == region
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedRegion = region },
                                label = {
                                    Text(
                                        text = if (isTa) region.titleTa else region.titleEn,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DeepIndigoSlatePrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Data Types Scrollable Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedDataType == null,
                            onClick = { selectedDataType = null },
                            label = { Text(if (isTa) "அனைத்து வகைகள்" else "All Types", style = MaterialTheme.typography.labelSmall) }
                        )

                        WorldWideLawDataType.values().forEach { dt ->
                            val isSelected = selectedDataType == dt
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedDataType = if (isSelected) null else dt },
                                label = {
                                    Text(
                                        text = if (isTa) dt.titleTa else dt.titleEn,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryContainerSlate,
                                    selectedLabelColor = DeepIndigoSlatePrimary
                                )
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = WarmOutline.copy(alpha = 0.5f))

            // Main Content Area based on Tab
            when (selectedTab) {
                0 -> {
                    // Sources List
                    if (filteredSources.isEmpty()) {
                        EmptyStateView(
                            title = if (isTa) "ஆதாரங்கள் எதுவும் காணப்படவில்லை" else "No matching legal sources found",
                            subtitle = if (isTa) "தேடல் சொற்கள் அல்லது பிராந்திய வடிகட்டியை மாற்றவும்" else "Try clearing filters or searching for another jurisdiction (e.g. IN, US, UK, FR)"
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredSources, key = { it.id }) { source ->
                                WorldWideLawSourceCard(
                                    source = source,
                                    isPinned = pinnedSources.any { it.id == source.id },
                                    isTa = isTa,
                                    onTogglePin = {
                                        val isCurrent = pinnedSources.any { it.id == source.id }
                                        viewModel.togglePinWorldWideLawSource(source.id, isCurrent)
                                    },
                                    onOpenUrl = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
                                        context.startActivity(intent)
                                    },
                                    onViewSampleDocs = {
                                        val match = allDocs.firstOrNull { it.sourceId == source.id }
                                            ?: allDocs.firstOrNull { it.countryCode == source.countryCode }
                                        if (match != null) {
                                            selectedDocumentForViewing = match
                                        } else {
                                            val fallback = WorldWideLawDocument(
                                                id = "${source.id.lowercase()}/sample-norm",
                                                sourceId = source.id,
                                                countryCode = source.countryCode,
                                                flagEmoji = source.flagEmoji,
                                                type = source.dataTypes.firstOrNull()?.code ?: "legislation",
                                                title = "${source.name} - Consolidated Record",
                                                text = "Standardized document normalized via World Wide Law script (${source.collectionScriptPath}).\n\nAuthority: ${source.name}\nDomain: ${source.lawEnforcementDomain}\nLicense: ${source.licenseName}\nStatus: ${source.status.name}\n\nOfficial Portal Endpoint: ${source.url}",
                                                date = "2026-01-01",
                                                url = source.url,
                                                keyHoldingsEn = "Official open data record from ${source.countryNameEn} normalized into common JSON schema.",
                                                keyHoldingsTa = "${source.countryNameTa} நாட்டின் திறந்தநிலை சட்ட ஆவணம் பொதுவான JSON திட்டத்தில் சேர்க்கப்பட்டுள்ளது.",
                                                lawEnforcementSubject = source.lawEnforcementDomain,
                                                isSavedToVault = false
                                            )
                                            selectedDocumentForViewing = fallback
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Normalized Case Law & Decisions
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredDocs, key = { it.id }) { doc ->
                            WorldWideLawDocCard(
                                document = doc,
                                isTa = isTa,
                                onClick = { selectedDocumentForViewing = doc },
                                onToggleSave = {
                                    viewModel.toggleSaveWorldWideLawDocToVault(doc.id, doc.isSavedToVault)
                                },
                                onShare = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "${doc.title}\nCitation: ${doc.id}\nSource: ${doc.url}\n\nKey Ratio:\n${if (isTa) doc.keyHoldingsTa else doc.keyHoldingsEn}")
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Legal Ratio"))
                                }
                            )
                        }
                    }
                }
                2 -> {
                    // Offline Pinned Vault
                    if (pinnedSources.isEmpty()) {
                        EmptyStateView(
                            title = if (isTa) "ஆஃப்லைன் ஆதாரங்கள் எதுவும் இல்லை" else "No Pinned Offline Sources",
                            subtitle = if (isTa) "ஆதாரங்கள் தாவலில் 'ஆஃப்லைனில் சேமி' என்பதைத் தட்டுவதன் மூலம் Room தரவுத்தளத்தில் சேமிக்கவும்." else "Tap the Bookmark icon on any legal data source to cache its schema and endpoints in Room DB for 100% offline access."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SageGreenSuccessContainer,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = SageGreenSuccessText,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = if (isTa) "இந்த ஆதாரங்கள் மற்றும் குறியீட்டுத் திட்டங்கள் இணைய இணைப்பு இல்லாமலும் செயல்படும் (Room Database)."
                                            else "These sources and collection schemas are persistently saved locally in Room DB for offline legal reference.",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = SageGreenSuccessText,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                    }
                                }
                            }

                            items(pinnedSources, key = { it.id }) { source ->
                                WorldWideLawSourceCard(
                                    source = source,
                                    isPinned = true,
                                    isTa = isTa,
                                    onTogglePin = {
                                        viewModel.togglePinWorldWideLawSource(source.id, true)
                                    },
                                    onOpenUrl = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
                                        context.startActivity(intent)
                                    },
                                    onViewSampleDocs = {
                                        val match = allDocs.firstOrNull { it.sourceId == source.id }
                                        if (match != null) {
                                            selectedDocumentForViewing = match
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Document Modal Dialog
    if (selectedDocumentForViewing != null) {
        val doc = selectedDocumentForViewing!!
        AlertDialog(
            onDismissRequest = { selectedDocumentForViewing = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = doc.flagEmoji, fontSize = 22.sp)
                    Column {
                        Text(
                            text = doc.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Standard Schema ID: ${doc.id} • ${doc.date}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            },
            text = {
                Column(modifier = Modifier.verticalScrollableColumn()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PrimaryContainerSlate,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = if (isTa) "சட்ட விகிதம் & தீர்ப்பு சாரம்சம்:" else "Legal Ratio & Enforcement Finding:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigoSlatePrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isTa) doc.keyHoldingsTa else doc.keyHoldingsEn,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = CharcoalTextPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isTa) "முழு உரை (Normalized Text):" else "Operative Text (Normalized):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PaleSandstoneVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = doc.text,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            ),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            viewModel.toggleSaveWorldWideLawDocToVault(doc.id, doc.isSavedToVault)
                            selectedDocumentForViewing = doc.copy(isSavedToVault = !doc.isSavedToVault)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary)
                    ) {
                        Icon(
                            imageVector = if (doc.isSavedToVault) Icons.Default.CheckCircle else Icons.Default.Security,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (doc.isSavedToVault)
                                (if (isTa) "பெட்டகத்தில் உள்ளது" else "Saved in Vault")
                            else
                                (if (isTa) "பெட்டகத்தில் சேமி" else "Save to Vault")
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(doc.url))
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isTa) "மூல இணையம்" else "Source")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedDocumentForViewing = null }) {
                    Text(if (isTa) "மூடுக" else "Close")
                }
            }
        )
    }
}

data class CountryLawData(
    val jurisdictionName: String,
    val statuteSummary: String,
    val enforcingAuthority: String
)

@Composable
fun CountryLawCard(
    countryLaw: CountryLawData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)), // வெளிர் பின்னணி
        border = BorderStroke(1.dp, Color(0xFFD4CAB8))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // நாடு / அதிகார வரம்பு தலைப்பு
            Text(
                text = countryLaw.jurisdictionName, // e.g., "United Kingdom / Common Law"
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F1E36) // Sovereign Navy (தெளிவான அடர் நிறம்)
            )
            Spacer(modifier = Modifier.height(4.dp))
            // சட்ட விளக்கம்
            Text(
                text = countryLaw.statuteSummary,
                fontSize = 13.sp,
                color = Color(0xFF14181F), // Dark Ink (100% படிக்கக்கூடியது)
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            // அதிகார மையம் / போர்ட்டல்
            Text(
                text = "Authority: ${countryLaw.enforcingAuthority}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFC85A32) // Terracotta accent
            )
        }
    }
}

@Composable
fun WorldWideLawSourceCard(
    source: WorldWideLawSource,
    isPinned: Boolean,
    isTa: Boolean,
    onTogglePin: () -> Unit,
    onOpenUrl: () -> Unit,
    onViewSampleDocs: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3ECE1)), // Sandstone surface
        border = BorderStroke(1.dp, Color(0xFFD4CAB8)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Flag, Country, ID, Pin
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = source.flagEmoji, fontSize = 20.sp)
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = source.id,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigoSlatePrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = PrimaryContainerSlate
                            ) {
                                Text(
                                    text = if (isTa) source.countryNameTa else source.countryNameEn,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = DeepIndigoSlatePrimary
                                    ),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                IconButton(onClick = onTogglePin) {
                    Icon(
                        imageVector = if (isPinned) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Pin Offline",
                        tint = if (isPinned) TerracottaAccentSecondary else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Source Name
            Text(
                text = source.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Law Enforcement Domain Tag
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = DeepIndigoSlatePrimary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = source.lawEnforcementDomain,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = DeepIndigoSlatePrimary,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Badges Row (Data Types, Status, License)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                source.dataTypes.forEach { dt ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = PaleSandstoneVariant
                    ) {
                        Text(
                            text = if (isTa) dt.titleTa else dt.titleEn,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = CharcoalTextPrimary
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(source.status.colorHex).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (isTa) source.status.titleTa else source.status.titleEn,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(source.status.colorHex)
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                source.licenseName?.let { lic ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF1EFE8)
                    ) {
                        Text(
                            text = lic,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = Color.DarkGray
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Notes summary
            Text(
                text = source.notes,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = if (isExpanded) 10 else 2,
                overflow = TextOverflow.Ellipsis
            )

            // Expanded metadata section
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .background(PaleSandstoneVariant, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.Code, contentDescription = null, modifier = Modifier.size(14.dp), tint = DeepIndigoSlatePrimary)
                        Text(
                            text = "Collection Script: ${source.collectionScriptPath}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = DeepIndigoSlatePrimary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Standard Normalization Output:\n• _id, _source, _type, title, text, date, url\n• Auth: ${source.auth} • Commercial: ${if (source.commercialUse) "Allowed" else "Non-commercial"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            color = Color.DarkGray,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { isExpanded = !isExpanded }) {
                    Text(
                        text = if (isExpanded) (if (isTa) "குறைவாக" else "Less") else (if (isTa) "தொழில்நுட்ப விவரம்" else "Script Details"),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onViewSampleDocs,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isTa) "மாதிரி ஆவணம்" else "Sample Doc", style = MaterialTheme.typography.labelSmall)
                    }

                    Button(
                        onClick = onOpenUrl,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isTa) "போர்டல்" else "Portal", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
fun WorldWideLawDocCard(
    document: WorldWideLawDocument,
    isTa: Boolean,
    onClick: () -> Unit,
    onToggleSave: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, WarmOutline.copy(alpha = 0.6f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = document.flagEmoji, fontSize = 20.sp)
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = PrimaryContainerSlate
                            ) {
                                Text(
                                    text = document.sourceId,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepIndigoSlatePrimary
                                    ),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = document.date,
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onShare) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onToggleSave) {
                        Icon(
                            imageVector = if (document.isSavedToVault) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save to Vault",
                            tint = if (document.isSavedToVault) TerracottaAccentSecondary else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = document.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = null,
                    tint = TerracottaAccentSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = document.lawEnforcementSubject,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TerracottaAccentSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PaleSandstoneVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = if (isTa) "முக்கிய சட்டக் கொள்கை (Ratio Decidendi):" else "Core Legal Principle (Ratio Decidendi):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isTa) document.keyHoldingsTa else document.keyHoldingsEn,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CharcoalTextPrimary
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun MetricItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = DeepIndigoSlatePrimary
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
fun EmptyStateView(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = CharcoalTextPrimary
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray
                ),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

// Utility extension for dialog scroll
@Composable
fun Modifier.verticalScrollableColumn(): Modifier =
    this.then(Modifier.verticalScroll(rememberScrollState()))
