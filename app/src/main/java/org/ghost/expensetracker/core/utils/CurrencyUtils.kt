package org.ghost.expensetracker.core.utils

import java.text.NumberFormat
import java.util.Currency

object CurrencyUtils {
    fun formatter(currency: String): NumberFormat {
        val formatter = NumberFormat.getCurrencyInstance()
        try {
            // 2. Set the specific currency from your account object.
            formatter.currency = Currency.getInstance(currency)
        } catch (e: IllegalArgumentException) {
            // Handle cases where account.currency might be an invalid code.
            // Fallback to a default, like USD.
            formatter.currency = Currency.getInstance("USD")
        }
        // 3. Return the fully configured formatter.
        return formatter
    }

    fun formattedAmount(amount: Double, currency: String): String {
        val formatter = formatter(currency)
        val formattedBalance = formatter.format(amount)
        return formattedBalance
    }

//    fun covertToCurrency(amount: Double, from: String, to: String): Double {
//        if (from == to) return amount
//
//    }


}