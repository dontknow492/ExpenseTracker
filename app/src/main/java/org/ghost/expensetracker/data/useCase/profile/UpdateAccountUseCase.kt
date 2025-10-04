package org.ghost.expensetracker.data.useCase.profile

import org.ghost.expensetracker.data.models.Account
import org.ghost.expensetracker.data.repository.AccountRepository
import javax.inject.Inject

class UpdateAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(account: Account): Boolean {
        return accountRepository.updateAccount(account)
    }

    suspend operator fun invoke(accounts: List<Account>) {
        accountRepository.updateAccounts(accounts)
    }
}