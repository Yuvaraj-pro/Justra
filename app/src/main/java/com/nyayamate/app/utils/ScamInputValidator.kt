package com.nyayamate.app.utils

import android.util.Patterns

enum class InputType {
    VALID_URL,
    SUSPICIOUS_PLAIN_TEXT,
    PHONE_NUMBER,
    INVALID_GIBBERISH
}

object ScamInputValidator {

    // Detects whether a string is random typing (no vowels, repeated consonants, lack of words)
    private val gibberishRegex = Regex("""^(?:[^aeiouAEIOU\s\d]{5,}|[a-zA-Z\d]{25,})$""")

    fun determineInputType(input: String): InputType {
        val trimmed = input.trim()

        if (trimmed.length < 5 || gibberishRegex.matches(trimmed)) {
            return InputType.INVALID_GIBBERISH
        }

        // Check if string contains or is a valid URL
        val hasUrl = Patterns.WEB_URL.matcher(trimmed).find() || 
                     trimmed.startsWith("http://") || 
                     trimmed.startsWith("https://") ||
                     trimmed.contains(".com") || 
                     trimmed.contains(".xyz") ||
                     trimmed.contains(".top") || 
                     trimmed.contains(".in")

        if (hasUrl) {
            return InputType.VALID_URL
        }

        // Numeric helpline / shortcode checks
        if (trimmed.matches(Regex("""^\+?\d{8,13}$"""))) {
            return InputType.PHONE_NUMBER
        }

        // General message or SMS text
        return InputType.SUSPICIOUS_PLAIN_TEXT
    }
}
