package org.ghost.expensetracker.data.useCase.profile

import kotlinx.coroutines.flow.firstOrNull
import org.ghost.expensetracker.core.exceptions.AccountAlreadyExistsException
import org.ghost.expensetracker.data.models.Account
import org.ghost.expensetracker.data.repository.AccountRepository
import javax.inject.Inject

class AddAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    /**
     * The single business action of creating a new account (wallet, card, etc.).
     * It handles validation before attempting to insert the account.
     * @param account The account object to be created.
     * @return The official Kotlin Result wrapper containing the new account's ID on success.
     */
    suspend operator fun invoke(account: Account): Result<Long> = runCatching {
        // --- 1. Validate the Inputs (This is the core job of the Use Case) ---
        require(account.name.isNotBlank()) {
            "Account name cannot be empty."
        }
        // A sensible business rule is that an account cannot be created with a negative balance.
        require(account.balance >= 0) {
            "Initial account balance cannot be negative."
        }

        val existingAccount = accountRepository.getAccountByProfileAndName(
            profileId = account.profileOwnerId,
            name = account.name
        ).firstOrNull()

        if (existingAccount != null) {
            throw AccountAlreadyExistsException("Account already exists.")
        }


        val displayOrder = accountRepository.getAccountsCount()

        // --- 2. Call the Repository ---
        // The last expression in a runCatching block is what gets returned on success.
        // We call the generic createAccount method and return the new ID.
        accountRepository.createAccount(account.copy(displayOrder = displayOrder))
    }
}