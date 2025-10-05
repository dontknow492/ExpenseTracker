package org.ghost.expensetracker.core.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import org.ghost.expensetracker.data.models.Language

object LocaleHelper {
    fun setLocale(language: Language) {
        // Create a new LocaleList with the desired language
        val localeList = LocaleListCompat.forLanguageTags(language.code)

        // Set the app-wide locale
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}