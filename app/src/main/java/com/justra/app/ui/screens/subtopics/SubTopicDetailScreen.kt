package com.justra.app.ui.screens.subtopics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
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
import com.justra.app.ui.theme.AccentTerracotta
import com.justra.app.ui.theme.CardBorderStroke
import com.justra.app.ui.theme.NavySurface
import com.justra.app.ui.theme.SandstoneCard
import com.justra.app.ui.theme.SoftNavyContainer
import com.justra.app.ui.theme.SovereignNavy
import com.justra.app.ui.theme.TextOnSageGreen
import com.justra.app.ui.theme.TextPrimaryDark
import com.justra.app.ui.theme.TextSecondaryDark
import com.justra.app.ui.theme.VerifiedSageGreen
import com.justra.app.ui.theme.WarmCanvasBg
import com.justra.app.utils.ActionUtils

data class SubTopicInfo(
    val key: String,
    val titleEn: String,
    val titleTa: String,
    val subtitleEn: String,
    val subtitleTa: String,
    val primaryActs: List<String>,
    val overviewEn: String,
    val overviewTa: String,
    val citizenRightsEn: List<String>,
    val citizenRightsTa: List<String>,
    val requiredDocumentsEn: List<String>,
    val requiredDocumentsTa: List<String>,
    val officialPortalUrl: String,
    val officialPortalName: String,
    val helplineNumber: String,
    val helplineName: String
)

object SubTopicRegistry {
    fun getSubTopic(key: String): SubTopicInfo {
        val normalized = key.lowercase().trim()
        return when {
            normalized.contains("citizenship") || normalized.contains("passport") -> SubTopicInfo(
                key = "citizenship",
                titleEn = "Citizenship & Passport Laws",
                titleTa = "குடியுரிமை மற்றும் கடவுச்சீட்டு சட்டங்கள்",
                subtitleEn = "Citizenship Act 1955 & Passports Act 1967",
                subtitleTa = "குடியுரிமைச் சட்டம் 1955 & கடவுச்சீட்டு சட்டம் 1967",
                primaryActs = listOf(
                    "Citizenship Act, 1955 (Section 3 - Citizenship by Birth, Section 5 - Registration)",
                    "Passports Act, 1967 (Section 5 - Application, Section 6 - Grounds of Refusal, Section 10 - Impounding)",
                    "Passports Rules, 1980 (Police Verification & Expedited Tatkaal Processing)"
                ),
                overviewEn = "Governs statutory citizenship acquisition, renunciation, verification procedures, and mandatory statutory timelines for passport issuance or renewal under the Ministry of External Affairs.",
                overviewTa = "இந்தியக் குடியுரிமைப் பெறுதல், கடவுச்சீட்டு விண்ணப்பம், காவல் சரிபார்ப்பு மற்றும் தாமதங்கள் மீதான சட்டப்பூர்வ உரிமைகள்.",
                citizenRightsEn = listOf(
                    "Right to receive written grounds if passport application is refused or delayed under Section 6(2)",
                    "Right to statutory appeal under Section 11 of the Passports Act before the Chief Passport Officer",
                    "Protection against arbitrary impounding without prior show-cause notice",
                    "Right to expedited Tatkaal issuance upon meeting urgent verification criteria"
                ),
                citizenRightsTa = listOf(
                    "பிரிவு 6(2)-ன் கீழ் கடவுச்சீட்டு மறுக்கப்பட்டால் அதற்கான காரணத்தை எழுத்துப்பூர்வமாகப் பெறும் உரிமை",
                    "பிரிவு 11-ன் கீழ் முதன்மை கடவுச்சீட்டு அதிகாரியிடம் சட்டப்பூர்வ மேல்முறையீடு செய்யும் உரிமை",
                    "முன்னறிவிப்பின்றி கடவுச்சீட்டைப் பறிமுதல் செய்வதிலிருந்து பாதுகாப்பு",
                    "அவசர தேவைக்கு தட்கால் முறையில் விரைவாகப் பாஸ்போர்ட் பெறும் உரிமை"
                ),
                requiredDocumentsEn = listOf(
                    "Birth Certificate or Proof of Date of Birth",
                    "Aadhaar Card or Electoral Photo Identity Card (EPIC)",
                    "Standard Affidavit Annexure-E for Indian Citizen declaration",
                    "Proof of Present Residential Address (Utility Bill or Bank Passbook)",
                    "Old Passport copy (for reissue or renewal cases)"
                ),
                requiredDocumentsTa = listOf(
                    "பிறப்புச் சான்றிதழ் அல்லது பிறந்த தேதி ஆதாரம்",
                    "ஆதார் அட்டை அல்லது வாக்காளர் அடையாள அட்டை",
                    "இந்திய குடிமகன் பிரகடன படிவம் (Annexure-E)",
                    "தற்போதைய வசிப்பிட முகவரிச் சான்று",
                    "பழைய பாஸ்போர்ட் நகல் (புதுப்பித்தல் என்றால்)"
                ),
                officialPortalUrl = "https://www.passportindia.gov.in",
                officialPortalName = "Passport Seva Portal (passportindia.gov.in)",
                helplineNumber = "18002581800",
                helplineName = "National Passport Seva Desk (1800-258-1800)"
            )

            normalized.contains("tenancy") || normalized.contains("rent") -> SubTopicInfo(
                key = "tenancy",
                titleEn = "Tenancy & Eviction Dispute Laws",
                titleTa = "வாடகை மற்றும் வெளியேற்ற சட்டங்கள்",
                subtitleEn = "Model Tenancy Act & State Rent Control Legislation",
                subtitleTa = "மாதிரி வாடகைச் சட்டம் & மாநில வாடகைக் கட்டுப்பாட்டு சட்டம்",
                primaryActs = listOf(
                    "Model Tenancy Act, 2021 (Section 4 - Mandatory Tenancy Agreement, Section 11 - Security Deposit Caps)",
                    "Tamil Nadu Regulation of Rights and Responsibilities of Landlords and Tenants Act, 2017",
                    "Transfer of Property Act, 1882 (Section 106 - Mandatory Notice to Quit)"
                ),
                overviewEn = "Establishes clear statutory rights for residential and commercial tenants, restricting arbitrary rent hikes, limiting security deposits to maximum 2 months, and forbidding unlawful self-help evictions.",
                overviewTa = "வாடகைதாரர் உரிமைகள், அத்துமீறிய வெளியேற்றத் தடை, முன்தொகை உச்சவரம்பு மற்றும் வாடகை நீதிமன்ற நடைமுறைகள்.",
                citizenRightsEn = listOf(
                    "Maximum security deposit capped at two months' rent for residential premises",
                    "Prohibition of essential utility cutoff (water/electricity) during tenancy disputes under Section 20",
                    "Mandatory 24-hour advance written notice before landlord inspections",
                    "Right to approach the Rent Court / Rent Authority for wrongful deposit withholding"
                ),
                citizenRightsTa = listOf(
                    "குடியிருப்புக்கு அதிகபட்சம் 2 மாத வாடகை மட்டுமே முன்தொகையாகப் பெற வேண்டும்",
                    "தகராறு காரணமாக தண்ணீர், மின்சாரம் துண்டிக்கப்படுவது சட்டப்படி தடைசெய்யப்பட்டுள்ளது",
                    "வீட்டை ஆய்வு செய்ய வரும் முன் 24 மணி நேர முன்னறிவிப்பு தேவை",
                    "முன்தொகையைத் திருப்பித் தராத போது வாடகை நீதிமன்றத்தை அணுகும் உரிமை"
                ),
                requiredDocumentsEn = listOf(
                    "Registered or Stamped Tenancy / Lease Agreement",
                    "Rent Payment Receipts / Bank UPI Transfer Records",
                    "Security Deposit Transaction Receipt / Proof of Transfer",
                    "Landlord Correspondence (WhatsApp / Email / Letter notices)",
                    "Electricity / Water Meter reading proof"
                ),
                requiredDocumentsTa = listOf(
                    "பதிவு செய்யப்பட்ட வாடகை ஒப்பந்தம்",
                    "வாடகை செலுத்தியதற்கான வங்கி அல்லது யுபிஐ ரசீதுகள்",
                    "முன்தொகை கொடுத்ததற்கான வங்கி ஆதாரங்கள்",
                    "வீட்டு உரிமையாளர் தகவல் தொடர்பு (WhatsApp/மின்னஞ்சல்/கடிதம்)",
                    "மின் மற்றும் குடிநீர் பயன்பாட்டு ரசீதுகள்"
                ),
                officialPortalUrl = "https://tenancy.tn.gov.in",
                officialPortalName = "Rent Authority Portal (tenancy.tn.gov.in)",
                helplineNumber = "1100",
                helplineName = "State Grievance Helpline (1100)"
            )

            normalized.contains("labor") || normalized.contains("employment") || normalized.contains("salary") -> SubTopicInfo(
                key = "labor",
                titleEn = "Labor Rights & Wage Recovery Laws",
                titleTa = "தொழிலாளர் உரிமைகள் மற்றும் ஊதிய மீட்பு சட்டங்கள்",
                subtitleEn = "Payment of Wages Act 1936 & Industrial Disputes Act 1947",
                subtitleTa = "ஊதியம் வழங்கல் சட்டம் 1936 & தொழிற்தகராறுகள் சட்டம் 1947",
                primaryActs = listOf(
                    "Payment of Wages Act, 1936 (Section 15 - Claims Arising Out of Deductions or Delay in Wages)",
                    "Industrial Disputes Act, 1947 (Section 25F - Conditions Precedent to Retrenchment)",
                    "Code on Wages, 2019 (Mandatory Timely Payment by 7th/10th of succeeding month)"
                ),
                overviewEn = "Protects employees and workers against unauthorized salary deductions, delayed monthly wages, non-payment of statutory gratuity/PF, and wrongful termination without due notice.",
                overviewTa = "சட்டவிரோத சம்பள பிடித்தம், தாமதமான ஊதியம், பணிநீக்க நிவாரணம் மற்றும் தொழிலாளர் ஆணையர் குறைதீர்ப்பு.",
                citizenRightsEn = listOf(
                    "Right to receive full earned salary on or before the statutory due date without arbitrary deductions",
                    "Mandatory 30 days notice or pay in lieu thereof prior to termination for non-misconduct cases",
                    "Right to recover delayed wages with up to 10x statutory compensation under Section 15(3)",
                    "Right to file conciliation claims before the District Labour Commissioner via Samadhan Portal"
                ),
                citizenRightsTa = listOf(
                    "சட்டப்பூர்வ காலத்திற்குள் சம்பளம் பெறும் உரிமை",
                    "காரணமின்றி பணிநீக்கம் செய்தால் 30 நாள் முன்னறிவிப்பு அல்லது ஊதியம் பெறும் உரிமை",
                    "தாமதமான ஊதியத்திற்கு 10 மடங்கு வரை கூடுதல் இழப்பீடு கோரும் உரிமை",
                    "சமாதான் போர்டல் மூலம் தொழிலாளர் ஆணையரிடம் சமரச மனு தாக்கல் செய்யும் உரிமை"
                ),
                requiredDocumentsEn = listOf(
                    "Offer Letter / Employment Contract / Appointment Letter",
                    "Bank Account Statement showing salary credits or lack thereof",
                    "Official Pay Slips / Form 16 / Salary Certificates",
                    "Attendance records / Biometric logs / Official company emails",
                    "Formal written grievance / demand notice sent to HR/Management"
                ),
                requiredDocumentsTa = listOf(
                    "பணி நியமன ஆணை (Appointment Letter / Offer Letter)",
                    "சம்பளம் வராததைக் காட்டும் வங்கி கணக்கு அறிக்கை",
                    "சம்பள சீட்டுகள் (Pay Slips / Form 16)",
                    "பணி வருகைப் பதிவு மற்றும் நிறுவன மின்னஞ்சல்கள்",
                    "நிறுவனத்திற்கு அனுப்பிய ஊதியக் கோரிக்கை கடிதம்"
                ),
                officialPortalUrl = "https://samadhan.labour.gov.in",
                officialPortalName = "Ministry of Labour - Samadhan Portal (samadhan.labour.gov.in)",
                helplineNumber = "155214",
                helplineName = "National Shram Suvidha Helpline (155214)"
            )

            normalized.contains("cyber") || normalized.contains("fraud") -> SubTopicInfo(
                key = "cyber",
                titleEn = "Cyber Crime & Financial Fraud Laws",
                titleTa = "சைபர் குற்றங்கள் மற்றும் நிதி மோசடி சட்டங்கள்",
                subtitleEn = "Information Technology Act 2000 & Bharatiya Nyaya Sanhita 2023",
                subtitleTa = "தகவல் தொழில்நுட்பச் சட்டம் 2000 & பாரதிய நியாய சன்ஹிதா 2023",
                primaryActs = listOf(
                    "Information Technology Act, 2000 (Section 43, 66C - Identity Theft, 66D - Cheating by Personation)",
                    "Bharatiya Nyaya Sanhita, 2023 (Section 318 - Cheating & Dishonestly Inducing Delivery of Property)",
                    "RBI Master Directions (Zero Liability for Citizen upon Reporting Fraud within 72 Hours)"
                ),
                overviewEn = "Comprehensive legal recourse for citizens facing unauthorized UPI debits, phishing scams, SIM swapping, extortion calls, and cyber harassment with immediate bank account lien freezing via 1930.",
                overviewTa = "யுபிஐ வங்கி மோசடி, ஆள்மாறாட்டம், ஃபிஷிங் மற்றும் சைபர் குற்றங்களில் உடனடி வங்கிக் கணக்கு முடக்கம் மற்றும் பணம் மீட்பு.",
                citizenRightsEn = listOf(
                    "Zero financial liability if unauthorized banking debit is reported within 3 days under RBI guidelines",
                    "Immediate flagging and debit freeze across beneficiary mule accounts via 1930 Helpline",
                    "Right to have a Zero FIR registered at any police station or Cyber Cell under Section 173 BNSS",
                    "Right to prompt takedown of impersonation or non-consensual imagery under Section 67"
                ),
                citizenRightsTa = listOf(
                    "வங்கி மோசடியை 72 மணி நேரத்திற்குள் புகாரளித்தால் முழுப் பணப் பாதுகாப்பு (RBI உத்தரவு)",
                    "1930 அவசர எண் மூலம் மோசடி கணக்கை உடனடியாக முடக்கும் உரிமை",
                    "எந்த காவல் நிலையத்திலும் ஜீரோ எஃப்.ஐ.ஆர் பதிவு செய்யும் சட்ட உரிமை",
                    "போலி அல்லது அவதூறு கணக்குகளை இணையத்திலிருந்து அகற்றக் கோரும் உரிமை"
                ),
                requiredDocumentsEn = listOf(
                    "Bank Statement highlighting fraudulent transaction UTR/Reference number",
                    "Screenshots of fraudulent SMS, WhatsApp chat, or phishing website",
                    "Call log recording exact timestamps of scam caller",
                    "Complaint acknowledgment receipt from Bank / Payment Gateway",
                    "Copy of National Cybercrime Portal Acknowledgement Number"
                ),
                requiredDocumentsTa = listOf(
                    "மோசடி பரிவர்த்தனை எண் (UTR/Ref No) கொண்ட வங்கி அறிக்கை",
                    "மோசடி குறுஞ்செய்தி மற்றும் WhatsApp உரையாடல் திரைப்படங்கள்",
                    "மோசடி அழைப்பு வந்த தொலைபேசி எண் மற்றும் நேரப் பதிவு",
                    "வங்கிக்குக் கொடுத்த புகார் ரசீது",
                    "தேசிய சைபர் குற்ற போர்டல் ஒப்புகை சீட்டு"
                ),
                officialPortalUrl = "https://cybercrime.gov.in",
                officialPortalName = "National Cyber Crime Portal (cybercrime.gov.in)",
                helplineNumber = "1930",
                helplineName = "Citizen Financial Cyber Fraud Helpline (1930)"
            )

            normalized.contains("consumer") || normalized.contains("ecommerce") -> SubTopicInfo(
                key = "consumer",
                titleEn = "Consumer Protection & Fair Trade Laws",
                titleTa = "நுகர்வோர் பாதுகாப்பு மற்றும் நியாய வர்த்தக சட்டங்கள்",
                subtitleEn = "Consumer Protection Act, 2019 & E-Commerce Rules",
                subtitleTa = "நுகர்வோர் பாதுகாப்புச் சட்டம் 2019 & மின்-வர்த்தக விதிகள்",
                primaryActs = listOf(
                    "Consumer Protection Act, 2019 (Section 2(7) - Definition of Consumer, Section 35 - District Commission Jurisdiction)",
                    "Consumer Protection (E-Commerce) Rules, 2020 (Mandatory 48-Hour Acknowledgment & 1-Month Resolution)",
                    "Legal Metrology Act, 2009 (Prevention of Overcharging Above Maximum Retail Price)"
                ),
                overviewEn = "Empowers consumers against unfair trade practices, defective merchandise, refusal of warranty service, misleading advertisements, and non-delivery from e-commerce sellers.",
                overviewTa = "குறைபாடுள்ள பொருட்கள், வாரண்டி மறுப்பு, ஏமாற்று விளம்பரங்கள் மற்றும் இணைய வர்த்தக மோசடிகளுக்கு நுகர்வோர் நீதிமன்ற நிவாரணம்.",
                citizenRightsEn = listOf(
                    "Right to replacement, refund with interest, and statutory compensation for mental agony",
                    "Right to file consumer complaints digitally from anywhere via the e-Daakhil portal",
                    "Product liability action against manufacturer and seller for injury or damage under Section 83",
                    "Freedom from unfair contract clauses such as mandatory forfeiture of payments"
                ),
                citizenRightsTa = listOf(
                    "பொருளை மாற்றித் தருதல், வட்டியுடன் பணத்தைத் திரும்பப் பெறுதல் மற்றும் மன உளைச்சலுக்கு இழப்பீடு",
                    "இ-தாகில் போர்டல் மூலம் எங்கிருந்தும் இணைய வழியில் வழக்கு தாக்கல் செய்யும் உரிமை",
                    "பொருளினால் ஏற்படும் சேதத்திற்கு உற்பத்தியாளர் மீதான இழப்பீட்டு வழக்கு உரிமை",
                    "நியாயமற்ற ஒருதலைப்பட்ச ஒப்பந்த விதிகளிலிருந்து விடுதலை"
                ),
                requiredDocumentsEn = listOf(
                    "Tax Invoice / Purchase Bill / Payment Receipt",
                    "Warranty Card / Service Agreement documentation",
                    "Photographs / Video evidence of the defective product or damaged package",
                    "Customer Support Ticket ID and email correspondence records",
                    "Legal Notice copy served to company or seller"
                ),
                requiredDocumentsTa = listOf(
                    "பொருள் வாங்கிய ரசீது / ஜிஎஸ்டி பில் (Invoice)",
                    "வாரண்டி அட்டை அல்லது சேவை ஒப்பந்தம்",
                    "பழுதடைந்த பொருள் அல்லது சேதத்தின் புகைப்படங்கள்/வீடியோ",
                    "வாடிக்கையாளர் சேவை மின்னஞ்சல் மற்றும் புகார் எண்",
                    "நிறுவனத்திற்கு அனுப்பிய சட்ட அறிவிப்பு நகல்"
                ),
                officialPortalUrl = "https://edaakhil.nic.in",
                officialPortalName = "National e-Daakhil Portal (edaakhil.nic.in)",
                helplineNumber = "1915",
                helplineName = "National Consumer Helpline (NCH 1915)"
            )

            normalized.contains("rti") -> SubTopicInfo(
                key = "rti",
                titleEn = "Right to Information (RTI) Laws",
                titleTa = "தகவல் அறியும் உரிமைச் சட்டம் (RTI)",
                subtitleEn = "Right to Information Act, 2005",
                subtitleTa = "தகவல் அறியும் உரிமைச் சட்டம், 2005",
                primaryActs = listOf(
                    "Right to Information Act, 2005 (Section 6 - Application for Information, Section 7 - 30-Day Mandatory Disposal)",
                    "Section 19 - First Appeal within 30 Days & Second Appeal to Information Commission",
                    "Section 20 - Penalties of Rs. 250 per day up to Rs. 25,000 on Errant Public Information Officers (PIO)"
                ),
                overviewEn = "Enables citizens to seek public records, inspect government works, obtain certified copies, and hold public authorities accountable with mandatory 30-day statutory deadlines.",
                overviewTa = "அரசு அலுவலகங்களிலிருந்து ஆவணங்கள், திட்டங்கள், செலவினங்கள் பற்றிய அதிகாரப்பூர்வ தகவல்களை 30 நாட்களில் பெறும் சட்டம்.",
                citizenRightsEn = listOf(
                    "Right to inspect works, documents, and records of public authorities",
                    "Mandatory receipt of information within 30 days (or 48 hours for life and liberty matters)",
                    "Statutory right to first and second appeal if information is rejected or delayed",
                    "Exemption from providing reasons for seeking public information under Section 6(2)"
                ),
                citizenRightsTa = listOf(
                    "அரசு ஆவணங்கள், அலுவலக கோப்புகள் மற்றும் பணிகளை நேரில் ஆய்வு செய்யும் உரிமை",
                    "30 நாட்களுக்குள் தகவல் பெற உரிமை (உயிர் மற்றும் சுதந்திரம் தொடர்பானவை என்றால் 48 மணி நேரத்தில்)",
                    "தகவல் மறுக்கப்பட்டால் இலவசமாக முதல் மற்றும் இரண்டாம் மேல்முறையீடு செய்யும் உரிமை",
                    "தகவல் கேட்பதற்கான காரணத்தைக் கூற தேவையில்லை (பிரிவு 6(2))"
                ),
                requiredDocumentsEn = listOf(
                    "Form-A RTI Application citing specific questions and records sought",
                    "Postal Order (IPO) or Online Fee Receipt (Rs. 10 standard fee)",
                    "Proof of BPL status (if seeking fee exemption under BPL category)",
                    "Government department address and Public Information Officer (PIO) designation",
                    "Postal tracking receipt showing delivery of application"
                ),
                requiredDocumentsTa = listOf(
                    "குறிப்பிட்ட தகவல்களைக் கோரும் RTI விண்ணப்பம்",
                    "விண்ணப்பக் கட்டண ரசீது (ரூபாய் 10)",
                    "வறுமைக் கோட்டிற்கு கீழ் உள்ளவர் என்றால் அதற்கான அட்டை நகல்",
                    "பொதுத் தகவல் அலுவலரின் முகவரி விபரம்",
                    "அஞ்சல் மூலமாக அனுப்பியதற்கான ஒப்புகை சீட்டு"
                ),
                officialPortalUrl = "https://rtionline.gov.in",
                officialPortalName = "National RTI Online Portal (rtionline.gov.in)",
                helplineNumber = "01126717355",
                helplineName = "Central Information Commission Helpline"
            )

            normalized.contains("land") || normalized.contains("property") || normalized.contains("patta") -> SubTopicInfo(
                key = "land",
                titleEn = "Land Records, Patta & Property Laws",
                titleTa = "நில ஆவணங்கள், பட்டா மற்றும் சொத்துரிமைச் சட்டங்கள்",
                subtitleEn = "Transfer of Property Act 1882 & State Revenue Codes",
                subtitleTa = "சொத்து பரிமாற்றச் சட்டம் 1882 & வருவாய் சட்டங்கள்",
                primaryActs = listOf(
                    "Transfer of Property Act, 1882 (Section 54 - Sale of Immovable Property)",
                    "Registration Act, 1908 (Section 17 - Compulsory Registration of Property Conveyances)",
                    "Real Estate (Regulation and Development) Act, 2016 (RERA Section 18 - Builder Delays & Compensation)"
                ),
                overviewEn = "Statutory provisions governing mutation of patta, verification of encumbrance certificates (EC), boundary survey disputes, and legal recourse against illegal land encroachment.",
                overviewTa = "பட்டா மாறுதல், வில்லங்க சான்றிதழ், எல்லை சர்வே தகராறுகள் மற்றும் ஆக்கிரமிப்பு தடுப்பு சட்ட உரிமைகள்.",
                citizenRightsEn = listOf(
                    "Right to access digitized land records and certified copy of Patta/Chitta online",
                    "Right to statutory survey and boundary demarcation by Taluk Tahsildar upon application",
                    "Protection against fraudulent registration under Section 77A of Registration Act",
                    "Right to demand refund with statutory SBI interest from builders under RERA Section 18"
                ),
                citizenRightsTa = listOf(
                    "பட்டா/சிட்டா மற்றும் வில்லங்கச் சான்றை இணையவழியில் இலவசமாக சரிபார்க்கும் உரிமை",
                    "வட்டாட்சியரிடம் மனு அளித்து சட்டப்பூர்வ நில அளவீடு கோரும் உரிமை",
                    "போலி ஆவண பதிவை ரத்து செய்யக் கோரும் உரிமை (பிரிவு 77A)",
                    "கட்டிட தாமதத்திற்கு RERA மூலம் வட்டியுடன் பணத்தைத் திரும்பப் பெறும் உரிமை"
                ),
                requiredDocumentsEn = listOf(
                    "Sale Deed / Title Deed / Gift Deed of Property",
                    "Encumbrance Certificate (EC) for past 30 years",
                    "Current Patta / Chitta / Revenue Tax Receipts",
                    "FMB (Field Measurement Book) sketch of survey number",
                    "Aadhaar Card and Local Municipal Tax Assessment receipts"
                ),
                requiredDocumentsTa = listOf(
                    "கிரய பத்திரம் / தாய் பத்திரம் / மூல ஆவணம்",
                    "30 ஆண்டுகால வில்லங்கச் சான்றிதழ் (EC)",
                    "தற்போதைய பட்டா / சிட்டா / நில வரி ரசீதுகள்",
                    "புல வரைபடம் (FMB Sketch)",
                    "சொத்து வரி ரசீது மற்றும் ஆதார் அட்டை"
                ),
                officialPortalUrl = "https://eservices.tn.gov.in",
                officialPortalName = "State E-Services & Patta Portal (eservices.tn.gov.in)",
                helplineNumber = "18004254732",
                helplineName = "State Land Administration Grievance Desk"
            )

            else -> SubTopicInfo(
                key = key.ifBlank { "legal_rights" },
                titleEn = "Statutory Legal Rights & Redressal",
                titleTa = "சட்டப்பூர்வ குடிமக்கள் உரிமைகள்",
                subtitleEn = "Constitution of India & Core Citizen Codes",
                subtitleTa = "இந்திய அரசியலமைப்பு மற்றும் குடிமக்கள் உரிமைகள்",
                primaryActs = listOf(
                    "Constitution of India (Article 21 - Right to Life & Liberty, Article 39A - Equal Justice & Free Legal Aid)",
                    "Legal Services Authorities Act, 1987 (Free Legal Counsel for Eligible Citizens)",
                    "Bharatiya Nagarik Suraksha Sanhita, 2023 (BNSS Section 173 - Zero FIR & Police Duties)"
                ),
                overviewEn = "Comprehensive legal protection guaranteed to every Indian citizen, ensuring free legal aid, fair hearing, equal access to justice, and speedy redressal through established statutory authorities.",
                overviewTa = "இந்திய அரசியலமைப்பு மற்றும் சட்டங்களின் கீழ் ஒவ்வொரு குடிமகனுக்கும் உறுதி செய்யப்பட்ட அடிப்படை சட்டப் பாதுகாப்பு.",
                citizenRightsEn = listOf(
                    "Right to free legal aid from NALSA / SLSA if income is below statutory limits",
                    "Right to know grounds of detention and consult a legal practitioner",
                    "Right to register Zero FIR in case of cognizable offences regardless of jurisdiction",
                    "Right to speedy grievance redressal through statutory ombudsmen"
                ),
                citizenRightsTa = listOf(
                    "நல்சா (NALSA) மூலம் இலவச சட்ட ஆலோசனை மற்றும் வழக்கறிஞர் உதவி பெறும் உரிமை",
                    "கைதுக்கான காரணங்களை அறிந்து வழக்கறிஞரை அணுகும் உரிமை",
                    "எல்லை பாராமல் எந்த காவல் நிலையத்திலும் ஜீரோ எஃப்.ஐ.ஆர் பதிவு செய்ய உரிமை",
                    "ஒம்பட்ஸ்மேன் மூலம் விரைவான நீதி பெறும் உரிமை"
                ),
                requiredDocumentsEn = listOf(
                    "Government Issued Photo ID (Aadhaar / Voter ID / Passport)",
                    "Written chronology of facts and dates of the dispute",
                    "All relevant transactional proofs, agreements, or notices",
                    "Contact details of the opposing parties",
                    "Any previous police CSR / Court petition copies"
                ),
                requiredDocumentsTa = listOf(
                    "அரசு அடையாள அட்டை (ஆதார் / வாக்காளர் அட்டை)",
                    "நிகழ்வுகளின் காலவரிசை மற்றும் உண்மை விபரம்",
                    "தொடர்புடைய ஒப்பந்தங்கள், பணப்பரிவர்த்தனை ஆவணங்கள்",
                    "எதிர் தரப்பினரின் முகவரி மற்றும் விபரம்",
                    "முந்தைய புகார் மனு அல்லது CSR நகல்"
                ),
                officialPortalUrl = "https://nalsa.gov.in",
                officialPortalName = "National Legal Services Authority (nalsa.gov.in)",
                helplineNumber = "15100",
                helplineName = "NALSA Tele-Law Helpline (15100)"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubTopicDetailScreen(
    categoryKey: String,
    currentLanguage: LanguagePreference = LanguagePreference.ENGLISH,
    onNavigateBack: () -> Unit,
    onNavigateToVoice: (categoryKey: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTa = currentLanguage == LanguagePreference.TAMIL
    val subTopic = remember(categoryKey) { SubTopicRegistry.getSubTopic(categoryKey) }

    val checkedDocuments = remember { mutableStateMapOf<Int, Boolean>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isTa) subTopic.titleTa else subTopic.titleEn,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SovereignNavy,
                                fontFamily = FontFamily.Serif
                            ),
                            maxLines = 1
                        )
                        Text(
                            text = if (isTa) subTopic.subtitleTa else subTopic.subtitleEn,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            ),
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("subtopic_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SovereignNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WarmCanvasBg
                )
            )
        },
        containerColor = WarmCanvasBg,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Statutory Banner Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SandstoneCard),
                border = BorderStroke(1.dp, CardBorderStroke),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("subtopic_header_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(SoftNavyContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = SovereignNavy,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isTa) subTopic.titleTa else subTopic.titleEn,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SovereignNavy
                                )
                            )
                            Text(
                                text = if (isTa) "அதிகாரப்பூர்வ இந்திய சட்டப்பிரிவுகள்" else "Authoritative Indian Statutory Framework",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondaryDark,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isTa) subTopic.overviewTa else subTopic.overviewEn,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimaryDark,
                            lineHeight = 20.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Primary Acts List
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        subTopic.primaryActs.forEach { act ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = WarmCanvasBg,
                                border = BorderStroke(1.dp, CardBorderStroke),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = SovereignNavy,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = act,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SovereignNavy,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section: Citizen Rights
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SandstoneCard),
                border = BorderStroke(1.dp, CardBorderStroke),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("subtopic_citizen_rights_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isTa) "உங்கள் சட்டப்பூர்வ உரிமைகள்" else "Primary Citizen Statutory Rights",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SovereignNavy
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val rights = if (isTa) subTopic.citizenRightsTa else subTopic.citizenRightsEn
                    rights.forEachIndexed { index, right ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = VerifiedSageGreen,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextOnSageGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = right,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimaryDark,
                                    lineHeight = 18.sp
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Section: Required Documentation Checklist
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SandstoneCard),
                border = BorderStroke(1.dp, CardBorderStroke),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("subtopic_documentation_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = SovereignNavy,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isTa) "தேவையான ஆவணங்களின் பட்டியல்" else "Required Evidentiary Checklist",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SovereignNavy
                            )
                        )
                    }
                    Text(
                        text = if (isTa) "வழக்கு தாக்கல் செய்வதற்கு முன் இவற்றை தயார் செய்துகொள்ளுங்கள்" else "Check off items as you gather proof for your legal claim",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondaryDark,
                            fontSize = 11.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val docs = if (isTa) subTopic.requiredDocumentsTa else subTopic.requiredDocumentsEn
                    docs.forEachIndexed { index, doc ->
                        val isChecked = checkedDocuments[index] == true
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isChecked) VerifiedSageGreen.copy(alpha = 0.35f) else WarmCanvasBg,
                            border = BorderStroke(1.dp, if (isChecked) TextOnSageGreen.copy(alpha = 0.4f) else CardBorderStroke),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checkedDocuments[index] = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = SovereignNavy,
                                        checkmarkColor = WarmCanvasBg
                                    ),
                                    modifier = Modifier.testTag("checklist_checkbox_$index")
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = doc,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextPrimaryDark,
                                        fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // External Actions Card: Open Portal & Call Helpline
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SandstoneCard),
                border = BorderStroke(1.dp, CardBorderStroke),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("subtopic_portals_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isTa) "அதிகாரப்பூர்வ போர்ட்டல் & உதவி எண்கள்" else "Official Portals & Statutory Helplines",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SovereignNavy
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Action 1: Open Official Government Portal
                    Button(
                        onClick = {
                            ActionUtils.openWebUrl(context, subTopic.officialPortalUrl)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SovereignNavy),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_open_official_portal")
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = null,
                            tint = WarmCanvasBg,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isTa) "அதிகாரப்பூர்வ தளம் திற (${subTopic.officialPortalName})" else "Open Official Portal (${subTopic.officialPortalName})",
                            color = WarmCanvasBg,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action 2: Dial Helpline
                    OutlinedButton(
                        onClick = {
                            ActionUtils.dialEmergencyHelpline(context, subTopic.helplineNumber)
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, SovereignNavy),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_dial_subtopic_helpline")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            tint = SovereignNavy,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isTa) "அழைக்க: ${subTopic.helplineName}" else "Call: ${subTopic.helplineName}",
                            color = SovereignNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            // Primary In-App Action: Register Grievance with Voice
            Button(
                onClick = { onNavigateToVoice(subTopic.key) },
                colors = ButtonDefaults.buttonColors(containerColor = AccentTerracotta),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_register_grievance_voice")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    tint = WarmCanvasBg,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isTa) "இப்பிரிவில் குரல்வழி புகார் மனு எழுது" else "Register Grievance in this Category",
                    color = WarmCanvasBg,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
