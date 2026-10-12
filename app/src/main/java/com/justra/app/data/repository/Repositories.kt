package com.justra.app.data.repository

import android.content.Context
import com.justra.app.data.api.GeminiLegalEngine
import com.justra.app.data.api.LegalStatuteKnowledge
import com.justra.app.data.local.ActionStepDao
import com.justra.app.data.local.ActionStepEntity
import com.justra.app.data.local.CaseDao
import com.justra.app.data.local.CaseEntity
import com.justra.app.data.local.ChatDao
import com.justra.app.data.local.ChatMessageEntity
import com.justra.app.data.local.EvidenceArtifactEntity
import com.justra.app.data.local.EvidenceDao
import com.justra.app.data.local.ScamDao
import com.justra.app.data.local.ScamIncidentEntity
import com.justra.app.data.local.SecurityManager
import com.justra.app.data.local.ShareTokenDao
import com.justra.app.data.local.ShareTokenEntity
import com.justra.app.data.local.TimelineDao
import com.justra.app.data.local.TimelineEventEntity
import com.justra.app.domain.model.DisputeCategory
import com.justra.app.domain.model.EvidenceCategory
import com.justra.app.domain.model.LanguagePreference
import com.justra.app.domain.model.ReadinessMetric
import com.justra.app.domain.model.SenderRole
import com.justra.app.domain.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CaseRepository(
    private val caseDao: CaseDao,
    private val actionStepDao: ActionStepDao,
    private val timelineDao: TimelineDao,
    private val securityManager: SecurityManager
) {
    val allCases: Flow<List<CaseEntity>> = caseDao.getAllCases()
    val activeCases: Flow<List<CaseEntity>> = caseDao.getActiveCases()

    fun getCaseStream(caseId: String): Flow<CaseEntity?> = caseDao.getCaseById(caseId)

    suspend fun getCaseOnce(caseId: String): CaseEntity? = caseDao.getCaseByIdOnce(caseId)

    suspend fun createCaseWithDefaults(
        title: String,
        category: DisputeCategory,
        incidentDate: String? = null,
        opposingParty: String? = null,
        estimatedClaimAmount: String? = null,
        factualSummary: String? = null,
        demandedRelief: String? = null,
        userRole: UserRole? = null
    ): String {
        val caseId = UUID.randomUUID().toString()
        val role = userRole ?: securityManager.getUserRole()
        val statute = LegalStatuteKnowledge.getStatuteForCategory(category)
        val defaultSubject = when (role) {
            UserRole.LEGAL_COUNSEL -> "Statutory Demand Notice on behalf of Client against ${opposingParty ?: "Respondent"} (${statute.actName})"
            UserRole.MSME_BUSINESS -> "MSMED Act Commercial Notice: Overdue Payment Demand to ${opposingParty ?: "Buyer"}"
            UserRole.CYBER_FRAUD_VICTIM -> "Urgent Cyber Crime & Financial Fraud Grievance: Fraud by ${opposingParty ?: "Perpetrator"}"
            UserRole.CITIZEN -> "Formal Legal Grievance under ${statute.actName}: Dispute with ${opposingParty ?: "Opposing Party"}"
        }
        val defaultBody = buildInitialComplaintBody(category, title, incidentDate, opposingParty, estimatedClaimAmount, factualSummary, demandedRelief, role)

        val caseEntity = CaseEntity(
            caseId = caseId,
            title = title,
            disputeCategory = category,
            status = "ACTIVE",
            incidentDate = incidentDate,
            opposingParty = opposingParty,
            estimatedClaimAmount = estimatedClaimAmount,
            factualSummary = factualSummary,
            demandedRelief = demandedRelief ?: statute.primaryRelief,
            recipientAuthority = statute.applicableForum,
            subjectLine = defaultSubject,
            generatedComplaintDraft = defaultBody,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        caseDao.insertCase(caseEntity)

        // Seed statutory sequential action steps
        val steps = generateStatutorySteps(caseId, category)
        actionStepDao.insertSteps(steps)

        // Seed initial timeline event if date provided
        if (!incidentDate.isNullOrBlank()) {
            val event = TimelineEventEntity(
                eventId = UUID.randomUUID().toString(),
                caseId = caseId,
                eventDate = incidentDate,
                eventTitle = "Cause of Action / Incident Inception",
                description = "Dispute commenced regarding: $title",
                isInferred = false,
                sourceReference = "Citizen Primary Intake Statement",
                isVerified = false
            )
            timelineDao.insertEvent(event)
        }

        return caseId
    }

    suspend fun updateCase(caseEntity: CaseEntity) {
        caseDao.updateCase(caseEntity.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteCaseById(caseId: String) {
        caseDao.deleteCaseById(caseId)
    }

    suspend fun regenerateComplaintDraft(caseId: String, userRole: UserRole): String? {
        val existingCase = getCaseOnce(caseId) ?: return null
        val updatedDraft = buildInitialComplaintBody(
            category = existingCase.disputeCategory,
            title = existingCase.title,
            incidentDate = existingCase.incidentDate,
            opposingParty = existingCase.opposingParty,
            estimatedClaimAmount = existingCase.estimatedClaimAmount,
            factualSummary = existingCase.factualSummary,
            demandedRelief = existingCase.demandedRelief,
            userRole = userRole
        )
        val statute = LegalStatuteKnowledge.getStatuteForCategory(existingCase.disputeCategory)
        val updatedSubject = when (userRole) {
            UserRole.LEGAL_COUNSEL -> "Statutory Demand Notice on behalf of Client against ${existingCase.opposingParty ?: "Respondent"} (${statute.actName})"
            UserRole.MSME_BUSINESS -> "MSMED Act Commercial Notice: Overdue Payment Demand to ${existingCase.opposingParty ?: "Buyer"}"
            UserRole.CYBER_FRAUD_VICTIM -> "Urgent Cyber Crime & Financial Fraud Grievance: Fraud by ${existingCase.opposingParty ?: "Perpetrator"}"
            UserRole.CITIZEN -> "Formal Legal Grievance under ${statute.actName}: Dispute with ${existingCase.opposingParty ?: "Opposing Party"}"
        }
        updateCase(existingCase.copy(
            generatedComplaintDraft = updatedDraft,
            subjectLine = updatedSubject
        ))
        return updatedDraft
    }

    private fun generateStatutorySteps(caseId: String, category: DisputeCategory): List<ActionStepEntity> {
        val statute = LegalStatuteKnowledge.getStatuteForCategory(category)
        return when (category) {
            DisputeCategory.CONSUMER_GRIEVANCE -> listOf(
                ActionStepEntity(
                    stepId = UUID.randomUUID().toString(),
                    caseId = caseId,
                    stepNumber = 1,
                    actionTitleEn = "Lodge Written Grievance with Grievance Officer",
                    actionTitleTa = "நிறுவனத்தின் குறைதீர்க்கும் அதிகாரியிடம் எழுத்துப்பூர்வ புகார் அளித்தல்",
                    descriptionEn = "Send an official notice requesting refund/replacement within 15 days via email or registered post.",
                    descriptionTa = "15 நாட்களுக்குள் தீர்வுகாணக் கோரி நிறுவனத்தின் மின்னஞ்சல் அல்லது பதிவுத் தபாலில் முறையீடு அனுப்பவும்.",
                    targetAuthority = "Company Grievance Desk",
                    isCompleted = false,
                    directActionUrl = null,
                    requiredDocumentList = listOf("Purchase Invoice / Bill", "Payment Receipt", "Defect Photos / Screenshots")
                ),
                ActionStepEntity(
                    stepId = UUID.randomUUID().toString(),
                    caseId = caseId,
                    stepNumber = 2,
                    actionTitleEn = "Register Dispute on National Consumer Helpline (NCH)",
                    actionTitleTa = "தேசிய நுகர்வோர் உதவி மையத்தில் (NCH) புகார் பதிவு செய்தல்",
                    descriptionEn = "Dial 1915 or lodge an automated docket on consumerhelpline.gov.in for pre-litigation mediation.",
                    descriptionTa = "1915-ஐ அழைக்கவும் அல்லது consumerhelpline.gov.in இணையதளத்தில் சமரசத்திற்கு பதிவு செய்யவும்.",
                    targetAuthority = "Department of Consumer Affairs (NCH)",
                    isCompleted = false,
                    directActionUrl = "https://consumerhelpline.gov.in",
                    requiredDocumentList = listOf("Order Confirmation", "NCH Docket ID")
                ),
                ActionStepEntity(
                    stepId = UUID.randomUUID().toString(),
                    caseId = caseId,
                    stepNumber = 3,
                    actionTitleEn = "Serve Formal Legal Notice through Counsel",
                    actionTitleTa = "வக்கீல் மூலமாக அதிகாரப்பூர்வ சட்ட அறிவிப்பு (Legal Notice) அனுப்புதல்",
                    descriptionEn = "Demand statutory remedy under Section 35 of Consumer Protection Act 2019 before court filing.",
                    descriptionTa = "நுகர்வோர் சட்டம் பிரிவு 35-ன் கீழ் 15 நாள் காலக்கெடுவுடன் கூடிய சட்ட நோட்டீஸ்.",
                    targetAuthority = "Opposing Party / Corporate Entity",
                    isCompleted = false,
                    directActionUrl = null,
                    requiredDocumentList = listOf("Draft Legal Notice", "Postal Speed Post Tracking Slip")
                ),
                ActionStepEntity(
                    stepId = UUID.randomUUID().toString(),
                    caseId = caseId,
                    stepNumber = 4,
                    actionTitleEn = "File Consumer Complaint on e-Daakhil / DCDRC",
                    actionTitleTa = "e-Daakhil போர்ட்டல் அல்லது மாவட்ட நுகர்வோர் நீதிமன்றத்தில் வழக்கு தாக்கல் செய்தல்",
                    descriptionEn = "Submit complaint petition with indexed evidence artifacts to District Consumer Disputes Redressal Commission.",
                    descriptionTa = "மாவட்ட நுகர்வோர் குறைதீர்க்கும் ஆணையத்தில் இணைக்கப்பட்ட சான்றுகளுடன் வழக்கு தொடரவும்.",
                    targetAuthority = "District Consumer Forum / e-Daakhil",
                    isCompleted = false,
                    directActionUrl = "https://edaakhil.nic.in",
                    requiredDocumentList = listOf("Notarized Affidavit", "Evidence Index", "Court Fee Stamp")
                )
            )
            DisputeCategory.CYBER_FINANCIAL_FRAUD, DisputeCategory.SCAM_ANALYSIS -> listOf(
                ActionStepEntity(
                    stepId = UUID.randomUUID().toString(),
                    caseId = caseId,
                    stepNumber = 1,
                    actionTitleEn = "Immediate 1930 Helpline Call (Golden Hour Freeze)",
                    actionTitleTa = "உடனடி 1930 உதவி எண் அழைப்பு (பணப் பரிவர்த்தனை முடக்கம்)",
                    descriptionEn = "Dial 1930 immediately to trigger Indian Cybercrime Coordination Centre (I4C) bank freeze protocol.",
                    descriptionTa = "மோசடி செய்யப்பட்ட வங்கி அல்லது UPI கணக்கை உடனடியாக முடக்க 1930-ஐ தொடர்பு கொள்ளவும்.",
                    targetAuthority = "Citizen Financial Cyber Fraud Reporting System",
                    isCompleted = false,
                    directActionUrl = "tel:1930",
                    requiredDocumentList = listOf("Bank Transaction UTR", "Debit SMS Timestamp", "Beneficiary Account/UPI")
                ),
                ActionStepEntity(
                    stepId = UUID.randomUUID().toString(),
                    caseId = caseId,
                    stepNumber = 2,
                    actionTitleEn = "Lodge Formal Complaint on Cyber Crime Portal",
                    actionTitleTa = "தேசிய சைபர் கிரைம் போர்ட்டலில் விரிவான புகார் பதிவு செய்தல்",
                    descriptionEn = "File incident with full screenshot proofs on cybercrime.gov.in under IT Act Sec 66D.",
                    descriptionTa = "cybercrime.gov.in இணையதளத்தில் ஆதாரங்களுடன் முறையான புகாரை சமர்ப்பிக்கவும்.",
                    targetAuthority = "National Cyber Crime Reporting Portal",
                    isCompleted = false,
                    directActionUrl = "https://cybercrime.gov.in",
                    requiredDocumentList = listOf("Bank Account Statement", "Fraudulent Chat / SMS Screenshot", "Acknowledgement Number")
                ),
                ActionStepEntity(
                    stepId = UUID.randomUUID().toString(),
                    caseId = caseId,
                    stepNumber = 3,
                    actionTitleEn = "Submit Disputed Transaction Form to Home Bank Branch",
                    actionTitleTa = "உங்கள் வங்கி கிளையில் சர்ச்சை பரிவர்த்தனை படிவம் சமர்ப்பித்தல்",
                    descriptionEn = "Submit RBI Zero-Liability Dispute Chargeback claim within 3 days to protect account balance.",
                    descriptionTa = "ஆர்பிஐ விதிகளின்படி 3 நாட்களுக்குள் பொறுப்பற்ற பரிவர்த்தனை கோரிக்கை படிவத்தை வங்கியிடம் ஒப்படைக்கவும்.",
                    targetAuthority = "Nodal Bank Grievance Officer",
                    isCompleted = false,
                    directActionUrl = null,
                    requiredDocumentList = listOf("Bank Chargeback Form", "FIR / Acknowledgement Copy")
                )
            )
            DisputeCategory.TENANCY_RENT -> listOf(
                ActionStepEntity(
                    stepId = UUID.randomUUID().toString(),
                    caseId = caseId,
                    stepNumber = 1,
                    actionTitleEn = "Serve Written Notice for Refund of Security Deposit",
                    actionTitleTa = "முன்பணம் திரும்ப கோரி எழுத்துப்பூர்வ அறிவிப்பு அனுப்புதல்",
                    descriptionEn = "Demand full deposit return within 30 days citing the registered Rental Agreement terms.",
                    descriptionTa = "வாடகை ஒப்பந்தத்தின்படி 30 நாட்களுக்குள் முழு முன்பணத்தையும் திருப்பித் தரக் கோரி நோட்டீஸ்.",
                    targetAuthority = "Landlord / Property Manager",
                    isCompleted = false,
                    directActionUrl = null,
                    requiredDocumentList = listOf("Rental Agreement", "Deposit Payment Bank Receipt", "Key Handover Proof")
                ),
                ActionStepEntity(
                    stepId = UUID.randomUUID().toString(),
                    caseId = caseId,
                    stepNumber = 2,
                    actionTitleEn = "File Petition before Rent Authority / Rent Court",
                    actionTitleTa = "வாடகை நீதிமன்றம் / துணை ஆட்சியரிடம் மனு தாக்கல் செய்தல்",
                    descriptionEn = "Initiate formal proceedings under Section 21 of TN Tenancy Act for unlawful withholding of dues.",
                    descriptionTa = "தமிழ்நாடு வாடகை சட்டத்தின் கீழ் நிலுவைத் தொகையை மீட்க வாடகை நீதிமன்றத்தில் மனு.",
                    targetAuthority = "Jurisdictional Rent Court",
                    isCompleted = false,
                    directActionUrl = null,
                    requiredDocumentList = listOf("Registered Tenancy Document", "Calculation Sheet of Interest", "Legal Notice Copy")
                )
            )
            else -> listOf(
                ActionStepEntity(
                    stepId = UUID.randomUUID().toString(),
                    caseId = caseId,
                    stepNumber = 1,
                    actionTitleEn = "Prepare and Dispatch Formal Grievance Notice",
                    actionTitleTa = "முறையான சட்ட அறிவிப்பு தயார் செய்து அனுப்புதல்",
                    descriptionEn = "Present factual timeline and demand statutory relief under applicable law.",
                    descriptionTa = "சம்பவங்களின் விவரங்களை குறிப்பிட்டு உரிய நிவாரணம் கோரி நோட்டீஸ் அனுப்புதல்.",
                    targetAuthority = "Opposing Party / Nodal Authority",
                    isCompleted = false,
                    directActionUrl = null,
                    requiredDocumentList = listOf("Chronology Document", "Supporting Evidence Proofs")
                ),
                ActionStepEntity(
                    stepId = UUID.randomUUID().toString(),
                    caseId = caseId,
                    stepNumber = 2,
                    actionTitleEn = "Escalate to Jurisdictional Statutory Authority / Tribunal",
                    actionTitleTa = "உரிய தீர்ப்பாயம் அல்லது நீதிமன்றத்தில் வழக்கு தொடருதல்",
                    descriptionEn = "File formal dispute petition along with indexed verification evidence.",
                    descriptionTa = "சரிபார்க்கப்பட்ட சான்றுகளுடன் உரிய தீர்ப்பாயத்தில் முறையீடு செய்தல்.",
                    targetAuthority = statute.applicableForum,
                    isCompleted = false,
                    directActionUrl = null,
                    requiredDocumentList = listOf("Verified Complaint Petition", "Annexed Evidence Vault Records")
                )
            )
        }
    }

    fun buildInitialComplaintBody(
        category: DisputeCategory,
        title: String,
        incidentDate: String?,
        opposingParty: String?,
        estimatedClaimAmount: String?,
        factualSummary: String?,
        demandedRelief: String?,
        userRole: UserRole
    ): String {
        val statute = LegalStatuteKnowledge.getStatuteForCategory(category)
        val dateStr = incidentDate?.takeIf { it.isNotBlank() } ?: SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val partyName = opposingParty?.takeIf { it.isNotBlank() } ?: "[Opposing Party / Respondent Entity]"
        val claimAmt = estimatedClaimAmount?.takeIf { it.isNotBlank() }?.let { if (it.startsWith("₹")) it else "₹$it" } ?: "₹[Calculated Disputed Amount]"
        val summaryText = factualSummary?.takeIf { it.isNotBlank() }
            ?: "The incident arose from a transaction/engagement on or around $dateStr with $partyName, causing financial detriment, procedural unfairness, and statutory distress."

        return when (userRole) {
            UserRole.CITIZEN -> """
BEFORE THE COMPETENT ADJUDICATING FORUM / AUTHORITY
IN THE MATTER OF: ${category.titleEn.uppercase()}

GRIEVANCE PETITION / FORMAL LEGAL COMPLAINT
Under ${statute.actName} (${statute.section})

1. PARTICULARS OF PARTIES:
   Complainant: Aggrieved Citizen / Individual Consumer
   Opposing Party / Respondent: $partyName

2. DATE OF INCEPTION & JURISDICTION:
   Date of Cause of Action: $dateStr
   Statutory Ground: ${statute.title}
   Forum of Competence: ${statute.applicableForum}

3. CAPACITY & STATEMENT OF FACTS:
   The Complainant is a bonafide citizen and individual consumer acting pro se to enforce fundamental statutory rights and protections.
   Factual Narrative:
   "$summaryText"

4. MONETARY LOSS & CLAIM ASSESSMENT:
   Direct Disputed Loss / Claim: $claimAmt
   Statutory Limitation Period: ${statute.limitationPeriod}

5. DEMANDED RELIEF & REDRESSAL:
   ${demandedRelief ?: statute.primaryRelief}
   Additional: Refund of disputed sum with statutory interest, compensation for mental harassment, and procedural costs.

6. VERIFICATION:
   I, the Complainant above named, do hereby verify that the contents of paragraphs 1 to 5 are true to my personal knowledge and derived from contemporaneous evidentiary records preserved in the Evidence Vault.
            """.trimIndent()

            UserRole.LEGAL_COUNSEL -> """
OFFICE OF LEGAL COUNSEL & ADVOCATE-ON-RECORD
STATUTORY DEMAND NOTICE & PRE-LITIGATION DISPUTE BRIEF
Subject Matter: ${category.titleEn} | Act: ${statute.actName} (${statute.section})

TO:
$partyName
[Respondent / Noticee]

UNDER INSTRUCTIONS FROM AND ON BEHALF OF MY CLIENT:
Aggrieved Party / Complainant

SUBJECT: FORMAL STATUTORY NOTICE OF DEMAND UNDER ${statute.actName.uppercase()}
DISPUTE TITLE: $title
REF DATE: $dateStr

Sir / Madam,

Under instructions from and on behalf of my client ('The Complainant'), I, the undersigned Advocate, do hereby serve upon you this formal Statutory Notice of Demand:

1. CLIENT'S BRIEF OF FACTS:
   My client instructs me that the following actionable events occurred:
   "$summaryText"

2. STATUTORY CONTRAVENTIONS & LEGAL LIABILITIES:
   Your acts and omissions constitute a direct infraction of ${statute.actName} (${statute.section}), specifically amounting to ${statute.title}.
   The cause of action accrued on $dateStr within the territorial jurisdiction of ${statute.applicableForum}.

3. QUANTIFICATION OF LIABILITY:
   Liquidated / Disputed Sum: $claimAmt
   The Complainant is entitled to statutory interest and full indemnification of legal representation expenses.

4. DEMAND OF IMMEDIATE COMPLIANCE (15-DAY CURE PERIOD):
   You are hereby called upon to comply with the following within fifteen (15) days of receipt:
   ${demandedRelief ?: statute.primaryRelief}
   Payment of $claimAmt along with ₹15,000 towards legal notice drafting charges.

TAKE NOTICE that failure to redress my client's grievance within the statutory period shall compel filing before ${statute.applicableForum} or Criminal Courts without further notice, at your sole risk and costs.

Yours faithfully,
Advocate for the Complainant
Enrollment / Bar Ref: Active Bar Council Member
            """.trimIndent()

            UserRole.MSME_BUSINESS -> """
BEFORE THE MSME FACILITATION COUNCIL & COMPETENT COMMERCIAL FORUM
COMMERCIAL GRIEVANCE & STATUTORY RECOVERY DEMAND
Under MSMED Act, 2006 & ${statute.actName}

1. PARTICULARS OF COMMERCIAL PARTIES:
   Complainant / Claimant: Micro/Small/Medium Enterprise (MSME) Authorized Signatory
   Buyer / Respondent: $partyName

2. STATUTORY BASIS OF COMMERCIAL CLAIM:
   Governing Acts: Micro, Small and Medium Enterprises Development Act, 2006 (Sec 15, 16) & ${statute.actName}
   Date of Supply / Transaction Default: $dateStr
   Adjudicating Council / Court: ${statute.applicableForum}

3. BUSINESS STATEMENT OF FACTS & DEFAULT:
   The Claimant enterprise provided verified goods / commercial services under lawful agreement. The Respondent failed to make timely payments within the statutory 45-day window mandated under Section 15 of MSMED Act.
   Business Narrative:
   "$summaryText"

4. QUANTIFIED COMMERCIAL CLAIM:
   Principal Invoiced Value: $claimAmt
   Statutory Compound Interest: Compound monthly interest at three times (3x) the RBI Bank Rate under Section 16 of MSMED Act, 2006.

5. DEMANDED COMMERCIAL RELIEF:
   1. Immediate release of overdue principal amount ($claimAmt).
   2. Payment of accrued compounding interest calculated at 3x RBI Bank Rate.
   3. Compensation for commercial disruption and working capital impairment.
   ${demandedRelief?.let { "4. Additional: $it" } ?: ""}

6. CORPORATE / ENTERPRISE VERIFICATION:
   Verified at Chennai / Tamil Nadu that the contents above are extracted from registered books of accounts, GST tax invoices, and commercial purchase orders.
            """.trimIndent()

            UserRole.CYBER_FRAUD_VICTIM -> """
EMERGENCY CYBER CRIME INCIDENT COMPLAINT & FORENSIC PETITION
SUBMITTED TO: National Cyber Crime Reporting Portal (1930) / State Cyber Crime Police Station
COPY TO: Nodal Grievance Officer, Concerned Bank & Payment Gateway
Under Information Technology Act, 2000 (Sec 43, 66C, 66D) & Bharatiya Nyaya Sanhita, 2023 (Sec 318)

1. VICTIM & OPPOSING ENTITY DETAILS:
   Complainant / Victim: Individual Cyber Incident Victim
   Alleged Perpetrator / Beneficiary Account: $partyName
   Incident Inception Date / Time: $dateStr

2. CYBER FRAUD CHRONOLOGY & STATEMENT:
   The Complainant fell victim to an unauthorized electronic transaction / cyber financial deception as detailed below:
   "$summaryText"

3. QUANTUM OF UNAUTHORIZED LOSS:
   Total Unauthorized Electronic Debit: $claimAmt
   Governing RBI Circular: RBI Master Direction on Customer Protection – Limiting Liability of Customers in Unauthorized Electronic Banking Transactions (Zero Liability Clause).

4. URGENT DEMANDED DIRECTIONS / RELIEF:
   1. IMMEDIATE FREEZING of destination beneficiary bank accounts, UPI VPAs, and wallets associated with $partyName.
   2. Full credit reversal of $claimAmt under RBI Zero-Liability Mandate due to immediate reporting.
   3. Formal registration of FIR under Section 66D IT Act (Cheating by Personation using Computer Resource).
   4. Digital forensic preservation of IP logs, CDRs, and payment gateway audit trails.

5. DIGITAL INTEGRITY VERIFICATION:
   The Complainant verifies that the above details are authentic, supported by contemporaneous SMS alerts, bank account statements, and digital payment receipts preserved in the Evidence Vault.
            """.trimIndent()
        }
    }
}

class ChatRepository(
    private val chatDao: ChatDao,
    private val caseDao: CaseDao
) {
    fun getMessagesForCase(caseId: String?, userId: String = "local_user"): Flow<List<ChatMessageEntity>> =
        chatDao.getMessagesForUserAndCase(userId, caseId)

    suspend fun sendUserMessage(
        messageContent: String,
        caseId: String?,
        userId: String = "local_user",
        language: LanguagePreference,
        userRole: UserRole = UserRole.CITIZEN
    ): ChatMessageEntity {
        val userMessage = ChatMessageEntity(
            messageId = UUID.randomUUID().toString(),
            caseId = caseId,
            userId = userId,
            senderRole = SenderRole.USER,
            timestamp = System.currentTimeMillis(),
            messageContent = messageContent
        )
        chatDao.insertMessage(userMessage)

        val currentCase = if (caseId != null) caseDao.getCaseByIdOnce(caseId) else null
        val intakeResult = GeminiLegalEngine.analyzeChatIntake(
            userMessage = messageContent,
            currentCaseCategory = currentCase?.disputeCategory,
            language = language,
            userRole = userRole
        )

        val assistantMessage = ChatMessageEntity(
            messageId = UUID.randomUUID().toString(),
            caseId = caseId,
            userId = userId,
            senderRole = SenderRole.ASSISTANT,
            timestamp = System.currentTimeMillis(),
            messageContent = intakeResult.responseText,
            extractedEntityKey = intakeResult.extractedKey,
            extractedEntityValue = intakeResult.extractedValue,
            isActionableProof = intakeResult.isActionableProof
        )
        chatDao.insertMessage(assistantMessage)
        return assistantMessage
    }

    suspend fun clearChat(caseId: String) {
        chatDao.clearMessagesForCase(caseId)
    }

    suspend fun clearChatForUser(userId: String) {
        chatDao.clearMessagesForUser(userId)
    }

    suspend fun clearAllChatHistory() {
        chatDao.clearAllMessages()
    }
}

class ActionStepRepository(
    private val actionStepDao: ActionStepDao
) {
    fun getStepsForCase(caseId: String): Flow<List<ActionStepEntity>> =
        actionStepDao.getStepsForCase(caseId)

    suspend fun setStepCompletion(stepId: String, completed: Boolean) {
        actionStepDao.setStepCompletion(stepId, completed)
    }

    suspend fun addCustomStep(step: ActionStepEntity) {
        actionStepDao.insertStep(step)
    }
}

class EvidenceRepository(
    private val evidenceDao: EvidenceDao,
    private val securityManager: SecurityManager
) {
    fun getArtifactsForCase(caseId: String): Flow<List<EvidenceArtifactEntity>> =
        evidenceDao.getArtifactsForCase(caseId)

    fun getArtifactCountForCase(caseId: String): Flow<Int> =
        evidenceDao.getArtifactCountForCase(caseId)

    suspend fun addEvidenceArtifact(
        caseId: String,
        fileName: String,
        fileBytes: ByteArray? = null,
        mimeType: String? = null,
        category: EvidenceCategory,
        notes: String? = null
    ): EvidenceArtifactEntity {
        val hash = if (fileBytes != null && fileBytes.isNotEmpty()) {
            securityManager.calculateStreamSha256(java.io.ByteArrayInputStream(fileBytes))
        } else {
            securityManager.calculateSha256(fileName + System.currentTimeMillis())
        }
        val fileUri = fileBytes?.let { bytes ->
            // Save to app private storage
            val file = java.io.File(securityManager.appContext.filesDir, fileName)
            java.io.FileOutputStream(file).use { it.write(bytes) }
            file.absolutePath
        } ?: ""
        val artifact = EvidenceArtifactEntity(
            artifactId = UUID.randomUUID().toString(),
            caseId = caseId,
            fileName = fileName,
            fileUri = fileUri,
            mimeType = mimeType ?: "application/octet-stream",
            category = category,
            sha256Hash = hash,
            uploadTimestamp = System.currentTimeMillis(),
            notes = notes
        )
        evidenceDao.insertArtifact(artifact)
        return artifact
    }

    suspend fun deleteArtifactById(artifactId: String) {
        evidenceDao.deleteArtifactById(artifactId)
    }
}

class TimelineRepository(
    private val timelineDao: TimelineDao
) {
    fun getEventsForCase(caseId: String): Flow<List<TimelineEventEntity>> =
        timelineDao.getEventsForCase(caseId)

    suspend fun addEvent(
        caseId: String,
        eventDate: String,
        eventTitle: String,
        description: String,
        isInferred: Boolean = false,
        sourceReference: String? = null,
        isVerified: Boolean = true
    ) {
        val event = TimelineEventEntity(
            eventId = UUID.randomUUID().toString(),
            caseId = caseId,
            eventDate = eventDate,
            eventTitle = eventTitle,
            description = description,
            isInferred = isInferred,
            sourceReference = sourceReference,
            isVerified = isVerified
        )
        timelineDao.insertEvent(event)
    }

    suspend fun updateEvent(event: TimelineEventEntity) {
        timelineDao.updateEvent(event)
    }

    suspend fun deleteEventById(eventId: String) {
        timelineDao.deleteEventById(eventId)
    }
}

class ScamRepository(
    private val scamDao: ScamDao
) {
    val allScamIncidents: Flow<List<ScamIncidentEntity>> = scamDao.getAllScamIncidents()

    suspend fun analyzeAndSaveScam(text: String, language: LanguagePreference): ScamIncidentEntity {
        val incident = GeminiLegalEngine.analyzeScamText(text, language)
        scamDao.insertScamIncident(incident)
        return incident
    }

    suspend fun deleteIncident(id: String) {
        scamDao.deleteScamIncident(id)
    }
}

class CounselHandoffRepository(
    private val shareTokenDao: ShareTokenDao,
    private val securityManager: SecurityManager
) {
    fun getTokensForCase(caseId: String): Flow<List<ShareTokenEntity>> =
        shareTokenDao.getTokensForCase(caseId)

    suspend fun generateShareToken(caseId: String, validityHours: Int = 72): ShareTokenEntity {
        val tokenCode = "NYM-" + UUID.randomUUID().toString().take(8).uppercase()
        val expiresAt = System.currentTimeMillis() + (validityHours * 60 * 60 * 1000L)
        val signature = securityManager.calculateSha256("$tokenCode:$caseId:$expiresAt")

        val tokenEntity = ShareTokenEntity(
            tokenId = UUID.randomUUID().toString(),
            caseId = caseId,
            tokenCode = tokenCode,
            signatureHash = signature,
            expiresAt = expiresAt,
            createdAt = System.currentTimeMillis(),
            advocateNotes = null
        )
        shareTokenDao.insertToken(tokenEntity)
        return tokenEntity
    }

    suspend fun appendAdvocateNotes(tokenId: String, notes: String) {
        shareTokenDao.updateAdvocateNotes(tokenId, notes)
    }
}
