package org.ghost.expensetracker.data.useCase

import org.ghost.expensetracker.data.models.Due
import org.ghost.expensetracker.data.repository.LedgerRepository
import javax.inject.Inject

class AddDueUseCase @Inject constructor(
    private val ledgerRepository: LedgerRepository
) {
    suspend operator fun invoke(due: Due): Result<Long> = runCatching {
        // --- 1. Validate the Inputs ---
        require(due.name.isNotBlank()) {
            "Due name cannot be empty."
        }
        require(due.amount > 0) {
            "Due amount must be a positive number."
        }


        // Enforce that a due can be linked to an account OR a card, but not both.
        // It's valid for both to be null if it's a general bill not tied to a payment source yet.
        require(!(due.accountId != null && due.cardId != null)) {
            "Due cannot be linked to both an account and a card."
        }

        // Validate recurrence rules
        if (due.isRecurring) {
            require(due.recurrenceInterval != null && due.recurrenceUnit != null) {
                "Recurring dues must have a recurrence interval and unit."
            }
            require(due.recurrenceInterval > 0) {
                "Recurrence interval must be a positive number."
            }
        }

        // --- 2. Call the Repository ---
        ledgerRepository.addDue(due)
    }
}