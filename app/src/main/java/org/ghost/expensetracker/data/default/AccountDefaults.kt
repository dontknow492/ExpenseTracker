package org.ghost.expensetracker.data.default

import androidx.annotation.StringRes
import org.ghost.expensetracker.R
import org.ghost.expensetracker.core.utils.getSafeDefaultCurrencyCode

data class DefaultAccount(
    @param: StringRes val nameResId: Int,
    @param: StringRes val descriptionResId: Int,
    val balance: Double,
    val currency: String,
    val isDefault: Boolean
)

object AccountDefaults {
    val defaultAccount = DefaultAccount(
        nameResId = R.string.wallet,
        descriptionResId = R.string.default_wallet_description,
        balance = 0.0,
        currency = getSafeDefaultCurrencyCode(), // Assuming this is a context-free utility
        isDefault = true
    )
}