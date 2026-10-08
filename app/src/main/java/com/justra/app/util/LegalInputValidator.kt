package com.justra.app.util

/**
 * Deterministic Client-Side Heuristic Gate to reject arbitrary keystrokes,
 * sequential numbers, and consonant-mash gibberish (e.g., "asdfghjkl", "1234567").
 * Prevents unnecessary Gemini API calls and token consumption.
 */
object LegalInputValidator {

    sealed class ValidationResult {
        object Valid : ValidationResult()
        data class Invalid(val errorMessageTa: String, val errorMessageEn: String) : ValidationResult()
    }

    // Known single-word gibberish tokens (all lowercase) that are contextless and meaningless.
    private val SINGLE_WORD_GIBBERISH = setOf(
        "asdf", "qwer", "zxcv", "xyz", "abc", "test", "hello",
        "hi", "hey", "ok", "okay", "lol", "haha", "idk",
        "asd", "fgh", "jkl", "bnm", "sdf", "dfg"
    )

    // Scam-content indicator patterns: the scam checker MUST see at least one of these to proceed.
    private val SCAM_CONTENT_INDICATORS = listOf(
        "http", "https", "bit.ly", "t.me", "tinyurl", ".apk", "apk",
        "upi", "otp", "pin", "password", "kyc", "pan", "aadhaar",
        "bank", "account", "blocked", "deactivated", "freeze",
        "lottery", "prize", "won", "winner", "reward", "cashback",
        "refund", "electricity", "eb bill", "power cut", "min",
        "anydesk", "teamviewer", "rustdesk", "remote",
        "sms", "whatsapp", "telegram", "call", "number", "click",
        "download", "install", "verify", "update", "link", "urgent",
        "dear customer", "immediately", "மோசடி", "போலி", "மிரட்டல்",
        "பணம்", "கணக்கு", "வங்கி", "OTP", "APK"
    )

    /**
     * Validates legal grievance text prior to AI processing.
     * Enforces:
     * 1. Minimum 12 characters and minimum 3 coherent words.
     * 2. Sequential digit / random number rejection.
     * 3. Repetitive character pattern rejection.
     * 4. Keyboard mashing pattern rejection.
     * 5. Single-word gibberish/test input rejection.
     */
    fun validateLegalInput(input: String): ValidationResult {
        val trimmed = input.trim()

        // 1. Minimum length constraint (12 characters)
        if (trimmed.length < 12) {
            return ValidationResult.Invalid(
                errorMessageTa = "தவறான உள்ளீடு. தயவுசெய்து தெளிவான சட்டப் பிரச்சனை விளக்கத்தை கொடுங்கள் (குறைந்தது 3 வார்த்தைகள்).",
                errorMessageEn = "Invalid input. Please provide a clear and meaningful legal problem or incident description."
            )
        }

        // 2. Minimum 3 coherent words constraint
        val words = trimmed.split("\\s+".toRegex()).filter { it.isNotBlank() }
        if (words.size < 3) {
            return ValidationResult.Invalid(
                errorMessageTa = "தவறான உள்ளீடு. தயவுசெய்து தெளிவான சட்டப் பிரச்சனை விளக்கத்தை கொடுங்கள் (குறைந்தது 3 வார்த்தைகள்).",
                errorMessageEn = "Invalid input. Please provide a clear and meaningful legal problem or incident description."
            )
        }

        // 3. Sequential digits or pure random numbers (e.g. "123456789012")
        val digitOnlyRatio = trimmed.count { it.isDigit() }.toFloat() / trimmed.length
        if (digitOnlyRatio > 0.85f && trimmed.length > 8) {
            return ValidationResult.Invalid(
                errorMessageTa = "தவறான உள்ளீடு. தயவுசெய்து தெளிவான சட்டப் பிரச்சனை விளக்கத்தை கொடுங்கள்.",
                errorMessageEn = "Invalid input. Please provide a clear and meaningful legal problem or incident description."
            )
        }

        // 4. Repetitive character detection (e.g. "aaaaa", "zzzzz")
        val repetitionRegex = Regex("""(.)\1{5,}""", RegexOption.IGNORE_CASE)
        if (repetitionRegex.containsMatchIn(trimmed)) {
            return ValidationResult.Invalid(
                errorMessageTa = "தவறான உள்ளீடு. தயவுசெய்து தெளிவான சட்டப் பிரச்சனை விளக்கத்தை கொடுங்கள்.",
                errorMessageEn = "Invalid input. Please provide a clear and meaningful legal problem or incident description."
            )
        }

        // 5. Common keyboard mashing patterns
        val gibberishPatterns = listOf("asdfghjkl", "zxcvbnm", "qwertyuiop")
        val lowerText = trimmed.lowercase()
        if (gibberishPatterns.any { lowerText.contains(it) }) {
            return ValidationResult.Invalid(
                errorMessageTa = "தவறான உள்ளீடு. தயவுசெய்து தெளிவான சட்டப் பிரச்சனை விளக்கத்தை கொடுங்கள்.",
                errorMessageEn = "Invalid input. Please provide a clear and meaningful legal problem or incident description."
            )
        }

        // 6. Single-word gibberish / test token check
        if (words.size <= 2 && words.all { it.lowercase() in SINGLE_WORD_GIBBERISH }) {
            return ValidationResult.Invalid(
                errorMessageTa = "தவறான உள்ளீடு. தயவுசெய்து தெளிவான சட்டப் பிரச்சனை விளக்கத்தை கொடுங்கள்.",
                errorMessageEn = "Invalid input. Please provide a clear and meaningful legal problem or incident description."
            )
        }

        return ValidationResult.Valid
    }

    /**
     * Validates that the scam checker input actually contains scam-relevant content:
     * suspicious SMS text, phishing URL, WhatsApp alert, or unauthorized UPI prompt.
     * Rejects generic text, legal grievances, or arbitrary content that belongs in other modules.
     */
    fun validateScamCheckerInput(input: String): ValidationResult {
        val trimmed = input.trim()

        // Basic length check
        if (trimmed.length < 10) {
            return ValidationResult.Invalid(
                errorMessageTa = "சந்தேகத்திற்குரிய SMS / URL / WhatsApp செய்தியை ஒட்டவும்.",
                errorMessageEn = "Please paste the suspicious SMS, phishing URL, or WhatsApp message to analyze."
            )
        }

        // Must contain at least one scam content indicator
        val lowerText = trimmed.lowercase()
        val hasScamContent = SCAM_CONTENT_INDICATORS.any { lowerText.contains(it) }
        if (!hasScamContent) {
            return ValidationResult.Invalid(
                errorMessageTa = "தவறான உள்ளீடு. இந்த பகுப்பாய்வி SMS மோசடி, ஃபிஷிங் URL, WhatsApp எச்சரிக்கை மற்றும் UPI மோசடி செய்திகளை மட்டுமே சோதிக்கும். பொதுவான உரை இங்கு செல்லாது.",
                errorMessageEn = "Invalid input. The Scam Checker analyzes suspicious SMS messages, phishing URLs, WhatsApp fraud alerts, and unauthorized UPI prompts only. Please paste the exact suspicious content."
            )
        }

        return ValidationResult.Valid
    }
}
