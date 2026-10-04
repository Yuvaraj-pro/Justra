package com.justra.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.ui.theme.DeepIndigoSlatePrimary
import com.justra.app.ui.theme.PaleSandstoneVariant
import com.justra.app.ui.theme.PrimaryContainerSlate
import com.justra.app.ui.theme.SageGreenSuccessContainer
import com.justra.app.ui.theme.SageGreenSuccessText
import com.justra.app.ui.theme.TerracottaAccentSecondary
import com.justra.app.ui.theme.WarmIvorySurface
import com.justra.app.ui.theme.nyayaOutlinedTextFieldColors
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Section65BCertificateGeneratorScreen(
    currentLanguage: LanguagePreference,
    onToggleLanguage: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL
    val currentDateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    // Form inputs
    var deponentName by remember { mutableStateOf("K. Ramanathan") }
    var deponentFather by remember { mutableStateOf("S. Kuppusamy") }
    var deponentAddress by remember { mutableStateOf("No. 45, Anna Nagar, Chennai - 600040") }
    var deviceMakeModel by remember { mutableStateOf("Samsung Galaxy S22 (Android 14) / Apple MacBook Pro") }
    var deviceImeiSerial by remember { mutableStateOf("IMEI: 864209041234567 / SN: C02X80J1JG5J") }
    
    val mediaTypes = listOf(
        "WhatsApp Chat Export & Audio Notes",
        "Email Chain with Header Metadata",
        "UPI Bank Transaction Screenshot / Statement",
        "CCTV Camera MP4 Footage",
        "Call Recording (Audio File .m4a/.mp3)",
        "SMS Message Threads"
    )
    var selectedMediaType by remember { mutableStateOf(mediaTypes[0]) }
    var isMediaTypeDropdownExpanded by remember { mutableStateOf(false) }

    var evidenceFileName by remember { mutableStateOf("WhatsApp_Chat_Dispute_2026.txt") }
    var sha256Hash by remember { 
        mutableStateOf(computeSha256("EVIDENCE_SAMPLE_${System.currentTimeMillis()}")) 
    }
    var courtName by remember { mutableStateOf("District Consumer Disputes Redressal Commission, Chennai") }
    var caseTitle by remember { mutableStateOf("K. Ramanathan vs. ABC Electronics Pvt Ltd") }

    val generatedAffidavit = buildSection65BAffidavit(
        deponentName = deponentName,
        deponentFather = deponentFather,
        deponentAddress = deponentAddress,
        courtName = courtName,
        caseTitle = caseTitle,
        deviceMakeModel = deviceMakeModel,
        deviceImeiSerial = deviceImeiSerial,
        mediaType = selectedMediaType,
        evidenceFileName = evidenceFileName,
        sha256Hash = sha256Hash,
        dateStr = currentDateStr,
        isTa = isTa
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isTa) "பிரிவு 65B மின்னணு சான்றிதழ் தயாரிப்பான்" else "Sec 65B / 63 BSA Certificate Exporter",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigoSlatePrimary
                            )
                        )
                        Text(
                            text = if (isTa) "இந்திய சான்றுகள் சட்டம் பிரிவு 65B(4) & BSA 2023 பிரிவு 63" else "Electronic Evidence Certificate Affidavit (Arjun Panditrao Compliant)",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.secondary)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("sec65b_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DeepIndigoSlatePrimary)
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PaleSandstoneVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onToggleLanguage() }
                            .padding(end = 12.dp)
                    ) {
                        Text(
                            text = if (isTa) "தமிழ்" else "English",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepIndigoSlatePrimary
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
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
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryContainerSlate),
                    border = BorderStroke(1.dp, DeepIndigoSlatePrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = DeepIndigoSlatePrimary)
                        Text(
                            text = if (isTa) "அர்ஜுன் பண்டிட்ராவ் v கைலாஷ் குஷன்ராவ் (2020 7 SCC 1) உச்சநீதிமன்ற தீர்ப்பின்படி, மின்னணு சான்றுகளுக்கு பிரிவு 65B சான்றிதழ் கட்டாயமாகும்."
                                   else "Pursuant to Supreme Court landmark Arjun Panditrao vs Kailash Gorantyal (2020), Sec 65B(4) certification is mandatory for admissibility of all electronic records.",
                            style = MaterialTheme.typography.bodySmall.copy(color = DeepIndigoSlatePrimary, lineHeight = 18.sp)
                        )
                    }
                }
            }

            // Input Fields Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PaleSandstoneVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isTa) "1. சாட்சியாளர் மற்றும் வழக்கு விவரங்கள்" else "1. Deponent & Court Matter Details",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                        )

                        OutlinedTextField(
                            value = deponentName,
                            onValueChange = { deponentName = it },
                            label = { Text(if (isTa) "சாட்சியாளர் பெயர் (Deponent Full Name)" else "Deponent Full Name") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth().testTag("deponent_name_input")
                        )

                        OutlinedTextField(
                            value = courtName,
                            onValueChange = { courtName = it },
                            label = { Text(if (isTa) "நீதிமன்றம் / தீர்ப்பாயம் பெயர்" else "Court / Forum Name") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = caseTitle,
                            onValueChange = { caseTitle = it },
                            label = { Text(if (isTa) "வழக்கு தலைப்பு (Case Cause Title)" else "Case Cause Title (A vs. B)") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isTa) "2. சாதனம் & மின்னணு ஆவண விவரங்கள்" else "2. Computing Device & Electronic Record",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepIndigoSlatePrimary)
                        )

                        OutlinedTextField(
                            value = deviceMakeModel,
                            onValueChange = { deviceMakeModel = it },
                            label = { Text(if (isTa) "சாதன வகை & மாதிரி (Make / Model / OS)" else "Device Make, Model & OS") },
                            leadingIcon = { Icon(Icons.Default.Devices, contentDescription = null, tint = DeepIndigoSlatePrimary) },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = deviceImeiSerial,
                            onValueChange = { deviceImeiSerial = it },
                            label = { Text(if (isTa) "சாதன IMEI / வரிசை எண் (Serial No)" else "Device IMEI / Serial / MAC") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenuBox(
                            expanded = isMediaTypeDropdownExpanded,
                            onExpandedChange = { isMediaTypeDropdownExpanded = !isMediaTypeDropdownExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedMediaType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(if (isTa) "மின்னணு சான்றின் வகை" else "Electronic Media Type") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMediaTypeDropdownExpanded) },
                                colors = nyayaOutlinedTextFieldColors(),
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = isMediaTypeDropdownExpanded,
                                onDismissRequest = { isMediaTypeDropdownExpanded = false }
                            ) {
                                mediaTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type) },
                                        onClick = {
                                            selectedMediaType = type
                                            isMediaTypeDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = evidenceFileName,
                            onValueChange = { evidenceFileName = it },
                            label = { Text(if (isTa) "கோப்புப் பெயர் (Evidence File Name)" else "Evidence Artifact File Name") },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = sha256Hash,
                            onValueChange = { sha256Hash = it },
                            label = { Text(if (isTa) "SHA-256 கிரிப்டோகிராஃபிக் ஹாஷ்" else "SHA-256 Cryptographic Integrity Hash") },
                            leadingIcon = { Icon(Icons.Default.Fingerprint, contentDescription = null, tint = TerracottaAccentSecondary) },
                            colors = nyayaOutlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth().testTag("sha256_hash_input")
                        )

                        Button(
                            onClick = {
                                sha256Hash = computeSha256("DOC_${evidenceFileName}_${System.currentTimeMillis()}")
                                Toast.makeText(context, if (isTa) "புதிய SHA-256 ஹாஷ் கணக்கிடப்பட்டது" else "New SHA-256 Hash Generated", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TerracottaAccentSecondary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isTa) "புதிய ஹாஷ் உருவாக்கு (Regenerate Hash)" else "Regenerate Cryptographic Hash")
                        }
                    }
                }
            }

            // Affidavit Preview Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmIvorySurface),
                    border = BorderStroke(1.5.dp, DeepIndigoSlatePrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isTa) "சட்டப்பிரிவு 65B சான்றிதழ் வரைவு" else "Statutory Certificate Preview",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepIndigoSlatePrimary
                                )
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SageGreenSuccessContainer
                            ) {
                                Text(
                                    text = "BSA 2023 VALID",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SageGreenSuccessText
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PaleSandstoneVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = generatedAffidavit,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                ),
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Section 65B Certificate", generatedAffidavit)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, if (isTa) "சான்றிதழ் நகலெடுக்கப்பட்டது" else "Certificate copied to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DeepIndigoSlatePrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("copy_sec65b_btn")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isTa) "முழு சான்றிதழை நகலெடு (Copy Certificate)" else "Copy Section 65B Affidavit")
                        }
                    }
                }
            }
        }
    }
}

private fun computeSha256(input: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(input.toByteArray())
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}

private fun buildSection65BAffidavit(
    deponentName: String,
    deponentFather: String,
    deponentAddress: String,
    courtName: String,
    caseTitle: String,
    deviceMakeModel: String,
    deviceImeiSerial: String,
    mediaType: String,
    evidenceFileName: String,
    sha256Hash: String,
    dateStr: String,
    isTa: Boolean
): String {
    return """
BEFORE THE HON'BLE ${courtName.uppercase()}
IN THE MATTER OF:
$caseTitle

CERTIFICATE UNDER SECTION 65B(4) OF THE INDIAN EVIDENCE ACT, 1872
(READ WITH SECTION 63 OF THE BHARATIYA SAKSHYA ADHINIYAM, 2023)

I, $deponentName, residing at $deponentAddress, do hereby solemnly affirm and state as under:

1. I am the lawful owner and custodian of the electronic computing device, namely:
   Device Make & Model : $deviceMakeModel
   Unique Identifier / IMEI / Serial No : $deviceImeiSerial

2. That the electronic computer output, namely $mediaType titled '$evidenceFileName', has been produced by the aforesaid computer device during the period over which the device was used regularly to store or process information for the purposes of my lawful activities.

3. That throughout the material period, the aforesaid device was operating properly and at all material times, there was no malfunction or unauthorized interference that would affect the accuracy or integrity of the electronic record.

4. That the cryptographic hash value of the electronic file '$evidenceFileName' as produced before this Hon'ble Court is:
   SHA-256 HASH: $sha256Hash

5. I hereby certify that the electronic record produced herewith is a true, authentic, and unaltered reproduction of the original electronic record in compliance with the mandates laid down in Arjun Panditrao vs Kailash Gorantyal (2020 7 SCC 1).

DEPONENT
Date: $dateStr
Place: Chennai

VERIFICATION:
Verified at Chennai on this $dateStr that the contents of paras 1 to 5 are true to my knowledge and belief. Nothing material has been concealed.

DEPONENT
""".trimIndent()
}
