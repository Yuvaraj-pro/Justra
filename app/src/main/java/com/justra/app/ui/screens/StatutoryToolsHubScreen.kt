package com.justra.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.domain.model.DisputeCategory
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.components.AppBottomNavBar
import com.justra.app.ui.components.NyayaTopBar
import com.justra.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatutoryToolsHubScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onLockApp: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onSelectTopic: (categoryKey: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isTa = currentLanguage == LanguagePreference.TAMIL
    var selectedTab by remember { mutableStateOf(0) }
    val categories = remember { DisputeCategory.values().toList() }

    Scaffold(
        topBar = {
            NyayaTopBar(
                title = if (isTa) "சட்டக் கருவிகள்" else "Law & Tools",
                currentLanguage = currentLanguage,
                unreadNotifications = 0,
                onToggleLanguage = onToggleLanguage,
                onNotificationClick = { onNavigateToRoute("notifications_center") },
                onLockClick = onLockApp
            )
        },
        bottomBar = {
            AppBottomNavBar(
                currentRoute = "statutory_tools_hub",
                onNavigateTo = onNavigateToRoute,
                currentLanguage = currentLanguage
            )
        },
        containerColor = WarmCanvasBg,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isTa) "சட்டக் கருவிகள் & பகுதிகள்" else "Legal Tools and Law Library",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = SovereignNavy,
                    fontFamily = FontFamily.Serif
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = SovereignNavy
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(if (isTa) "சட்டப் பகுதிகள்" else "Indian Law Categories", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(if (isTa) "சட்டக் கருவிகள் (4)" else "Legal Utilities (4)", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text(if (isTa) "உலகளாவிய சட்டம்" else "Global Law Hub", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(categories, key = { it.name }) { category ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSelectTopic(category.name) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Gavel, contentDescription = null, tint = SovereignNavy, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isTa) category.titleTa else category.titleEn,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SovereignNavy)
                                        )
                                        Text(
                                            text = if (isTa) category.descriptionTa else category.descriptionEn,
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
                1 -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        item {
                            UtilityToolCard(
                                title = if (isTa) "நீதிமன்ற கட்டண கணக்கீடு" else "Court Fee Calculator",
                                subtitle = if (isTa) "மாநிலங்கள் வாரியாக நீதிமன்ற கட்டணம் மற்றும் முத்திரை வில்லை கணக்கிடுக" else "Calculate state-specific court fees & stamp duty",
                                icon = Icons.Default.Calculate,
                                onClick = { onNavigateToRoute("court_fee_calculator") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "பிரிவு 65B சான்றிதழ் வழிகாட்டி" else "65B Evidence Certificate Wizard",
                                subtitle = if (isTa) "டிஜிட்டல் ஆதாரங்களுக்கான 65B சான்றிதழ் உருவாக்கவும்" else "Generate mandatory Section 65B affidavit for digital evidence",
                                icon = Icons.Default.Verified,
                                onClick = { onNavigateToRoute("section_65b_certificate") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "RTI மனு வழிகாட்டி" else "RTI Application Generator",
                                subtitle = if (isTa) "தகவல் அறியும் உரிமைச் சட்ட மனு உடனே தயார் செய்க" else "Draft official Right to Information queries instantly",
                                icon = Icons.Default.Description,
                                onClick = { onNavigateToRoute("rti_drafting_wizard") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "BNS புதிய சட்ட மாற்றி" else "BNS / IPC Law Converter",
                                subtitle = if (isTa) "பழைய IPC பிரிவுகளை புதிய BNS பிரிவுகளாக மாற்றவும்" else "Convert old IPC sections into new Bharatiya Nyaya Sanhita (BNS) provisions",
                                icon = Icons.Default.Transform,
                                onClick = { onNavigateToRoute("bns_ipc_transition") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "ஜாமீன் தகுதி கணிப்பான் (BNSS 479)" else "Bail Eligibility Predictor (BNSS 479)",
                                subtitle = if (isTa) "சட்டப்பூர்வ ஜாமீன் உரிமை மற்றும் காவலில் உள்ள காலம் கணக்கீடு" else "Analyze statutory bail eligibility and custody days under Sec 479 BNSS",
                                icon = Icons.Default.Gavel,
                                onClick = { onNavigateToRoute("bail_predictor") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "செக் மோசடி 138 அறிவிப்பு வழிகாட்டி" else "Cheque Bounce Sec 138 Notice Builder",
                                subtitle = if (isTa) "மாற்றுச்சீட்டுச் சட்டம் பிரிவு 138 கீழ் 15 நாள் சட்டப்பூர்வ அறிவிப்பு" else "Draft 15-day statutory demand notice under Negotiable Instruments Act",
                                icon = Icons.Default.Payments,
                                onClick = { onNavigateToRoute("cheque_bounce_wizard") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "POSH & சைபர் ஸ்டாக்கிங் பிரிவு" else "POSH Act & Cyber Stalking Cell",
                                subtitle = if (isTa) "பணியிட பாலியல் புகார் & BNS பிரிவு 78 ஆன்லைன் மிரட்டல்" else "Workplace ICC complaints & BNS 78 cyber stalking redressal",
                                icon = Icons.Default.Shield,
                                onClick = { onNavigateToRoute("cyber_posh_cell") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "மூத்த குடிமக்கள் பராமரிப்பு தீர்ப்பாயம்" else "Senior Citizens Maintenance Tribunal",
                                subtitle = if (isTa) "பெற்றோர் மற்றும் மூத்த குடிமக்கள் பராமரிப்பு & சொத்து ரத்து (MWPSC 2007)" else "MWPSC Act 2007 maintenance application & gift deed revocation",
                                icon = Icons.Default.Elderly,
                                onClick = { onNavigateToRoute("senior_citizen_tribunal") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "பரஸ்பர விவாகரத்து 13B வழிகாட்டி" else "Mutual Consent Divorce Roadmap (Sec 13B)",
                                subtitle = if (isTa) "இந்து திருமணச் சட்டம் பிரிவு 13B இருதரப்பு சம்மத விவாகரத்து" else "Hindu Marriage Act 13B & Special Marriage Act 28 statutory process",
                                icon = Icons.Default.FavoriteBorder,
                                onClick = { onNavigateToRoute("divorce_mutual_consent") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "கைது உரிமைகள் வழிகாட்டி (BNSS 35)" else "Arrest Rights & BNSS 35 Guide",
                                subtitle = if (isTa) "D.K. பாசு உச்ச நீதிமன்ற விதிகள் & 24 மணி நேர ஆஜர்படுத்துதல் உரிமை" else "Sec 35/47 BNSS 2023 & D.K. Basu arrest defense guidelines",
                                icon = Icons.Default.Policy,
                                onClick = { onNavigateToRoute("arrest_rights_guide") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "பணிக்கொடை (Gratuity) & PF கணக்கீடு" else "Gratuity & EPF Claim Calculator",
                                subtitle = if (isTa) "பணிக்கொடை வழங்கல் சட்டம் 1972 & 10% தாமத வட்டி" else "Payment of Gratuity Act 1972 5-year eligibility & interest claim",
                                icon = Icons.Default.AccountBalanceWallet,
                                onClick = { onNavigateToRoute("gratuity_pf_calculator") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "வங்கி கணக்கு முடக்கம் நீக்க கோரிக்கை" else "Cyber Bank Account Freeze Unblock",
                                subtitle = if (isTa) "தேசிய இணைய குற்றப் பதிவு (1930) வங்கி கணக்கு முடக்கம் விடுவிப்பு" else "Challenge Sec 102 BNSS bank account lien & 1930 cyber freeze",
                                icon = Icons.Default.AccountBalance,
                                onClick = { onNavigateToRoute("cyber_bank_unfreeze") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "வணிக கடை உரிமம் & Shop Act தணிக்கை" else "Trade License & Shop Act Inspector",
                                subtitle = if (isTa) "30 நாட்களுக்குள் வணிக கடை பதிவு & மாநகராட்சி உரிமம்" else "Municipal Trade License & Labor Inspectorate statutory compliance",
                                icon = Icons.Default.Storefront,
                                onClick = { onNavigateToRoute("trade_license_shop_act") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "RERA ரியல் எஸ்டேட் தாமத வட்டி கணக்கீடு" else "RERA Homebuyer Delay Compensation",
                                subtitle = if (isTa) "RERA சட்டம் 2016 பிரிவு 18 கட்டட தாமத வட்டி கோரிக்கை" else "Calculate builder delay interest & 100% refund under Sec 18 RERA",
                                icon = Icons.Default.HomeWork,
                                onClick = { onNavigateToRoute("rera_homebuyer_dispute") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "முத்திரை காப்புரிமை IPR எச்சரிக்கை அறிவிப்பு" else "IPR Trademark Cease & Desist Builder",
                                subtitle = if (isTa) "வர்த்தக முத்திரைச் சட்டம் 1999 & பதிப்புரிமைச் சட்டம் 1957" else "Draft formal warning against brand impersonation & copyright theft",
                                icon = Icons.Default.Copyright,
                                onClick = { onNavigateToRoute("ipr_trademark_copyright") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "நடுவர் மன்ற ஒப்பந்தப் பிரிவு வரைவு" else "Arbitration Fast-Track Clause Builder",
                                subtitle = if (isTa) "இணக்க ஒப்புரவு மற்றும் நடுவர் மன்றச் சட்டம் 1996 பிரிவு 29B" else "Generate 6-month fast-track commercial dispute arbitration clauses",
                                icon = Icons.Default.Gavel,
                                onClick = { onNavigateToRoute("arbitration_clause_draft") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "மக்கள் நீதிமன்றம் (Lok Adalat) மனு" else "Pre-Litigation Lok Adalat Petition",
                                subtitle = if (isTa) "சட்டப் பணிகள் ஆணைக்குழு சட்டம் 1987 பிரிவு 19 கட்டணமில்லா தீர்வு" else "Legal Services Authorities Act 1987 zero court-fee binding award",
                                icon = Icons.Default.Groups,
                                onClick = { onNavigateToRoute("lok_adalat_petition") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "உயில் தயாரிப்பு & வாரிசு சான்றிதழ்" else "Will & Legal Heir Certificate Wizard",
                                subtitle = if (isTa) "இந்திய வாரிசுரிமைச் சட்டம் 1925 உயில் வரைவு" else "Indian Succession Act 1925 Will drafting & legal heir process",
                                icon = Icons.Default.HistoryEdu,
                                onClick = { onNavigateToRoute("will_probate_succession") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "மருத்துவ அலட்சிய இழப்பீடு மனு" else "Medical Negligence Claim Assessor",
                                subtitle = if (isTa) "NMC விதிகள் பிரிவு 1.3.2 72 மணி நேர ஆவண உரிமை" else "Consumer Protection Act 2019 & Medical Council negligence claims",
                                icon = Icons.Default.LocalHospital,
                                onClick = { onNavigateToRoute("medical_negligence_claim") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "காப்பீட்டு நிராகரிப்பு மேல்முறையீடு" else "Insurance Ombudsman Appeal Wizard",
                                subtitle = if (isTa) "IRDAI ஓம்பட்ஸ்மேன் ரூ. 50 லட்சம் வரையிலான இலவச மனு" else "File grievance against rejected health/vehicle insurance claims",
                                icon = Icons.Default.HealthAndSafety,
                                onClick = { onNavigateToRoute("insurance_ombudsman_appeal") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "வாடகைதாரர் சட்டவிரோத வெளியேற்ற தடுப்பு" else "Tenant Eviction Defense Guide",
                                subtitle = if (isTa) "மாதிரி வாடகை சட்டம் 2021 பிரிவு 20 மின்சார துண்டிப்பு தடை" else "Model Tenancy Act 2021 defense against illegal eviction & water cutoff",
                                icon = Icons.Default.Key,
                                onClick = { onNavigateToRoute("tenant_eviction_defense") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "அவதூறு சட்ட அறிவிப்பு (BNS 356)" else "Defamation Notice Builder (BNS 356)",
                                subtitle = if (isTa) "BNS 2023 பிரிவு 356 ஆன்லைன்/ஆஃப்லைன் அவதூறு மன்னிப்பு அறிவிப்பு" else "Bharatiya Nyaya Sanhita Sec 356 criminal defamation notice",
                                icon = Icons.Default.Campaign,
                                onClick = { onNavigateToRoute("defamation_notice_wizard") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "பாஸ்போர்ட் முடக்கம் & LOC மேல்முறையீடு" else "Passport Impoundment & LOC Appeal",
                                subtitle = if (isTa) "பாஸ்போர்ட் சட்டம் 1967 பிரிவு 10(3) விளக்கம் கேட்பு மனு" else "Passports Act 1967 Sec 10(3) RPO challenge & Look Out Circular relief",
                                icon = Icons.Default.FlightTakeoff,
                                onClick = { onNavigateToRoute("passport_impound_appeal") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "ஒப்பந்தக்காரர்/வெண்டார் நிலுவை மீட்பு" else "Contractor & Vendor Invoice Recovery",
                                subtitle = if (isTa) "CPC Order 37 90 நாள் சுருக்க வழக்கு & ஒப்பந்த சட்டம் 73" else "Summary recovery suit under Order 37 CPC for unpaid business invoices",
                                icon = Icons.Default.ReceiptLong,
                                onClick = { onNavigateToRoute("vendor_recovery_suit") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "தேசிய பசுமை தீர்ப்பாயம் NGT புகார்" else "National Green Tribunal (NGT) Portal",
                                subtitle = if (isTa) "NGT சட்டம் 2010 காற்று/நீர்/ஒலி மாசுபாடு தடுப்பு" else "National Green Tribunal Act 2010 environmental damage injunction",
                                icon = Icons.Default.Park,
                                onClick = { onNavigateToRoute("environmental_ngt_portal") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "போலி SIM & அடையாள திருட்டு தடுப்பு" else "TAFCOP SIM Fraud & Identity Theft",
                                subtitle = if (isTa) "சஞ்சார் சாதி TAFCOP போர்ட்டலில் போலி SIM எண்களை துண்டித்தல்" else "Sanchar Saathi TAFCOP SIM blocking & IT Act Sec 66C identity theft",
                                icon = Icons.Default.PhonelinkRing,
                                onClick = { onNavigateToRoute("sim_fraud_tafcop") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "குழந்தை தத்தெடுப்பு சட்ட நடைமுறை" else "CARA Adoption Legal Process",
                                subtitle = if (isTa) "CARA போர்டல் பதிவு & சிறுவர் நீதி சட்டம் 2015" else "Central Adoption Resource Authority regulations & JJ Act 2015",
                                icon = Icons.Default.ChildCare,
                                onClick = { onNavigateToRoute("adoption_cara_process") }
                            )
                        }
                        item {
                            UtilityToolCard(
                                title = if (isTa) "வழக்கு செலவு மற்றும் வழக்கறிஞர் கட்டணம்" else "Litigation Expense & Costs Estimator",
                                subtitle = if (isTa) "சிவில் வழக்கு சட்டம் பிரிவு 35 எதிர்தரப்பிடம் செலவு பெறல்" else "High Court Advocates Fee Rules & Sec 35 CPC taxation of costs",
                                icon = Icons.Default.PriceCheck,
                                onClick = { onNavigateToRoute("litigation_costs_estimator") }
                            )
                        }
                    }
                }
                2 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        UtilityToolCard(
                            title = if (isTa) "உலகளாவிய சட்ட தரவுத்தளம்" else "Global & Comparative Law Hub",
                            subtitle = if (isTa) "சர்வதேச சட்டங்கள் மற்றும் உரிமைகள் ஒப்பீடு" else "Compare Indian legal provisions with international human rights benchmarks",
                            icon = Icons.Default.Public,
                            onClick = { onNavigateToRoute("world_wide_law") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UtilityToolCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(10.dp), color = SoftNavyContainer, modifier = Modifier.size(44.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = SovereignNavy, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = SovereignNavy))
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        }
    }
}
