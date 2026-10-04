package com.justra.app.data.api

import com.justra.app.domain.model.DisputeCategory

data class LegalStatute(
    val actName: String,
    val actNameTa: String,
    val section: String,
    val title: String,
    val summaryEn: String,
    val summaryTa: String,
    val applicableForum: String,
    val limitationPeriod: String,
    val primaryRelief: String
)

object LegalStatuteKnowledge {
    val STATUTES = listOf(
        LegalStatute(
            actName = "Consumer Protection Act, 2019",
            actNameTa = "நுகர்வோர் பாதுகாப்புச் சட்டம், 2019",
            section = "Section 35 / Section 2(47)",
            title = "Filing Complaint for Unfair Trade Practice / Defective Goods",
            summaryEn = "Provides recourse against misleading ads, deficient service, non-delivery, refusal of legitimate refunds, or spurious goods.",
            summaryTa = "குறைபாடுள்ள பொருட்கள், போலியான விளம்பரம், சேவை குறைபாடு, மற்றும் பணத்தைத் திருப்பித் தர மறுப்பதற்கு எதிரான தீர்வு.",
            applicableForum = "District Consumer Disputes Redressal Commission (DCDRC) / e-Daakhil Portal",
            limitationPeriod = "2 years from the date on which the cause of action arose",
            primaryRelief = "Full Refund with statutory interest, replacement of goods, and compensation for mental agony"
        ),
        LegalStatute(
            actName = "Information Technology Act, 2000 & BNS, 2023",
            actNameTa = "தகவல் தொழில்நுட்பச் சட்டம், 2000 & BNS, 2023",
            section = "Section 66D IT Act & Section 318(4) BNS",
            title = "Cheating by Personation using Computer Resource / Cyber Fraud",
            summaryEn = "Punishes fraudulent UPI payment transfers, unauthorized bank deductions, phishing scams, OTP extortion, and online impersonation.",
            summaryTa = "கணினி அல்லது செல்போன் வழியே ஆள்மாறாட்டம் செய்து பண மோசடி, போலி UPI பரிவர்த்தனைகள், OTP திருட்டுக்கு எதிரான தண்டனை.",
            applicableForum = "National Cyber Crime Reporting Portal (1930 / cybercrime.gov.in) & Cyber Crime Police Station",
            limitationPeriod = "Immediate (Golden Hour within 24 hours for bank account freeze)",
            primaryRelief = "Freezing suspect mule accounts via 1930 financial fraud helpline & recovery of lost funds"
        ),
        LegalStatute(
            actName = "Tamil Nadu Regulation of Rights and Responsibilities of Landlords and Tenants Act, 2017",
            actNameTa = "தமிழ்நாடு வாடகை மற்றும் நில உரிமையாளர்-வாடகைதாரர் உரிமைகள் சட்டம், 2017",
            section = "Section 4 & Section 21",
            title = "Mandatory Tenancy Agreement & Unlawful Withholding of Security Deposit",
            summaryEn = "Requires written tenancy agreement registered with the Rent Authority; strictly limits advance security deposits and regulates lawful eviction grounds.",
            summaryTa = "எழுத்துப்பூர்வ வாடகை ஒப்பந்தம் கட்டாயம்; முன்பணத்தை காரணமின்றி பிடித்து வைப்பது மற்றும் சட்டவிரோத வெளியேற்றத்திற்கு எதிரான பாதுகாப்பு.",
            applicableForum = "Rent Court / Rent Tribunal presided by the jurisdictional Deputy Collector / Sub-Judge",
            limitationPeriod = "90 days from default or refusal to refund deposit",
            primaryRelief = "Recovery of security deposit with penalty interest and protection against forceful dispossession"
        ),
        LegalStatute(
            actName = "Payment of Wages Act, 1936 & Industrial Disputes Act, 1947",
            actNameTa = "ஊதிய வழங்கல் சட்டம், 1936 & தொழில் தகராறுகள் சட்டம், 1947",
            section = "Section 15 Payment of Wages Act & Section 33C(2) IDA",
            title = "Claim for Unpaid Salary, Delayed Wages & Wrongful Withholding",
            summaryEn = "Empowers employees and workmen to recover unpaid salary, withheld incentives, severance dues, or notice pay deductions.",
            summaryTa = "வழங்கப்படாத சம்பளம், தாமதமான ஊதியம், அனுபவச் சான்றிதழ் நிறுத்தி வைப்பு ஆகியவற்றைப் பெற தொழிலாளர் ஆணையரிடம் முறையீடு.",
            applicableForum = "Labour Court / Assistant Commissioner of Labour (Grievances)",
            limitationPeriod = "12 months from the date wages were due",
            primaryRelief = "Direct order to release full pending salary along with up to 10x compensation penalty"
        ),
        LegalStatute(
            actName = "Protection of Women from Domestic Violence Act, 2005 & PoSH Act, 2013",
            actNameTa = "பெண்களுக்கு எதிரான குடும்ப வன்முறை பாதுகாப்புச் சட்டம், 2005",
            section = "Section 12 PWDVA & Section 9 PoSH Act",
            title = "Protection, Residence, and Monetary Relief for Women",
            summaryEn = "Guarantees rights against domestic abuse, emotional harassment, unlawful dispossession from shared household, or workplace harassment.",
            summaryTa = "குடும்ப வன்முறை, பணியிட பாலியல் தொல்லை, வீட்டை விட்டு வெளியேற்றுதல் ஆகியவற்றிலிருந்து உடனடி நீதித்துறை பாதுகாப்பு.",
            applicableForum = "Judicial Magistrate Court / Protection Officer (Social Welfare Dept) / Internal Complaints Committee",
            limitationPeriod = "Immediate application for emergency protection order",
            primaryRelief = "Ex-parte Protection Orders, Residence Orders, and monthly interim maintenance relief"
        ),
        LegalStatute(
            actName = "Right to Information Act, 2005",
            actNameTa = "தகவல் அறியும் உரிமைச் சட்டம், 2005",
            section = "Section 6(1) & Section 19",
            title = "Request for Public Records, File Notings & Status Reports",
            summaryEn = "Enables every citizen to obtain government documents, inspection of works, reasons for administrative delays, and municipal records.",
            summaryTa = "அரசு அலுவலக கோப்புகள், திட்டப்பணிகள், சான்றிதழ் தாமதங்களுக்கான காரணங்களை 30 நாட்களுக்குள் பெறும் உரிமை.",
            applicableForum = "Public Information Officer (PIO) -> First Appellate Authority -> State Information Commission",
            limitationPeriod = "30-day statutory response deadline (48 hours if life and liberty is involved)",
            primaryRelief = "Supply of certified public records and statutory penalty against defaulting public officers"
        )
    )

    fun getStatuteForCategory(category: DisputeCategory): LegalStatute {
        return when (category) {
            DisputeCategory.CONSUMER_GRIEVANCE -> STATUTES[0]
            DisputeCategory.CYBER_FINANCIAL_FRAUD, DisputeCategory.SCAM_ANALYSIS -> STATUTES[1]
            DisputeCategory.TENANCY_RENT -> STATUTES[2]
            DisputeCategory.EMPLOYMENT_SALARY -> STATUTES[3]
            DisputeCategory.WOMEN_RIGHTS -> STATUTES[4]
            DisputeCategory.LAND_PROPERTY -> STATUTES[2]
            DisputeCategory.GOVT_RTI -> STATUTES[5]
        }
    }
}
