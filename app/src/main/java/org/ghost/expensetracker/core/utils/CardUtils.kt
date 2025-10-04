package org.ghost.expensetracker.core.utils

import androidx.annotation.DrawableRes
import org.ghost.expensetracker.R

object CardUtils {
    /**
     * Returns the drawable resource ID for a given card company name.
     * The matching is case-insensitive.
     *
     * @param companyName The name of the card company (e.g., "Visa", "Mastercard").
     * @return The resource ID for the matching icon, or a default card icon if no match is found.
     */
    @DrawableRes
    fun getCardIcon(companyName: String?): Int {
        // Store the lowercase name in a variable for clean checks
        val name = companyName?.lowercase() ?: return R.drawable.debit_card

        return when {
            // More specific names first
            name.contains("american express") -> R.drawable.american_express
            name.contains("mastercard") -> R.drawable.master_card
            name.contains("discover") -> R.drawable.discover

            // Less specific names later
            name.contains("amex") -> R.drawable.american_express
            name.contains("visa") -> R.drawable.visa
            name.contains("chase") -> R.drawable.chase_logo
            name.contains("paypal") -> R.drawable.paypal
            name.contains("rupay") -> R.drawable.rupay

            else -> R.drawable.debit_card // Default icon
        }
    }
}