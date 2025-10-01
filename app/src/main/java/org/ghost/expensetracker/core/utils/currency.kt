package org.ghost.expensetracker.core.utils

import android.icu.util.Currency
import java.util.Locale

fun getSafeDefaultCurrencyCode(): String {
    return try {
        Currency.getInstance(Locale.getDefault()).currencyCode
    } catch (e: IllegalArgumentException) {
        // This can happen on locales without a currency.
        // Log this exception if needed: Log.w("Currency", "Could not find currency for locale", e)
        "USD"
    }
}