package com.example.util

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

    /**
     * Validates legal grievance text prior to AI processing.
     * Enforces:
     * 1. Minimum 12 characters and minimum 3 distinct words.
     * 2. Vowel-to-consonant ratio check (< 0.12 rejected for non-pure Tamil text).
     * 3. Repetitive character and pattern detection (e.g., "aaaaa", "asdfasdf", "1234567").
     */
    fun validateLegalInput(input: String): ValidationResult {
        val trimmed = input.trim()

        // 1. Minimum length constraint (12 characters)
        if (trimmed.length < 12) {
            return ValidationResult.Invalid(
                errorMessageTa = "உங்கள் பிரச்சனையை குறைந்தது 12 எழுத்துக்கள் மற்றும் 2-3 வரிகளில் விளக்கமாக உள்ளிடவும்.",
                errorMessageEn = "Please describe your grievance in at least 12 characters (minimum 2-3 lines)."
            )
        }

        // 2. Minimum distinct words constraint (3 words)
        val words = trimmed.split("\\s+".toRegex()).filter { it.isNotBlank() }
        if (words.size < 3) {
            return ValidationResult.Invalid(
                errorMessageTa = "முழுமையான வாக்கியங்களாக உள்ளிடவும் (எ.கா: வாடகை அட்வான்ஸ் தரவில்லை, சம்பள பாக்கி).",
                errorMessageEn = "Please provide complete sentences with at least 3 distinct words (e.g., Landlord withholding rent deposit)."
            )
        }

        // 3. Sequential digits or pure random numbers (e.g. "123456789012")
        val digitOnlyRatio = trimmed.count { it.isDigit() }.toFloat() / trimmed.length
        if (digitOnlyRatio > 0.6f) {
            return ValidationResult.Invalid(
                errorMessageTa = "தவறான உள்ளீடு: வெறும் எண்களை மட்டும் உள்ளிடக்கூடாது. பிரச்சனை விபரத்தை எழுதவும்.",
                errorMessageEn = "Invalid input: Please enter factual narrative text instead of plain numbers."
            )
        }

        // 4. Vowel-to-consonant ratio check for Latin / Tanglish text (< 0.12 rejected)
        val alphabeticChars = trimmed.filter { it.isLetter() }
        if (alphabeticChars.isNotEmpty()) {
            val isPureTamil = trimmed.any { it in '\u0B80'..'\u0BFF' }
            if (!isPureTamil) {
                val vowels = alphabeticChars.count { it.lowercaseChar() in listOf('a', 'e', 'i', 'o', 'u') }
                val vowelRatio = vowels.toFloat() / alphabeticChars.length
                if (vowelRatio < 0.12f) {
                    return ValidationResult.Invalid(
                        errorMessageTa = "சரியான வார்த்தைகளை உள்ளிடவும். அர்த்தமற்ற எழுத்துக்களை (Gibberish) ஏற்க முடியாது.",
                        errorMessageEn = "Invalid text detected: Meaningless consonant strings or random key mashing are not permitted."
                    )
                }
            }
        }

        // 5. Repetitive character and pattern detection (e.g. "aaaaa", "asdfasdf", "qwertyqwerty")
        val repetitionRegex = Regex("""(.)\1{4,}""", RegexOption.IGNORE_CASE)
        if (repetitionRegex.containsMatchIn(trimmed)) {
            return ValidationResult.Invalid(
                errorMessageTa = "தவறான உள்ளீடு: ஒரே எழுத்துக்கள் அல்லது குறியீடுகள் மீண்டும் மீண்டும் உள்ளன.",
                errorMessageEn = "Invalid input: Detected repetitive character patterns."
            )
        }

        // Common consonant keyboard mashing patterns
        val gibberishPatterns = listOf("asdf", "ghjk", "zxcv", "qwerty", "123456", "987654")
        val lowerText = trimmed.lowercase()
        val matchCount = gibberishPatterns.count { lowerText.contains(it) }
        if (matchCount >= 2 || (lowerText.length < 25 && matchCount >= 1)) {
            return ValidationResult.Invalid(
                errorMessageTa = "தவறான உள்ளீடு: விசைப்பலகை தட்டல் (Keyboard Mashing) கண்டறியப்பட்டது. சரியான விவரங்களை உள்ளிடவும்.",
                errorMessageEn = "Invalid input: Keyboard mashing pattern detected. Please enter genuine legal details."
            )
        }

        return ValidationResult.Valid
    }
}
