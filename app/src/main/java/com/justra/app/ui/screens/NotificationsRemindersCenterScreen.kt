package com.justra.app.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.domain.model.InAppNotificationItem
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.domain.model.NotificationType
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
import com.justra.app.ui.viewmodel.NyayaMateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsRemindersCenterScreen(
    viewModel: NyayaMateViewModel,
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL

    val notifications by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadNotificationCount.collectAsState()

    var selectedFilter by remember { mutableStateOf(NotificationType.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddReminderDialog by remember { mutableStateOf(false) }

    // Custom reminder inputs
    var reminderTitle by remember { mutableStateOf("") }
    var reminderDesc by remember { mutableStateOf("") }
    var reminderDays by remember { mutableStateOf(15) }
    var reminderAct by remember { mutableStateOf("Limitation Act / Statutory Notice") }
    var reminderRoute by remember { mutableStateOf("legal_notice_composer") }

    val filteredList = notifications.filter { item ->
        val matchesCategory = when (selectedFilter) {
            NotificationType.ALL -> true
            NotificationType.DEADLINE -> item.type == NotificationType.DEADLINE || item.deadlineDaysRemaining != null
            NotificationType.CASE_STATUS -> item.type == NotificationType.CASE_STATUS
            NotificationType.SYSTEM_ALERT -> item.type == NotificationType.SYSTEM_ALERT
            NotificationType.UNREAD -> !item.isRead
        }
        val query = searchQuery.trim().lowercase()
        val matchesSearch = if (query.isEmpty()) true else {
            item.titleEn.lowercase().contains(query) ||
                    item.titleTa.lowercase().contains(query) ||
                    item.messageEn.lowercase().contains(query) ||
                    item.messageTa.lowercase().contains(query) ||
                    item.category.lowercase().contains(query) ||
                    (item.statutoryAct?.lowercase()?.contains(query) ?: false)
        }
        matchesCategory && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isTa) "அறிவிப்புகள் & நினைவூட்டல் மையம்" else "Notifications & Reminders",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigoSlatePrimary
                                )
                            )
                            if (unreadCount > 0) {
                                Surface(
                                    shape = CircleShape,
                                    color = TerracottaAccentSecondary,
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "$unreadCount",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            text = if (isTa) "சட்டப்பூர்வ காலக்கெடு & வழக்கு நிலை புதுப்பிப்புகள்" else "Statutory Limitation Deadlines & Case Status Updates",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = TerracottaAccentSecondary
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("notif_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DeepIndigoSlatePrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.markAllNotificationsRead()
                            Toast.makeText(
                                context,
                                if (isTa) "அனைத்தும் படித்ததாகக் குறிக்கப்பட்டது" else "All alerts marked as read",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.testTag("mark_all_read_top_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Mark All Read",
                            tint = DeepIndigoSlatePrimary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PrimaryContainerSlate,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onToggleLanguage() }
                            .padding(end = 12.dp)
                            .testTag("notif_language_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = DeepIndigoSlatePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isTa) "English" else "தமிழ்",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigoSlatePrimary
                                )
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmIvorySurface),
                modifier = Modifier.border(1.dp, PaleSandstoneVariant)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    reminderTitle = ""
                    reminderDesc = ""
                    reminderDays = 15
                    showAddReminderDialog = true
                },
                containerColor = DeepIndigoSlatePrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("schedule_reminder_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.AddAlert, contentDescription = "Schedule Reminder")
                    Text(
                        text = if (isTa) "+ நினைவூட்டல்" else "+ Set Reminder",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        },
        containerColor = WarmIvorySurface,
        modifier = modifier.testTag("notifications_reminders_center_screen")
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = if (isTa) "அறிவிப்புகள் அல்லது சட்டப்பிரிவுகளைத் தேடுக..." else "Search alerts, limitation clocks, statutes...",
                        style = MaterialTheme.typography.bodySmall
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
                            Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = DeepIndigoSlatePrimary)
                        }
                    }
                },
                singleLine = true,
                colors = nyayaOutlinedTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("notif_search_input")
            )

            // Category Filter Scrollable Tabs
            ScrollableTabRow(
                selectedTabIndex = NotificationType.values().indexOf(selectedFilter),
                containerColor = WarmIvorySurface,
                contentColor = DeepIndigoSlatePrimary,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    val index = NotificationType.values().indexOf(selectedFilter)
                    if (index in tabPositions.indices) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[index]),
                            color = TerracottaAccentSecondary
                        )
                    }
                },
                divider = { HorizontalDivider(color = PaleSandstoneVariant) }
            ) {
                NotificationType.values().forEach { filterType ->
                    val isSelected = selectedFilter == filterType
                    val count = when (filterType) {
                        NotificationType.ALL -> notifications.size
                        NotificationType.DEADLINE -> notifications.count { it.type == NotificationType.DEADLINE || it.deadlineDaysRemaining != null }
                        NotificationType.CASE_STATUS -> notifications.count { it.type == NotificationType.CASE_STATUS }
                        NotificationType.SYSTEM_ALERT -> notifications.count { it.type == NotificationType.SYSTEM_ALERT }
                        NotificationType.UNREAD -> notifications.count { !it.isRead }
                    }

                    Tab(
                        selected = isSelected,
                        onClick = { selectedFilter = filterType },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (isTa) filterType.labelTa else filterType.labelEn,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) DeepIndigoSlatePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                if (count > 0) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSelected) TerracottaAccentSecondary else PaleSandstoneVariant,
                                        modifier = Modifier.size(18.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "$count",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) Color.White else DeepIndigoSlatePrimary
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.testTag("filter_tab_${filterType.name}")
                    )
                }
            }

            // Limitation Clock Banner
            Surface(
                color = HennaRedAlertContainer.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, HennaRedAlertText.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = HennaRedAlertText,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isTa) "சட்டப்பூர்வ காலக்கெடு சட்டம் (Limitation Act 1963): காலாவதியான பின் மனு தாக்கல் செய்ய முடியாது. உடனடி நடவடிக்கை தேவை."
                        else "Statutory Limitation Act, 1963: Rights expire after statutory notice cure windows. Review critical timers below.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HennaRedAlertText
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Notification Feed List
            if (filteredList.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = PaleSandstoneVariant),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier.size(120.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = DeepIndigoSlatePrimary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = if (isTa) "அறிவிப்புகள் இல்லை" else "No Notifications Yet",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigoSlatePrimary
                                )
                            )
                            Text(
                                text = if (isTa)
                                    "நீங்கள் வழக்குகள் உருவாக்கவும், நினைவூட்டல்கள் அமைக்கவும், அல்லது சட்ட நடவடிக்கைகள் எடுக்கும்போது, இங்கு அண்மை தகவல்கள் தோன்றும்."
                                else
                                    "Notifications will appear here when you create cases, set statutory reminders, or take legal actions. Tap the + button below to schedule your first reminder.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                maxLines = 4
                            )
                        }

                        // Helpful action hint
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PrimaryContainerSlate,
                            border = BorderStroke(1.dp, DeepIndigoSlatePrimary.copy(alpha = 0.2f)),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = DeepIndigoSlatePrimary, modifier = Modifier.size(20.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isTa) "முடிவு: சட்ட காலக்கெடுகளுக்கான நினைவூட்டல்கள் அமைக்க + பொத்தானை பயன்படுத்தவும்" else "Tip: Use the + button to set statutory deadline reminders",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = DeepIndigoSlatePrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        NotificationCard(
                            item = item,
                            isTa = isTa,
                            onCardClick = {
                                viewModel.markNotificationRead(item.id)
                                onNavigateToRoute(item.targetRoute)
                            },
                            onActionClick = {
                                viewModel.markNotificationRead(item.id)
                                onNavigateToRoute(item.targetRoute)
                            },
                            onMarkRead = { viewModel.markNotificationRead(item.id) },
                            onDelete = { viewModel.deleteNotification(item.id) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            }
        }
    }

    // Schedule Custom Reminder Dialog
    if (showAddReminderDialog) {
        AlertDialog(
            onDismissRequest = { showAddReminderDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.AddAlert, contentDescription = null, tint = DeepIndigoSlatePrimary)
                    Text(
                        text = if (isTa) "புதிய சட்ட நினைவூட்டலை அமைக்க" else "Schedule Statutory Reminder",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepIndigoSlatePrimary
                        )
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (isTa) "வழக்கு அல்லது நோட்டீஸ் காலக்கெடுக்கான நாட்களை தேர்ந்தெடுக்கவும்:" else "Set a countdown reminder for statutory notices or court deadlines:",
                        style = MaterialTheme.typography.bodySmall
                    )

                    // Title
                    OutlinedTextField(
                        value = reminderTitle,
                        onValueChange = { reminderTitle = it },
                        label = { Text(if (isTa) "நினைவூட்டல் தலைப்பு" else "Reminder Title") },
                        placeholder = { Text(if (isTa) "எ.கா: 15-நாள் நோட்டீஸ் பதில்" else "e.g. 15-day Notice Response") },
                        singleLine = true,
                        colors = nyayaOutlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_reminder_title_input")
                    )

                    // Description
                    OutlinedTextField(
                        value = reminderDesc,
                        onValueChange = { reminderDesc = it },
                        label = { Text(if (isTa) "விளக்கம் / குறிப்புகள்" else "Notes / Action Required") },
                        singleLine = true,
                        colors = nyayaOutlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_reminder_desc_input")
                    )

                    // Days preset chips
                    Column {
                        Text(
                            text = if (isTa) "சட்டப்பூர்வ கால அளவு (Statutory Window):" else "Statutory Window Preset:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                15 to "15 Days (NI 138)",
                                30 to "30 Days (RTI)",
                                45 to "45 Days (Appeal)",
                                90 to "90 Days (MACT)"
                            ).forEach { (days, label) ->
                                val isSelected = reminderDays == days
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) DeepIndigoSlatePrimary else WarmIvorySurface,
                                    border = BorderStroke(1.dp, if (isSelected) DeepIndigoSlatePrimary else PaleSandstoneVariant),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            reminderDays = days
                                            when (days) {
                                                15 -> {
                                                    reminderAct = "Sec 138 NI Act"
                                                    reminderRoute = "legal_notice_composer"
                                                }
                                                30 -> {
                                                    reminderAct = "RTI Act 2005"
                                                    reminderRoute = "rti_drafting_wizard"
                                                }
                                                45 -> {
                                                    reminderAct = "Consumer Protection Act"
                                                    reminderRoute = "consumer_mediation"
                                                }
                                                90 -> {
                                                    reminderAct = "Motor Vehicles Act"
                                                    reminderRoute = "motor_accident_mact"
                                                }
                                            }
                                        }
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else DeepIndigoSlatePrimary
                                        ),
                                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val title = if (reminderTitle.isBlank()) "Statutory $reminderDays-Day Deadline" else reminderTitle
                        val desc = if (reminderDesc.isBlank()) "Statutory limitation period active for $reminderAct." else reminderDesc
                        viewModel.addCustomReminder(
                            titleEn = title,
                            titleTa = if (isTa) "சட்டப்பூர்வ $reminderDays நாள் காலக்கெடு" else title,
                            messageEn = desc,
                            messageTa = if (isTa) "$reminderAct சட்டத்தின் கீழான காலக்கெடு." else desc,
                            days = reminderDays,
                            statutoryAct = reminderAct,
                            targetRoute = reminderRoute
                        )
                        showAddReminderDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary),
                    modifier = Modifier.testTag("save_custom_reminder_button")
                ) {
                    Text(if (isTa) "அமைக்க (Set)" else "Schedule Reminder")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddReminderDialog = false }) {
                    Text(if (isTa) "ரத்து" else "Cancel")
                }
            },
            containerColor = WarmIvorySurface
        )
    }
}

@Composable
private fun NotificationCard(
    item: InAppNotificationItem,
    isTa: Boolean,
    onCardClick: () -> Unit,
    onActionClick: () -> Unit,
    onMarkRead: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUrgent = item.isUrgent || (item.deadlineDaysRemaining != null && item.deadlineDaysRemaining <= 5)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isRead) WarmIvorySurface else (if (isUrgent) HennaRedAlertContainer.copy(alpha = 0.45f) else PrimaryContainerSlate)
        ),
        border = BorderStroke(
            if (!item.isRead && isUrgent) 1.5.dp else 1.dp,
            if (isUrgent) HennaRedAlertText.copy(alpha = 0.4f) else (if (!item.isRead) DeepIndigoSlatePrimary.copy(alpha = 0.4f) else PaleSandstoneVariant)
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("notif_card_${item.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header Row: Category Badge, Urgency, and Read Dot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Category Chip
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isUrgent) HennaRedAlertContainer else PaleSandstoneVariant,
                        border = BorderStroke(1.dp, if (isUrgent) HennaRedAlertText.copy(alpha = 0.3f) else DeepIndigoSlatePrimary.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = item.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isUrgent) HennaRedAlertText else DeepIndigoSlatePrimary
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Countdown Badge if deadline
                    if (item.deadlineDaysRemaining != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (item.deadlineDaysRemaining <= 5) HennaRedAlertText else SaffronAmberWarningText,
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                Text(
                                    text = if (isTa) "${item.deadlineDaysRemaining} நாட்கள் எஞ்சியுள்ளன" else "${item.deadlineDaysRemaining}d remaining",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (!item.isRead) {
                        Surface(
                            shape = CircleShape,
                            color = TerracottaAccentSecondary,
                            modifier = Modifier.size(8.dp)
                        ) {}
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Dismiss",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Title
            Text(
                text = if (isTa) item.titleTa else item.titleEn,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isUrgent) HennaRedAlertText else DeepIndigoSlatePrimary
                )
            )

            // Message Body
            Text(
                text = if (isTa) item.messageTa else item.messageEn,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            )

            // Statutory Act if provided
            if (item.statutoryAct != null) {
                Text(
                    text = "Statute: ${item.statutoryAct}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TerracottaAccentSecondary
                    )
                )
            }

            HorizontalDivider(color = PaleSandstoneVariant.copy(alpha = 0.6f))

            // Footer Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!item.isRead) {
                    TextButton(
                        onClick = onMarkRead,
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = DeepIndigoSlatePrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isTa) "படித்ததாகக் குறிக்க" else "Mark Read",
                            style = MaterialTheme.typography.labelSmall.copy(color = DeepIndigoSlatePrimary)
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SageGreenSuccessText, modifier = Modifier.size(14.dp))
                        Text(
                            text = if (isTa) "படிக்கப்பட்டது" else "Read",
                            style = MaterialTheme.typography.labelSmall.copy(color = SageGreenSuccessText)
                        )
                    }
                }

                Button(
                    onClick = onActionClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isUrgent) HennaRedAlertText else DeepIndigoSlatePrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = if (isTa) (item.actionLabelTa ?: "நடவடிக்கை எடுக்க") else (item.actionLabelEn ?: "Take Action"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
