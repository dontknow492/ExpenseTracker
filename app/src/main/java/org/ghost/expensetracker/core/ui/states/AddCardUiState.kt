package org.ghost.expensetracker.core.ui.states

import androidx.compose.ui.graphics.Color
import org.ghost.expensetracker.core.enums.CardType

data class AddCardUiState(
    // Input fields
    val holderName: String = "",
    val expirationDate: String = "",
    val cardLastFourDigits: String = "",
    val cardType: String = CardType.CREDIT.type,
    val cardCompany: String = "",
    val color: Color? = null,
    // Validation errors
    val isHolderNameValid: Boolean = true,
    val isCardLastFourDigitsValid: Boolean = true,
    val isCardCompanyValid: Boolean = true,
    val isExpirationDateValid: Boolean = true,

    // State of the save operation
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCardSaved: Boolean = false
)