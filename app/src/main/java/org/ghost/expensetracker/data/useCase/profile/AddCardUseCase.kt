package org.ghost.expensetracker.data.useCase.profile

import kotlinx.coroutines.flow.firstOrNull
import org.ghost.expensetracker.core.exceptions.CardAlreadyExistsException
import org.ghost.expensetracker.core.exceptions.InvalidCredentialsException
import org.ghost.expensetracker.core.exceptions.InvalidNameException
import org.ghost.expensetracker.data.models.Card
import org.ghost.expensetracker.data.repository.AccountRepository
import javax.inject.Inject

class AddCardUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(card: Card): Result<Long> = runCatching {
        // --- 1. Validate the Inputs (The core job of the Use Case) ---
        require(card.holderName.isNotBlank()) {
            throw InvalidNameException("Card holder name cannot be empty.")
        }
        require(card.cardLastFourDigits in 1000..9999) {

            throw InvalidCredentialsException("Invalid card number.")
        }
        require(card.cardCompany.isNotBlank()) {
            throw IllegalStateException("Card company cannot be empty.")
        }

        val existingCard = accountRepository.getCardByProfileAndCompanyAndLastFourDigits(
            profileId = card.profileOwnerId,
            company = card.cardCompany,
            lastFourDigits = card.cardLastFourDigits
        ).firstOrNull()

        if (existingCard != null) {
            throw CardAlreadyExistsException("Card already exists.")
        }

        val displayOrder = accountRepository.getCardsCount()

        // --- 2. Call the Correct Repository Method ---
        // The last expression in a runCatching block is what gets returned on success.
        accountRepository.addCard(card.copy(displayOrder = displayOrder))
    }
}