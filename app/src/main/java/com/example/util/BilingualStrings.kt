package com.example.util

import com.example.domain.model.LanguagePreference

object BilingualStrings {
    fun t(key: String, lang: LanguagePreference): String {
        return LocalizationManager.getString(key, lang)
    }

    fun term(termId: String): LegalGlossaryTerm? {
        return LocalizationManager.getLegalTerm(termId)
    }
}


