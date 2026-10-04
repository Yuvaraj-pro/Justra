package com.justra.app.data.repository

import com.justra.app.data.local.DraftTemplateDao
import com.justra.app.data.local.DraftTemplateEntity
import com.justra.app.data.local.LegalDocumentDao
import com.justra.app.data.local.UserLegalDocumentEntity
import com.justra.app.domain.model.DisputeCategory
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class LegalDocumentRepository(
    private val legalDocumentDao: LegalDocumentDao,
    private val draftTemplateDao: DraftTemplateDao
) {
    // ---------------------------------------------------------
    // User Legal Documents (Offline-First CRUD & Reactive Flows)
    // ---------------------------------------------------------

    val allDocuments: Flow<List<UserLegalDocumentEntity>> = legalDocumentDao.getAllDocuments()

    fun getDocumentsForCase(caseId: String): Flow<List<UserLegalDocumentEntity>> =
        legalDocumentDao.getDocumentsForCase(caseId)

    fun getDocumentById(documentId: String): Flow<UserLegalDocumentEntity?> =
        legalDocumentDao.getDocumentById(documentId)

    suspend fun getDocumentByIdOnce(documentId: String): UserLegalDocumentEntity? =
        legalDocumentDao.getDocumentByIdOnce(documentId)

    fun getDocumentsByType(type: String): Flow<List<UserLegalDocumentEntity>> =
        legalDocumentDao.getDocumentsByType(type)

    fun getDocumentsByCategory(category: DisputeCategory): Flow<List<UserLegalDocumentEntity>> =
        legalDocumentDao.getDocumentsByCategory(category)

    fun searchDocuments(query: String): Flow<List<UserLegalDocumentEntity>> =
        legalDocumentDao.searchDocuments(query)

    val documentCount: Flow<Int> = legalDocumentDao.getDocumentCount()

    suspend fun saveLegalDocument(
        title: String,
        documentType: String,
        category: DisputeCategory,
        content: String,
        caseId: String? = null,
        recipientParty: String? = null,
        jurisdictionCourt: String? = null,
        statutoryActRef: String? = null,
        language: String = "EN",
        isDraft: Boolean = true,
        notes: String? = null
    ): UserLegalDocumentEntity {
        val documentId = UUID.randomUUID().toString()
        val doc = UserLegalDocumentEntity(
            documentId = documentId,
            caseId = caseId,
            title = title,
            documentType = documentType,
            disputeCategory = category,
            content = content,
            recipientParty = recipientParty,
            jurisdictionCourt = jurisdictionCourt,
            statutoryActRef = statutoryActRef,
            language = language,
            isDraft = isDraft,
            isFinalized = !isDraft,
            exportPdfPath = null,
            fileSizeBytes = content.toByteArray(Charsets.UTF_8).size.toLong(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            notes = notes
        )
        legalDocumentDao.insertDocument(doc)
        return doc
    }

    suspend fun updateDocumentContent(
        documentId: String,
        title: String? = null,
        content: String,
        isFinalized: Boolean? = null,
        notes: String? = null
    ) {
        val existing = legalDocumentDao.getDocumentByIdOnce(documentId) ?: return
        val updated = existing.copy(
            title = title ?: existing.title,
            content = content,
            isDraft = if (isFinalized != null) !isFinalized else existing.isDraft,
            isFinalized = isFinalized ?: existing.isFinalized,
            fileSizeBytes = content.toByteArray(Charsets.UTF_8).size.toLong(),
            notes = notes ?: existing.notes,
            updatedAt = System.currentTimeMillis()
        )
        legalDocumentDao.updateDocument(updated)
    }

    suspend fun recordExportPdfPath(documentId: String, pdfPath: String) {
        val existing = legalDocumentDao.getDocumentByIdOnce(documentId) ?: return
        legalDocumentDao.updateDocument(
            existing.copy(
                exportPdfPath = pdfPath,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteDocument(documentId: String) {
        legalDocumentDao.deleteDocumentById(documentId)
    }

    // ---------------------------------------------------------
    // Draft Templates (Offline statutory library & customization)
    // ---------------------------------------------------------

    val allTemplates: Flow<List<DraftTemplateEntity>> = draftTemplateDao.getAllTemplates()

    val favoriteTemplates: Flow<List<DraftTemplateEntity>> = draftTemplateDao.getFavoriteTemplates()

    fun getTemplatesByCategory(category: DisputeCategory): Flow<List<DraftTemplateEntity>> =
        draftTemplateDao.getTemplatesByCategory(category)

    fun getTemplateById(templateId: String): Flow<DraftTemplateEntity?> =
        draftTemplateDao.getTemplateById(templateId)

    suspend fun getTemplateByIdOnce(templateId: String): DraftTemplateEntity? =
        draftTemplateDao.getTemplateByIdOnce(templateId)

    fun searchTemplates(query: String): Flow<List<DraftTemplateEntity>> =
        draftTemplateDao.searchTemplates(query)

    suspend fun toggleFavorite(templateId: String, isFavorite: Boolean) {
        draftTemplateDao.setTemplateFavorite(templateId, isFavorite)
    }

    suspend fun incrementTemplateUsage(templateId: String) {
        draftTemplateDao.incrementUsage(templateId)
    }

    suspend fun saveCustomTemplate(template: DraftTemplateEntity) {
        draftTemplateDao.insertTemplate(template)
    }

    suspend fun deleteTemplate(templateId: String) {
        draftTemplateDao.deleteTemplateById(templateId)
    }

    /**
     * Instantiates a new user document from a draft template by substituting placeholder variables.
     */
    suspend fun createDocumentFromTemplate(
        template: DraftTemplateEntity,
        caseId: String? = null,
        title: String,
        variableValues: Map<String, String>,
        language: String = "EN",
        recipientParty: String? = null,
        jurisdictionCourt: String? = null
    ): UserLegalDocumentEntity {
        val baseBody = if (language == "TA") template.templateBodyTa else template.templateBodyEn
        var populatedContent = baseBody

        variableValues.forEach { (key, value) ->
            val placeholder = "[${key.uppercase()}]"
            populatedContent = populatedContent.replace(placeholder, value)
        }

        incrementTemplateUsage(template.templateId)

        return saveLegalDocument(
            title = title,
            documentType = template.documentType,
            category = template.disputeCategory,
            content = populatedContent,
            caseId = caseId,
            recipientParty = recipientParty,
            jurisdictionCourt = jurisdictionCourt,
            statutoryActRef = template.statuteRef,
            language = language,
            isDraft = true,
            notes = "Created from statutory template: ${template.templateTitleEn}"
        )
    }

    /**
     * Seeds default statutory legal templates into the local Room database for full offline capability.
     */
    suspend fun seedDefaultStatutoryTemplatesIfEmpty() {
        val count = draftTemplateDao.getTemplateCount()
        if (count > 0) return

        val defaultTemplates = listOf(
            DraftTemplateEntity(
                templateId = "tpl_138_ni_notice",
                templateTitleEn = "Statutory 15-Day Demand Notice (Section 138 NI Act)",
                templateTitleTa = "15 நாள் சட்டபூர்வ அறிவிப்பு (138 காசோலை மோசடி சட்டம்)",
                disputeCategory = DisputeCategory.CYBER_FINANCIAL_FRAUD,
                documentType = "STATUTORY_NOTICE",
                statuteRef = "Section 138 & 142, Negotiable Instruments Act, 1881",
                descriptionEn = "Mandatory formal demand notice served within 30 days of cheque dishonour/bouncing memo.",
                descriptionTa = "காசோலை பவுன்ஸ் ஆன 30 நாட்களுக்குள் அனுப்ப வேண்டிய கட்டாய சட்ட அறிவிப்பு.",
                templateBodyEn = """
FORMAL STATUTORY NOTICE OF DEMAND
UNDER SECTION 138 OF THE NEGOTIABLE INSTRUMENTS ACT, 1881

TO:
[DRAWEE_NAME]
[DRAWEE_ADDRESS]

UNDER INSTRUCTIONS FROM AND ON BEHALF OF MY CLIENT:
[PAYEE_NAME]
Residing at: [PAYEE_ADDRESS]

Sir / Madam,

Under instructions from my client above named, I do hereby serve upon you this Statutory Notice of Demand:

1. That towards discharge of lawful legally enforceable debt / liability, you issued Cheque No. [CHEQUE_NUMBER] dated [CHEQUE_DATE] for a sum of ₹[AMOUNT] drawn on [BANK_NAME].

2. That my client presented the said cheque for encashment through [PAYEE_BANK]. However, the same was returned unpaid vide Bank Memo dated [RETURN_MEMO_DATE] with reason: 'FUNDS INSUFFICIENT'.

3. You are hereby called upon to pay the entire cheque amount of ₹[AMOUNT] within FIFTEEN (15) DAYS from the date of receipt of this notice.

4. Failure to comply shall compel my client to initiate criminal prosecution under Section 138 & 142 of the Negotiable Instruments Act before the Jurisdictional Judicial Magistrate Court at your sole risk and costs.

Yours faithfully,
Advocate for the Payee
                """.trimIndent(),
                templateBodyTa = """
சட்டபூர்வ கோரிக்கை அறிவிப்பு
(மாற்றுமுறை ஆவணச் சட்டம், 1881 பிரிவு 138-ன் கீழ்)

பெறுநர்:
[DRAWEE_NAME]
[DRAWEE_ADDRESS]

என் கட்சிக்காரர் சார்பாக அனுப்பப்படும் அறிவிப்பு:
[PAYEE_NAME]
[PAYEE_ADDRESS]

ஐயா / அம்மா,

சட்டபூர்வ கடனை அடைப்பதற்காக நீங்கள் வழங்கிய காசோலை எண் [CHEQUE_NUMBER], நாள் [CHEQUE_DATE], தொகை ₹[AMOUNT] வங்கியில் போதிய பணம் இல்லாத காரணத்தால் [RETURN_MEMO_DATE] அன்று பவுன்ஸ் ஆகியுள்ளது.

இந்த அறிவிப்பு கிடைத்த 15 நாட்களுக்குள் மேற்படி தொகை ₹[AMOUNT]-ஐ என் கட்சிக்காரரிடம் ஒப்படைக்குமாறு கேட்டுக்கொள்ளப்படுகிறீர்கள்.

தவறும் பட்சத்தில், குற்றவியல் நீதிமன்றத்தில் உங்கள் மீது பிரிவு 138-ன் கீழ் வழக்கு தொடரப்படும்.

இவண்,
வக்கீல்
                """.trimIndent(),
                variablePlaceholders = listOf("DRAWEE_NAME", "DRAWEE_ADDRESS", "PAYEE_NAME", "PAYEE_ADDRESS", "CHEQUE_NUMBER", "CHEQUE_DATE", "AMOUNT", "BANK_NAME", "PAYEE_BANK", "RETURN_MEMO_DATE"),
                applicableForums = listOf("Judicial Magistrate Court", "Metropolitan Magistrate Court"),
                isSystemDefault = true,
                isFavorite = true
            ),
            DraftTemplateEntity(
                templateId = "tpl_cpa_2019_petition",
                templateTitleEn = "Consumer Grievance Petition (Consumer Protection Act 2019)",
                templateTitleTa = "நுகர்வோர் குறைதீர்ப்பு மனு (நுகர்வோர் பாதுகாப்பு சட்டம் 2019)",
                disputeCategory = DisputeCategory.CONSUMER_GRIEVANCE,
                documentType = "COMPLAINT_PETITION",
                statuteRef = "Section 35, Consumer Protection Act, 2019",
                descriptionEn = "Model consumer complaint petition filed before District Consumer Commission (DCDRC).",
                descriptionTa = "மாவட்ட நுகர்வோர் குறைதீர்க்கும் ஆணையத்தில் தாக்கல் செய்யப்படும் மாதிரி மனு.",
                templateBodyEn = """
BEFORE THE DISTRICT CONSUMER DISPUTES REDRESSAL COMMISSION AT [DISTRICT_NAME]
CONSUMER COMPLAINT NO. _____ / 2026
Under Section 35 of the Consumer Protection Act, 2019

IN THE MATTER OF:
[COMPLAINANT_NAME]
Residing at: [COMPLAINANT_ADDRESS]
... COMPLAINANT

VERSUS

[OPPOSING_PARTY_NAME]
Having office at: [OPPOSING_PARTY_ADDRESS]
... OPPOSITE PARTY / RESPONDENT

COMPLAINT UNDER SECTION 35 FOR DEFICIENCY OF SERVICE AND UNFAIR TRADE PRACTICE

MOST RESPECTFULLY SHOWETH:
1. The Complainant is a 'Consumer' within Section 2(7) of CPA 2019, having purchased [PRODUCT_SERVICE_NAME] on [TRANSACTION_DATE] vide Invoice No. [INVOICE_NUMBER] for a consideration of ₹[AMOUNT].

2. The Opposite Party committed grave deficiency in service by [NARRATIVE_DEFECT].

3. Despite multiple written requests and notices, the Opposite Party failed to rectify the defect or refund the money.

PRAYER:
It is therefore respectfully prayed that this Hon'ble Commission may be pleased to:
a) Direct the Opposite Party to refund the sum of ₹[AMOUNT] with interest @ 12% p.a.
b) Direct payment of ₹[COMPENSATION_AMOUNT] towards compensation for mental agony.
c) Award ₹[LITIGATION_COSTS] towards costs of litigation.

VERIFICATION:
Verified at [DISTRICT_NAME] that the statements above are true to my personal knowledge.
Complainant
                """.trimIndent(),
                templateBodyTa = """
மாவட்ட நுகர்வோர் குறைதீர்க்கும் ஆணையம் - [DISTRICT_NAME]
நுகர்வோர் பாதுகாப்புச் சட்டம் 2019, பிரிவு 35-ன் கீழ் மனு

மனுதாரர்:
[COMPLAINANT_NAME]
[COMPLAINANT_ADDRESS]

எதிர்மனுதாரர்:
[OPPOSING_PARTY_NAME]
[OPPOSING_PARTY_ADDRESS]

சேவை குறைபாடு மற்றும் முறையற்ற வர்த்தக நடைமுறைக்கான புகார் மனு:

1. மனுதாரர் [TRANSACTION_DATE] அன்று ₹[AMOUNT] செலுத்தி [PRODUCT_SERVICE_NAME] வாங்கினார்.
2. எதிர்மனுதாரர் [NARRATIVE_DEFECT] மூலமாக சேவை குறைபாடு செய்துள்ளார்.

பிரார்த்தனை:
எதிர்மனுதாரர் மனுதாரருக்கு ₹[AMOUNT] தொகையை 12% வட்டியுடன் திருப்பித் தரவும், மன உளைச்சலுக்கு நஷ்டஈடு வழங்கவும் ஆணையிடுமாறு கேட்டுக்கொள்ளப்படுகிறது.

மனுதாரர்
                """.trimIndent(),
                variablePlaceholders = listOf("DISTRICT_NAME", "COMPLAINANT_NAME", "COMPLAINANT_ADDRESS", "OPPOSING_PARTY_NAME", "OPPOSING_PARTY_ADDRESS", "PRODUCT_SERVICE_NAME", "TRANSACTION_DATE", "INVOICE_NUMBER", "AMOUNT", "NARRATIVE_DEFECT", "COMPENSATION_AMOUNT", "LITIGATION_COSTS"),
                applicableForums = listOf("District Consumer Commission (DCDRC)", "State Consumer Commission (SCDRC)"),
                isSystemDefault = true,
                isFavorite = true
            ),
            DraftTemplateEntity(
                templateId = "tpl_rti_2005_form_a",
                templateTitleEn = "RTI Information Request Application (Form A)",
                templateTitleTa = "தகவல் அறியும் உரிமை சட்டம் (படிவம் A மனு)",
                disputeCategory = DisputeCategory.GOVT_RTI,
                documentType = "RTI_APPLICATION",
                statuteRef = "Section 6(1), Right to Information Act, 2005",
                descriptionEn = "Standard statutory application to Public Information Officer (PIO) seeking government records.",
                descriptionTa = "பொது தகவல் அதிகாரியிடம் அரசு ஆவணங்களைக் கோரும் அதிகாரப்பூர்வ மனு.",
                templateBodyEn = """
APPLICATION FOR OBTAINING INFORMATION UNDER SECTION 6(1)
OF THE RIGHT TO INFORMATION ACT, 2005

TO:
The Public Information Officer (PIO)
Office of [DEPARTMENT_OR_OFFICE]
[OFFICE_ADDRESS]

1. Full Name of Applicant: [APPLICANT_NAME]
2. Address: [APPLICANT_ADDRESS]
3. Particulars of Information Sought:
   Subject: [RTI_SUBJECT]
   Detailed Specific Points:
   a) [QUESTION_1]
   b) [QUESTION_2]
   c) Certified copies of relevant file notings, orders, and action-taken reports.
4. Period to which the information relates: [PERIOD_TIMEFRAME]
5. Application Fee Details:
   Court Fee Stamp / IPO No. [FEE_RECEIPT_NO] of ₹10 enclosed.
6. The information sought does not fall under the exemptions of Section 8 or 9 of the RTI Act.

Date: [APPLICATION_DATE]
Place: [APPLICANT_CITY]
Signature of Applicant
                """.trimIndent(),
                templateBodyTa = """
தகவல் அறியும் உரிமைச் சட்டம் 2005, பிரிவு 6(1)-ன் கீழ் விண்ணப்பம்

பெறுநர்:
பொதுத் தகவல் அலுவலர் (PIO)
[DEPARTMENT_OR_OFFICE]
[OFFICE_ADDRESS]

1. விண்ணப்பதாரர் பெயர்: [APPLICANT_NAME]
2. முகவரி: [APPLICANT_ADDRESS]
3. கோரப்படும் தகவல்கள்:
   விஷயம்: [RTI_SUBJECT]
   விவரங்கள்:
   அ) [QUESTION_1]
   ஆ) [QUESTION_2]
4. தகவல் தொடர்புடைய கால அளவு: [PERIOD_TIMEFRAME]
5. கட்டண விபரம்: ₹10 கட்டணம் செலுத்தப்பட்டுள்ளது (ரசீது: [FEE_RECEIPT_NO]).

விண்ணப்பதாரர் கையொப்பம்
                """.trimIndent(),
                variablePlaceholders = listOf("DEPARTMENT_OR_OFFICE", "OFFICE_ADDRESS", "APPLICANT_NAME", "APPLICANT_ADDRESS", "RTI_SUBJECT", "QUESTION_1", "QUESTION_2", "PERIOD_TIMEFRAME", "FEE_RECEIPT_NO", "APPLICATION_DATE", "APPLICANT_CITY"),
                applicableForums = listOf("Central Information Commission (CIC)", "State Information Commission (SIC)"),
                isSystemDefault = true,
                isFavorite = false
            ),
            DraftTemplateEntity(
                templateId = "tpl_tenancy_deposit_notice",
                templateTitleEn = "Tenancy Security Deposit Demand & Eviction Settlement Notice",
                templateTitleTa = "வாடகை முன்பணம் திரும்ப பெறுதல் & குத்தகை தீர்வு அறிவிப்பு",
                disputeCategory = DisputeCategory.TENANCY_RENT,
                documentType = "TENANCY_NOTICE",
                statuteRef = "TN Regulation of Rights and Responsibilities of Landlords and Tenants Act",
                descriptionEn = "Demand notice to landlord for refund of security deposit balance post vacating premises.",
                descriptionTa = "வீடு காலி செய்த பின் முன்பணத்தை திரும்ப அளிக்கக் கோரி வீட்டு உரிமையாளருக்கு அனுப்பும் நோட்டீஸ்.",
                templateBodyEn = """
LEGAL DEMAND NOTICE FOR IMMEDIATE REFUND OF TENANCY SECURITY DEPOSIT

TO:
[LANDLORD_NAME]
[LANDLORD_ADDRESS]

FROM:
[TENANT_NAME]
[TENANT_ADDRESS]

Sir / Madam,
1. That I was a lawful tenant in respect of premises located at [PREMISES_ADDRESS] under Rental Agreement dated [AGREEMENT_DATE].
2. That at the inception of tenancy, I paid an interest-free refundable security deposit of ₹[DEPOSIT_AMOUNT].
3. That I duly handed over vacant and peaceful possession of the premises to you on [VACATED_DATE] along with keys.
4. Despite lapse of 30 days and no damage to property, you have unlawfully withheld ₹[WITHHELD_AMOUNT].
5. You are hereby called upon to transfer the full refundable sum of ₹[WITHHELD_AMOUNT] within 15 days of this notice, failing which I shall initiate proceedings before the Rent Court with statutory interest and costs.

Tenant
                """.trimIndent(),
                templateBodyTa = """
வாடகை முன்பணத்தை உடனடியாக திரும்ப வழங்கக் கோரும் சட்ட அறிவிப்பு

பெறுநர்:
[LANDLORD_NAME]
[LANDLORD_ADDRESS]

அனுப்புநர்:
[TENANT_NAME]
[TENANT_ADDRESS]

நான் [PREMISES_ADDRESS] முகவரியில் உள்ள உங்கள் வீட்டில் வாடகைக்கு இருந்து, [VACATED_DATE] அன்று வீட்டை காலி செய்து சாவியை ஒப்படைத்தேன். நான் செலுத்திய முன்பணம் ₹[DEPOSIT_AMOUNT]-ல் நிலுவையிலுள்ள ₹[WITHHELD_AMOUNT]-ஐ 15 நாட்களுக்குள் திருப்பித் தர வேண்டும். தவறும் பட்சத்தில் வாடகை நீதிமன்றத்தில் வழக்கு தொடரப்படும்.

வாடகைதாரர்
                """.trimIndent(),
                variablePlaceholders = listOf("LANDLORD_NAME", "LANDLORD_ADDRESS", "TENANT_NAME", "TENANT_ADDRESS", "PREMISES_ADDRESS", "AGREEMENT_DATE", "DEPOSIT_AMOUNT", "VACATED_DATE", "WITHHELD_AMOUNT"),
                applicableForums = listOf("Rent Court", "Rent Tribunal"),
                isSystemDefault = true,
                isFavorite = false
            ),
            DraftTemplateEntity(
                templateId = "tpl_msme_delayed_payment",
                templateTitleEn = "MSME Overdue Payment Demand Notice (Sec 15 & 16 MSMED Act)",
                templateTitleTa = "MSME நிலுவைத் தொகை வசூல் அறிவிப்பு (MSMED சட்டம்)",
                disputeCategory = DisputeCategory.CONSUMER_GRIEVANCE,
                documentType = "COMMERCIAL_DEMAND",
                statuteRef = "Section 15, 16 & 18, MSMED Act, 2006",
                descriptionEn = "Statutory demand for MSME payment recovery carrying 3x RBI compound interest.",
                descriptionTa = "ஆர்பிஐ வட்டி விகிதத்தின்படி 3 மடங்கு கூட்டு வட்டியுடன் நிலுவை வசூலிக்கும் நோட்டீஸ்.",
                templateBodyEn = """
STATUTORY RECOVERY NOTICE UNDER SECTION 15 & 16 OF THE MSMED ACT, 2006

TO:
[BUYER_COMPANY_NAME]
[BUYER_REGISTERED_ADDRESS]

FROM:
[SUPPLIER_ENTERPRISE_NAME]
UDYAM Registration No: [UDYAM_REG_NUMBER]
[SUPPLIER_ADDRESS]

SUBJECT: DEMAND FOR PAYMENT OF OVERDUE INVOICES WITH COMPOUND INTEREST

Sir / Madam,
Our enterprise delivered goods/services under Purchase Order [PO_NUMBER] vide Invoice(s) [INVOICE_NUMBERS] dated [INVOICE_DATE] for total principal value ₹[PRINCIPAL_AMOUNT].
Under Section 15 of MSMED Act 2006, payments must be cleared within 45 days. You have delayed payment by [DAYS_DELAYED] days.
You are liable to pay monthly compound interest at three times (3x) the RBI bank rate under Section 16.
DEMAND:
1. Outstanding Principal: ₹[PRINCIPAL_AMOUNT]
2. Compound Interest Accrued: ₹[INTEREST_AMOUNT]
TOTAL PAYABLE: ₹[TOTAL_AMOUNT] within 15 days, failing which an application will be filed before MSME Samadhaan / Facilitation Council.

Authorized Signatory
                """.trimIndent(),
                templateBodyTa = """
MSMED சட்டம் 2006 பிரிவு 15 & 16-ன் கீழ் நிலுவைத் தொகை மீட்பு அறிவிப்பு

பெறுநர்:
[BUYER_COMPANY_NAME]
[BUYER_REGISTERED_ADDRESS]

அனுப்புநர்:
[SUPPLIER_ENTERPRISE_NAME]
உத்யம் பதிவு எண்: [UDYAM_REG_NUMBER]

பொருட்கள்/சேவை வழங்கியதன் பில் தொகை ₹[PRINCIPAL_AMOUNT] மற்றும் தாமதத்திற்கான கூட்டு வட்டி ₹[INTEREST_AMOUNT] சேர்த்து மொத்தம் ₹[TOTAL_AMOUNT]-ஐ 15 நாட்களுக்குள் செலுத்த வேண்டும். தவறும் பட்சத்தில் MSME சமாதான் கவுன்சிலில் வழக்கு தொடரப்படும்.

அங்கீகரிக்கப்பட்ட நபர்
                """.trimIndent(),
                variablePlaceholders = listOf("BUYER_COMPANY_NAME", "BUYER_REGISTERED_ADDRESS", "SUPPLIER_ENTERPRISE_NAME", "UDYAM_REG_NUMBER", "SUPPLIER_ADDRESS", "PO_NUMBER", "INVOICE_NUMBERS", "INVOICE_DATE", "PRINCIPAL_AMOUNT", "DAYS_DELAYED", "INTEREST_AMOUNT", "TOTAL_AMOUNT"),
                applicableForums = listOf("MSME Facilitation Council", "Commercial Court"),
                isSystemDefault = true,
                isFavorite = false
            ),
            DraftTemplateEntity(
                templateId = "tpl_cyber_freeze_notice",
                templateTitleEn = "Cyber Fraud Debit Grievance & Account Freeze Petition",
                templateTitleTa = "சைபர் மோசடி புகார் & வங்கி கணக்கு முடக்க மனு",
                disputeCategory = DisputeCategory.CYBER_FINANCIAL_FRAUD,
                documentType = "COMPLAINT_PETITION",
                statuteRef = "Section 66D Information Technology Act & RBI Zero-Liability Mandate",
                descriptionEn = "Emergency complaint to Bank Nodal Officer and Cyber Police for freezing fraud beneficiary.",
                descriptionTa = "மோசடி கணக்கை உடனடியாக முடக்கவும் பணத்தை மீட்கவும் வங்கி மற்றும் சைபர் காவல்துறைக்கு மனு.",
                templateBodyEn = """
URGENT COMPLAINT FOR FREEZING BENEFICIARY ACCOUNT & ZERO LIABILITY CHARGEBACK
UNDER RBI CUSTOMER PROTECTION DIRECTIVE & IT ACT 2000

TO:
1. The Nodal Grievance Officer, [VICTIM_BANK_NAME]
2. Officer-in-Charge, Cyber Crime Police Station / Portal (Ack: [CYBER_ACK_NO])

FROM:
[VICTIM_NAME]
Account No: [VICTIM_ACCOUNT_NO]
Phone: [VICTIM_PHONE]

1. On [INCIDENT_TIMESTAMP], an unauthorized fraudulent electronic transaction occurred from my account for ₹[DEBIT_AMOUNT] via [TRANSACTION_TYPE: UPI/NEFT/IMPS].
2. Destination Fraudulent Beneficiary / UPI VPA: [BENEFICIARY_INFO].
3. I reported this incident within the Golden Hour to 1930 Helpline.
4. REQUEST:
   a) Immediately communicate with destination bank to FREEZE the fraudulent funds.
   b) Credit ₹[DEBIT_AMOUNT] as shadow credit under RBI Zero Liability circular.

Victim / Complainant
                """.trimIndent(),
                templateBodyTa = """
சைபர் மோசடி பணம் முடக்கம் மற்றும் திரும்பப் பெறுதல் கோரிக்கை மனு

பெறுநர்:
வங்கி குறைதீர்ப்பு அதிகாரி, [VICTIM_BANK_NAME]
மற்றும் சைபர் கிரைம் காவல் நிலையம்

விண்ணப்பதாரர்: [VICTIM_NAME], கணக்கு எண்: [VICTIM_ACCOUNT_NO]

[INCIDENT_TIMESTAMP] அன்று என் கணக்கிலிருந்து ₹[DEBIT_AMOUNT] முறைகேடாக எடுக்கப்பட்டு [BENEFICIARY_INFO] கணக்கிற்கு மாற்றப்பட்டுள்ளது.
ஆர்பிஐ வழிகாட்டுதலின்படி உடனடியாக அந்த மோசடி கணக்கை முடக்கி என் பணத்தை திரும்ப வழங்க உத்தரவிடுமாறு கோருகிறேன்.

மனுதாரர்
                """.trimIndent(),
                variablePlaceholders = listOf("VICTIM_BANK_NAME", "CYBER_ACK_NO", "VICTIM_NAME", "VICTIM_ACCOUNT_NO", "VICTIM_PHONE", "INCIDENT_TIMESTAMP", "DEBIT_AMOUNT", "TRANSACTION_TYPE", "BENEFICIARY_INFO"),
                applicableForums = listOf("National Cyber Crime Reporting Portal", "Banking Ombudsman"),
                isSystemDefault = true,
                isFavorite = true
            )
        )

        draftTemplateDao.insertTemplates(defaultTemplates)
    }
}
