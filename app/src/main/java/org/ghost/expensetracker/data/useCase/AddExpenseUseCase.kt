package org.ghost.expensetracker.data.useCase

import kotlinx.coroutines.flow.firstOrNull
import org.ghost.expensetracker.core.exceptions.InsufficientBalanceException
import org.ghost.expensetracker.core.exceptions.InvalidAmountException
import org.ghost.expensetracker.core.exceptions.InvalidNameException
import org.ghost.expensetracker.core.exceptions.InvalidSourceOfFundsException
import org.ghost.expensetracker.core.exceptions.ItemNotFoundException
import org.ghost.expensetracker.data.models.Expense
import org.ghost.expensetracker.data.repository.AccountRepository
import org.ghost.expensetracker.data.repository.LedgerRepository
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val ledgerRepository: LedgerRepository,
    private val accountRepository: AccountRepository,
    // Assuming a CardRepository exists for card-specific operations
) {
    /**
     * Attempts to add a new expense.
     * This is a complete business transaction that validates the source of funds,
     * updates the balance, and inserts the expense record.
     * @return The official Kotlin Result wrapper indicating success or a specific failure reason.
     */
    suspend operator fun invoke(expense: Expense): Result<Unit> = runCatching {
        // --- 1. Validate the input: Enforce the "Single Source of Funds" rule ---
        val hasAccount = expense.accountId != null
        val hasCard = expense.cardId != null

        if (expense.title.isBlank() || expense.title.length > 50) {
            throw InvalidNameException("Expense title must be between 1 and 50 characters.")
        }

        if (expense.amount <= 0.0) {
            throw InvalidAmountException("Expense amount must be greater than 0.")
        }

        if (hasAccount && hasCard) {
            throw InvalidSourceOfFundsException("Expense cannot have both an account and a card as a source.")
        }
        if (!hasAccount && !hasCard) {
            throw InvalidSourceOfFundsException("Expense must have a source of funds (account or card).")
        }

        // --- 2. Handle the two separate logic paths using a 'when' statement ---
        when {
            hasAccount -> handleAccountExpense(expense)
            hasCard -> handleCardExpense(expense)
            // This case should be unreachable due to the checks above
            else -> throw IllegalStateException("Invalid expense state.")
        }
    }

    private suspend fun handleAccountExpense(expense: Expense) {
        // This helper function now returns Unit on success or throws an exception on failure.
        val account = accountRepository.getAccountById(expense.accountId!!).firstOrNull()
            ?: throw ItemNotFoundException("Source account not found.")

        val updatedAccount = if (expense.isSend) {
            if (account.balance < expense.amount) {
                throw InsufficientBalanceException("Account balance is insufficient.")
            }
            // Perform the updates as a single logical unit
            account.copy(balance = account.balance - expense.amount)
        } else {
            account.copy(balance = account.balance + expense.amount)
        }


        accountRepository.updateAccount(updatedAccount)
        ledgerRepository.addExpense(expense)
    }

    private suspend fun handleCardExpense(expense: Expense) {
        // This helper function now returns Unit on success or throws an exception on failure.
        val card = accountRepository.getCardById(expense.cardId!!).firstOrNull()
            ?: throw ItemNotFoundException("Source card not found.")


        val updatedCard = if (expense.isSend) {
            if (card.balance < expense.amount) {
                throw InsufficientBalanceException("Card balance is insufficient.")
            }
            // Perform the updates as a single logical unit
            card.copy(balance = card.balance - expense.amount)
        } else {
            card.copy(balance = card.balance + expense.amount)
        }

        // Assuming your Card model has a 'balance' property
        accountRepository.updateCard(updatedCard)
        ledgerRepository.addExpense(expense)
    }
}