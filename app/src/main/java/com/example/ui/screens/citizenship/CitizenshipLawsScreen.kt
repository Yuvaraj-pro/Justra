package com.example.ui.screens.citizenship

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.LanguagePreference
import com.example.ui.theme.DeepHennaAlertContainer
import com.example.ui.theme.DeepImperialNavy
import com.example.ui.theme.MutedAmberWarningContainer
import com.example.ui.theme.OnDeepHennaAlert
import com.example.ui.theme.OnImperialNavy
import com.example.ui.theme.OnMutedAmberWarning
import com.example.ui.theme.OnParchmentText
import com.example.ui.theme.OnSageHerbSuccess
import com.example.ui.theme.OnSandstoneSurfaceVariant
import com.example.ui.theme.ParchmentOutline
import com.example.ui.theme.PrimaryNavyContainer
import com.example.ui.theme.SageHerbSuccessContainer
import com.example.ui.theme.SandstoneSurface
import com.example.ui.theme.WarmParchmentBase
import com.example.ui.theme.WarmTerracotta

data class StatutoryProvision(
    val id: String,
    val actName: String,
    val sectionNumber: String,
    val titleEn: String,
    val titleTa: String,
    val summaryEn: String,
    val summaryTa: String,
    val proceduralRemedyEn: String,
    val proceduralRemedyTa: String,
    val landmarkPrecedent: String,
    val category: String
)

object CitizenshipStatuteRepository {
    val provisions: List<StatutoryProvision> = listOf(
        StatutoryProvision(
            id = "sec3_birth",
            actName = "The Citizenship Act, 1955",
            sectionNumber = "Section 3",
            titleEn = "Citizenship by Birth",
            titleTa = "பிறப்பினால் இந்தியக் குடியுரிமை",
            summaryEn = "Provides citizenship rules based on birth date:\n• Pre-1 July 1987: Born in India regardless of parents' nationality.\n• 1 July 1987 - 3 Dec 2004: Either parent must be an Indian citizen.\n• Post-3 Dec 2004: Both parents citizens OR one parent citizen and neither is an illegal migrant.",
            summaryTa = "பிறந்த தேதியின் அடிப்படையில் குடியுரிமை விதிகள்:\n• 1 ஜூலை 1987க்கு முன்: இந்தியாவில் பிறந்தால் குடியுரிமை.\n• 1987 முதல் 2004 வரை: பெற்றோரில் ஒருவர் இந்தியக் குடிமகனாக இருக்க வேண்டும்.\n• 3 டிசம்பர் 2004க்குப் பின்: இருவரும் குடிமக்களாக இருக்க வேண்டும் அல்லது சட்டவிரோத குடியேறியாக இருக்கக்கூடாது.",
            proceduralRemedyEn = "Apply via Form I to the District Magistrate or Regional Passport Officer along with Municipal Birth Certificate issued under Registration of Births and Deaths Act, 1969.",
            proceduralRemedyTa = "படிவம் I மூலம் மாவட்ட ஆட்சியர் அல்லது பாஸ்போர்ட் அதிகாரியிடம் பதிவு செய்யப்பட்ட பிறப்புச் சான்றிதழுடன் விண்ணப்பிக்கவும்.",
            landmarkPrecedent = "Union of India v. Dudh Nath Prasad (AIR 2000 SC 525)",
            category = "CITIZENSHIP"
        ),
        StatutoryProvision(
            id = "sec4_descent",
            actName = "The Citizenship Act, 1955",
            sectionNumber = "Section 4",
            titleEn = "Citizenship by Descent",
            titleTa = "வழித்தோன்றல் மூலமாக குடியுரிமை",
            summaryEn = "A person born outside India on or after 3 December 2004 is NOT a citizen unless birth is registered at an Indian Consulate within one year with declaration that minor does not hold foreign passport.",
            summaryTa = "3 டிசம்பர் 2004க்குப் பிறகு வெளிநாட்டில் பிறந்த நபர், ஒரு வருடத்திற்குள் இந்திய தூதரகத்தில் பதிவு செய்யப்பட்டால் மட்டுமே குடியுரிமை பெற முடியும்.",
            proceduralRemedyEn = "Submit birth registration before the relevant Indian Embassy / High Commission with affidavit renouncing dual foreign citizenship on attaining age of 18.",
            proceduralRemedyTa = "18 வயதை அடையும் போது வெளிநாட்டு குடியுரிமையை துறக்கும் பிரமாணப் பத்திரத்துடன் தூதரகத்தில் பதிவு செய்யவும்.",
            landmarkPrecedent = "Kulathil Mammu v. State of Kerala (AIR 1966 SC 1614)",
            category = "CITIZENSHIP"
        ),
        StatutoryProvision(
            id = "sec5_registration",
            actName = "The Citizenship Act, 1955",
            sectionNumber = "Section 5",
            titleEn = "Citizenship by Registration",
            titleTa = "பதிவு மூலம் இந்தியக் குடியுரிமை",
            summaryEn = "Applies to Persons of Indian Origin (PIO) ordinarily resident in India for 7 years, persons married to Indian citizens (7 years residency), and minor children of Indian citizens.",
            summaryTa = "இந்தியாவில் 7 ஆண்டுகள் வசிக்கும் இந்திய வம்சாவளியினர் மற்றும் இந்தியக் குடிமக்களைத் திருமணம் செய்துகொண்ட நபர்களுக்குப் பொருந்தும்.",
            proceduralRemedyEn = "Apply via MHA Portal (Form II or III) to Ministry of Home Affairs with FRRO clearance, police verification report, and valid residential permit.",
            proceduralRemedyTa = "உள்துறை அமைச்சகத்தின் MHA போர்டல் மூலம் போலீஸ் சான்றிதழுடன் ஆன்லைனில் விண்ணப்பிக்கவும்.",
            landmarkPrecedent = "State of Arunachal Pradesh v. Khudiram Chakma (1994 Supp (1) SCC 615)",
            category = "CITIZENSHIP"
        ),
        StatutoryProvision(
            id = "sec6_naturalization",
            actName = "The Citizenship Act, 1955",
            sectionNumber = "Section 6",
            titleEn = "Citizenship by Naturalization",
            titleTa = "இயல்புரிமைச் சட்ட குடியுரிமை",
            summaryEn = "Granted to foreign nationals ordinarily resident for 12 years (1 year continuous prior to application + 11 years out of 14 years), good character, and proficiency in an 8th Schedule language.",
            summaryTa = "இந்தியாவில் 12 ஆண்டுகள் வசித்த, நன்னடத்தை கொண்ட மற்றும் எட்டாவது அட்டவணை இந்திய மொழிகளில் ஒன்றில் தேர்ச்சி பெற்ற வெளிநாட்டினருக்கு வழங்கப்படுகிறது.",
            proceduralRemedyEn = "Form XII filing with Gazette notification, character certificates from two Gazetted Officers, and certificate of renunciation of foreign allegiance.",
            proceduralRemedyTa = "படிவம் XII தாக்கல், கெசட் அறிவிப்பு மற்றும் வெளிநாட்டு குடியுரிமை துறப்பு சான்றிதழுடன் அணுகவும்.",
            landmarkPrecedent = "Sarbananda Sonowal v. Union of India (2005 5 SCC 665)",
            category = "CITIZENSHIP"
        ),
        StatutoryProvision(
            id = "oci_boundaries",
            actName = "The Citizenship Act, 1955 (Sec 7A-7D)",
            sectionNumber = "Section 7A - 7D & Art 9",
            titleEn = "OCI Rights & Dual Citizenship Boundaries",
            titleTa = "OCI உரிமைகள் & இரட்டைக் குடியுரிமை எல்லைகள்",
            summaryEn = "Constitutional Article 9 strictly prohibits dual citizenship. Overseas Citizen of India (OCI) is a lifelong visa card granting parity with NRIs in economic, financial, and educational fields.",
            summaryTa = "இந்திய அரசியலமைப்பு பிரிவு 9 இரட்டைக் குடியுரிமையை முற்றிலும் தடை செய்கிறது. OCI என்பது வாழ்நாள் விசா மட்டுமே; இது முழுமையான குடியுரிமை அல்ல.",
            proceduralRemedyEn = "OCI cardholders have NO voting rights, CANNOT hold public or constitutional office, and CANNOT purchase agricultural or plantation land. Registration can be cancelled under Section 7D for penal violations.",
            proceduralRemedyTa = "OCI அட்டைதாரர்களுக்கு வாக்குரிமை கிடையாது, விவசாய நிலங்களை வாங்க முடியாது. பிரிவு 7D கீழ் ரத்து செய்யப்படலாம்.",
            landmarkPrecedent = "Dr. Christo Thomas Philip v. Union of India (Delhi HC 2019)",
            category = "OCI_DUAL"
        ),
        StatutoryProvision(
            id = "passport_act",
            actName = "The Passports Act, 1967",
            sectionNumber = "Section 6 & Section 10",
            titleEn = "Passport Issuance, Impounding & Revocation",
            titleTa = "பாஸ்போர்ட் பறிமுதல் மற்றும் ரத்து செய்யும் விதிகள்",
            summaryEn = "Governs issuance, impounding, and cancellation of Indian travel documents. Regional Passport Officers cannot arbitrarily deny passports without reasoned written order under Section 5(2).",
            summaryTa = "பாஸ்போர்ட் வழங்குதல் மற்றும் பறிமுதல் செய்வதை ஒழுங்குபடுத்துகிறது. தகுந்த காரணமின்றி பாஸ்போர்ட்டை மறுக்க முடியாது.",
            proceduralRemedyEn = "If impounded or refused, file Statutory Appeal under Section 11 before the Chief Passport Officer (MEA) within 30 days. Writ petition lies under Article 226 for arbitrary impounding.",
            proceduralRemedyTa = "பாஸ்போர்ட் மறுக்கப்பட்டால், 30 நாட்களுக்குள் பிரிவு 11 கீழ் தலைமை பாஸ்போர்ட் அதிகாரியிடம் மேல்முறையீடு செய்யலாம்.",
            landmarkPrecedent = "Maneka Gandhi v. Union of India (AIR 1978 SC 597)",
            category = "PASSPORT"
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenshipLawsScreen(
    currentLanguage: LanguagePreference,
    onNavigateBack: () -> Unit,
    onNavigateToTimeline: () -> Unit,
    onNavigateToEvidenceVault: () -> Unit,
    onToggleLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabCategory by remember { mutableStateOf("ALL") }

    val documentChecklist = remember {
        mutableStateMapOf(
            "doc_birth_cert" to false,
            "doc_parents_domicile" to false,
            "doc_passport_copies" to false,
            "doc_surrender_cert" to false,
            "doc_frro_residential" to false,
            "doc_apostille" to false
        )
    }

    val statutoryProvisions = remember { CitizenshipStatuteRepository.provisions }

    val filteredProvisions = statutoryProvisions.filter { provision ->
        val matchesCategory = (selectedTabCategory == "ALL") || (provision.category == selectedTabCategory)
        val query = searchQuery.trim().lowercase()
        val matchesSearch = query.isBlank() ||
                provision.actName.lowercase().contains(query) ||
                provision.sectionNumber.lowercase().contains(query) ||
                provision.titleEn.lowercase().contains(query) ||
                provision.titleTa.lowercase().contains(query) ||
                provision.summaryEn.lowercase().contains(query) ||
                provision.summaryTa.lowercase().contains(query)
        matchesCategory && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isTa) "குடியுரிமை மற்றும் பாஸ்போர்ட் சட்டம்" else "Citizenship & Passport Laws",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepImperialNavy,
                                fontFamily = FontFamily.Serif
                            )
                        )
                        Text(
                            text = if (isTa) "The Citizenship Act 1955 & OCI விதிகளுக்கான கையேடு" else "Statutory Framework & OCI Constitutional Boundaries",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                color = OnSandstoneSurfaceVariant
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("citizenship_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DeepImperialNavy
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onToggleLanguage,
                        modifier = Modifier.testTag("citizenship_language_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Toggle Language",
                            tint = DeepImperialNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmParchmentBase)
            )
        },
        containerColor = WarmParchmentBase,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Constitutional Badge Card
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryNavyContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DeepImperialNavy.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DeepImperialNavy
                            ) {
                                Text(
                                    text = "CONSTITUTIONAL JURISDICTION",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnImperialNavy
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Union List (Entry 17)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DeepImperialNavy
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isTa) {
                                "இந்திய அரசியலமைப்பின் பகுதி II (பிரிவுகள் 5-11) மற்றும் 1955 ஆம் ஆண்டின் குடியுரிமைச் சட்டம் இந்தியக் குடியுரிமையை முற்றிலும் ஒழுங்குபடுத்துகிறது. இந்தியாவில் இரட்டைக் குடியுரிமைக்கு அனுமதியில்லை."
                            } else {
                                "Regulated under Part II of the Constitution of India (Articles 5-11) and The Citizenship Act, 1955. Article 9 strictly bars dual citizenship for Indian nationals."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DeepImperialNavy,
                                lineHeight = 17.sp
                            )
                        )
                    }
                }
            }

            // Quick Tool Launchers (Timeline & Vault)
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Timeline Button
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentOutline),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onNavigateToTimeline() }
                            .testTag("citizenship_launch_timeline_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = null,
                                tint = WarmTerracotta,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isTa) "விசா காலவரிசை" else "Dispute Timeline",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepImperialNavy
                                    )
                                )
                                Text(
                                    text = if (isTa) "நாட்கள் & காலக்கெடு" else "Build Visa/FRRO Sequence",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = OnSandstoneSurfaceVariant
                                    )
                                )
                            }
                        }
                    }

                    // Evidence Vault Button
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentOutline),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onNavigateToEvidenceVault() }
                            .testTag("citizenship_launch_vault_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = DeepImperialNavy,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isTa) "ஆவணப் பெட்டகம்" else "Evidence Vault",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepImperialNavy
                                    )
                                )
                                Text(
                                    text = if (isTa) "சான்றுகளைப் பதிவேற்று" else "Secure Apostilles & FRRO",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = OnSandstoneSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar Filter
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (isTa) "பிரிவு 3, OCI, பாஸ்போர்ட் விதியைத் தேடுக..." else "Search Section 3, OCI, Naturalization, Passport...",
                            style = MaterialTheme.typography.bodySmall.copy(color = OnSandstoneSurfaceVariant)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = DeepImperialNavy
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Text("✕", color = OnSandstoneSurfaceVariant)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("citizenship_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = DeepImperialNavy,
                        unfocusedBorderColor = ParchmentOutline,
                        focusedTextColor = OnParchmentText,
                        unfocusedTextColor = OnParchmentText,
                        focusedPlaceholderColor = OnSandstoneSurfaceVariant,
                        unfocusedPlaceholderColor = OnSandstoneSurfaceVariant
                    )
                )
            }

            // Category Filter Pills
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val pills = listOf(
                        "ALL" to (if (isTa) "அனைத்தும்" else "All Grounds"),
                        "CITIZENSHIP" to (if (isTa) "குடியுரிமை" else "Citizenship 1955"),
                        "OCI_DUAL" to (if (isTa) "OCI & விதிகள்" else "OCI & Art 9"),
                        "PASSPORT" to (if (isTa) "பாஸ்போர்ட்" else "Passports 1967")
                    )

                    pills.forEach { (catKey, label) ->
                        FilterChip(
                            selected = selectedTabCategory == catKey,
                            onClick = { selectedTabCategory = catKey },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepImperialNavy,
                                selectedLabelColor = OnImperialNavy
                            ),
                            modifier = Modifier.testTag("citizenship_filter_$catKey")
                        )
                    }
                }
            }

            // Statutory Provision Cards
            items(filteredProvisions) { provision ->
                StatutoryProvisionCard(
                    provision = provision,
                    isTa = isTa
                )
            }

            // Interactive Document Checklist Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SandstoneSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentOutline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("citizenship_documents_checklist_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = OnSageHerbSuccess,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTa) "தேவையான ஆவணங்கள் சரிபார்ப்புப் பட்டியல்" else "Statutory Documentation Checklist",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepImperialNavy
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val docs = listOf(
                            "doc_birth_cert" to (if (isTa) "முனிசிபல் பிறப்புச் சான்றிதழ் (RBD Act 1969)" else "Municipal Birth Certificate (Under RBD Act 1969)"),
                            "doc_parents_domicile" to (if (isTa) "பெற்றோரின் இந்திய பாஸ்போர்ட் / இருப்பிடச் சான்று" else "Parents' Indian Passport & Domicile Proof"),
                            "doc_passport_copies" to (if (isTa) "தற்போதைய பாஸ்போர்ட் & பழைய பாஸ்போர்ட்டுகளின் நகல்" else "Certified copies of Current & Expired Passports"),
                            "doc_surrender_cert" to (if (isTa) "வெளிநாட்டு குடியுரிமை ஒப்படைப்புச் சான்றிதழ்" else "Surrender Certificate / Renunciation of Nationality"),
                            "doc_frro_residential" to (if (isTa) "FRRO வசிப்பிட அனுமதி & விசா நீட்டிப்பு உத்தரவு" else "FRRO Residential Permit & Valid Indian Visa"),
                            "doc_apostille" to (if (isTa) "ஹேக் மாநாட்டு அபோஸ்டில் சான்றளிக்கப்பட்ட ஆவணங்கள்" else "Hague Apostille authenticated overseas records")
                        )

                        docs.forEach { (docKey, label) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        documentChecklist[docKey] = !(documentChecklist[docKey] ?: false)
                                    }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = documentChecklist[docKey] == true,
                                    onCheckedChange = { isChecked ->
                                        documentChecklist[docKey] = isChecked
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = DeepImperialNavy)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        color = OnParchmentText
                                    )
                                )
                            }
                        }

                        val completedCount = documentChecklist.values.count { it }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isTa) "$completedCount / ${docs.size} ஆவணங்கள் சரிபார்க்கப்பட்டன" else "$completedCount of ${docs.size} documents validated",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (completedCount == docs.size) OnSageHerbSuccess else WarmTerracotta
                            )
                        )
                    }
                }
            }

            // Consular Grievance Redressal (MADAD / CPV Division)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = DeepImperialNavy,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTa) "மத்திய வெளியுறவு அமைச்சக புகார் உதவி (MADAD)" else "Consular Assistance & MADAD Portal",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepImperialNavy
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isTa) {
                                "வெளிநாட்டில் உள்ள இந்தியர்கள் மற்றும் பாஸ்போர்ட் சிக்கல்களுக்கு, மத்திய வெளியுறவு அமைச்சகத்தின் MADAD இணையதளம் வழியாக நேரடியாக புகார் அளிக்கலாம்."
                            } else {
                                "For stranded NRIs, passport delays, or consular grievance redressal abroad, file official petitions directly on the MEA MADAD Portal (Consular Services Management System)."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSandstoneSurfaceVariant,
                                fontSize = 12.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                com.example.utils.ActionUtils.openWebUrl(context, "https://madad.gov.in")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepImperialNavy),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("citizenship_open_madad_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInBrowser,
                                contentDescription = null,
                                tint = OnImperialNavy,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open MEA MADAD Portal", fontSize = 12.sp)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun StatutoryProvisionCard(
    provision: StatutoryProvision,
    isTa: Boolean,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentOutline),
        modifier = modifier
            .fillMaxWidth()
            .testTag("statutory_provision_${provision.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PrimaryNavyContainer
                ) {
                    Text(
                        text = provision.sectionNumber,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepImperialNavy
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = provision.actName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        color = OnSandstoneSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isTa) provision.titleTa else provision.titleEn,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = DeepImperialNavy
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isTa) provision.summaryTa else provision.summaryEn,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = OnParchmentText,
                    lineHeight = 17.sp
                )
            )

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = ParchmentOutline)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Procedural Remedy Box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SandstoneSurface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "⚖️ ${if (isTa) "சட்டப்பூர்வ தீர்வு & நடைமுறை:" else "Procedural Remedy & Forum:"}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepImperialNavy
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isTa) provision.proceduralRemedyTa else provision.proceduralRemedyEn,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = OnParchmentText
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Landmark Precedent
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = null,
                            tint = WarmTerracotta,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Landmark Judgment: ${provision.landmarkPrecedent}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                fontSize = 11.sp,
                                color = DeepImperialNavy
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Expand / Collapse Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) (if (isTa) "குறைவாகக் காட்டு" else "Show Less") else (if (isTa) "நடைமுறை விவரங்கள்" else "Procedural Details"),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = WarmTerracotta
                    )
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = WarmTerracotta,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
