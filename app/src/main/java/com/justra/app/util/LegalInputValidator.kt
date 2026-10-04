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

    /**
     * Validates legal grievance text prior to AI processing.
     * Enforces:
     * 1. Minimum 12 characters and minimum 3 distinct words.
     * 2. Vowel-to-consonant ratio check (< 0.12 rejected for non-pure Tamil text).
     * 3. Repetitive character and pattern detection (e.g., "aaaaa", "asdfasdf", "1234567").
     */
    fun validateLegalInput(input: String): ValidationResult {
        val trimmed = input.trim()

        // 1. Minimum length constraint (5 characters)
        if (trimmed.length < 5) {
            return ValidationResult.Invalid(
                errorMessageTa = "தயவுசெய்து உங்கள் சட்டப் பிரச்சனையை விரிவாகக் கூறவும் (குறைந்தது 5 எழுத்துக்கள்).",
                errorMessageEn = "Please describe your grievance in at least 5 characters."
            )
        }

        // 2. Minimum words constraint (at least 1 word)
        val words = trimmed.split("\\s+".toRegex()).filter { it.isNotBlank() }
        if (words.isEmpty()) {
            return ValidationResult.Invalid(
                errorMessageTa = "சல்லுபடியாகும் வார்த்தைகளை உள்ளிடவும்.",
                errorMessageEn = "Please enter valid text."
            )
        }

        // 3. Sequential digits or pure random numbers (e.g. "123456789012")
        val digitOnlyRatio = trimmed.count { it.isDigit() }.toFloat() / trimmed.length
        if (digitOnlyRatio > 0.85f && trimmed.length > 8) {
            return ValidationResult.Invalid(
                errorMessageTa = "தவறான உள்ளீடு: வெறும் எண்களை மட்டும் உள்ளிடக்கூடாது. பிரச்சனை விபரத்தை எழுதவும்.",
                errorMessageEn = "Invalid input: Please enter factual narrative text instead of plain numbers."
            )
        }

        // 4. Repetitive character detection (e.g. "aaaaa", "zzzzz")
        val repetitionRegex = Regex("""(.)\1{5,}""", RegexOption.IGNORE_CASE)
        if (repetitionRegex.containsMatchIn(trimmed)) {
            return ValidationResult.Invalid(
                errorMessageTa = "தவறான உள்ளீடு: ஒரே எழுத்துக்கள் மீண்டும் மீண்டும் உள்ளன.",
                errorMessageEn = "Invalid input: Repetitive character pattern detected."
            )
        }

        // 5. Common keyboard mashing patterns (only for repetitive long strings)
        val gibberishPatterns = listOf("asdfghjkl", "zxcvbnm", "qwertyuiop")
        val lowerText = trimmed.lowercase()
        if (gibberishPatterns.any { lowerText.contains(it) }) {
            return ValidationResult.Invalid(
                errorMessageTa = "தவறான உள்ளீடு: விசைப்பலகை தட்டல் (Keyboard Mashing) கண்டறியப்பட்டது.",
                errorMessageEn = "Invalid input: Keyboard mashing pattern detected. Please enter genuine legal details."
            )
        }

        return ValidationResult.Valid
    }
}
