package com.justra.app.util

import com.justra.app.domain.model.LanguagePreference

object BilingualStrings {
    fun t(key: String, lang: LanguagePreference): String {
        return LocalizationManager.getString(key, lang)
    }

    fun term(termId: String): LegalGlossaryTerm? {
        return LocalizationManager.getLegalTerm(termId)
    }
}


